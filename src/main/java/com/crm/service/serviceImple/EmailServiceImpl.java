package com.crm.service.serviceImple;

import com.crm.dto.request.EmailRequestDto;
import com.crm.dto.response.BulkEmailResponseDto;
import com.crm.dto.response.EmailResultDto;
import com.crm.entity.LeadEntity;
import com.crm.repository.LeadRepository;
import com.crm.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final LeadRepository leadRepository;

    // application.properties me se aayenge, na mile to sensible default use hoga
    @Value("${app.mail.from:${spring.mail.username}}")
    private String fromEmail;

    @Value("${app.company.name:Kunash Media}")
    private String companyName;

    // local logo file inside src/main/resources — embedded inline in the email (works for any recipient, no hosting needed)
    @Value("${app.company.logo-path:static/Images/kunash-logo.png}")
    private String companyLogoPath;

    // fallback: public/CDN hosted image URL, only used if companyLogoPath file isn't found
    @Value("${app.company.logo-url:}")
    private String companyLogoUrl;

    @Value("${app.company.phone:+91 8983448510}")
    private String companyPhone;

    @Value("${app.company.email:nikitajamdhade@gmail.com}")
    private String companyEmail;

    @Value("${app.company.website:https://kunashmedia.com/}")
    private String companyWebsite;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, LeadRepository leadRepository) {
        this.mailSender = mailSender;
        this.leadRepository = leadRepository;
    }

    @Override
    public BulkEmailResponseDto sendBulkEmails(EmailRequestDto request) {
        List<Long> leadIds = request.getLeadPrimeIds();
        if (leadIds == null || leadIds.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "No leads selected for email");
        }

        List<EmailResultDto> results = new ArrayList<>();
        List<LeadEntity> leads = leadRepository.findByLeadPrimeIdIn(leadIds);

        // jo id db me mili hi nahi, unhe bhi "failed" me report karo — silently skip mat karo
        for (Long id : leadIds) {
            boolean found = leads.stream().anyMatch(l -> l.getLeadPrimeId().equals(id));
            if (!found) {
                results.add(EmailResultDto.failure(id, null, null, "Lead not found"));
            }
        }

        for (LeadEntity lead : leads) {
            results.add(sendSingleInternal(lead, request));
        }

        long successCount = results.stream().filter(EmailResultDto::isSuccess).count();

        BulkEmailResponseDto response = new BulkEmailResponseDto();
        response.setTotalRequested(leadIds.size());
        response.setTotalSuccess((int) successCount);
        response.setTotalFailed(results.size() - (int) successCount);
        response.setResults(results);
        return response;
    }

    @Override
    public EmailResultDto sendSingleEmail(Long leadPrimeId, EmailRequestDto request) {
        LeadEntity lead = leadRepository.findByLeadPrimeIdAndDeletedLeadFalse(leadPrimeId).orElse(null);
        if (lead == null) {
            return EmailResultDto.failure(leadPrimeId, null, null, "Lead not found");
        }
        return sendSingleInternal(lead, request);
    }

    // ===================== INTERNAL =====================

    private EmailResultDto sendSingleInternal(LeadEntity lead, EmailRequestDto request) {
        String toEmail = lead.getEmail();
        String fullName = buildFullName(lead);

        if (toEmail == null || toEmail.trim().isEmpty()) {
            return EmailResultDto.failure(lead.getLeadPrimeId(), lead.getLeadStrId(), null,
                    "Lead has no email address");
        }
        if (request.getTemplateType() == null || request.getTemplateType().trim().isEmpty()) {
            return EmailResultDto.failure(lead.getLeadPrimeId(), lead.getLeadStrId(), toEmail,
                    "templateType is required (FOLLOWUP / MEET_REMINDER / NORMAL_REMINDER)");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(buildSubject(request, lead));

            // logo resolve: pehle local classpath file try karo (embed inline), warna configured URL use karo
            String logoSrc = null;
            ClassPathResource logoResource = null;
            if (hasText(companyLogoPath)) {
                ClassPathResource candidate = new ClassPathResource(companyLogoPath);
                if (candidate.exists()) {
                    logoResource = candidate;
                    logoSrc = "cid:companyLogo";
                }
            }
            if (logoSrc == null && hasText(companyLogoUrl)) {
                logoSrc = companyLogoUrl;
            }

            helper.setText(buildHtmlBody(request, lead, fullName, logoSrc), true);

            if (logoResource != null) {
                helper.addInline("companyLogo", logoResource);
            }

            mailSender.send(message);
            return EmailResultDto.success(lead.getLeadPrimeId(), lead.getLeadStrId(), toEmail);

        } catch (MessagingException | MailException ex) {
            return EmailResultDto.failure(lead.getLeadPrimeId(), lead.getLeadStrId(), toEmail,
                    ex.getMessage() != null ? ex.getMessage() : "Failed to send email");
        } catch (Exception ex) {
            return EmailResultDto.failure(lead.getLeadPrimeId(), lead.getLeadStrId(), toEmail,
                    "Unexpected error: " + ex.getMessage());
        }
    }

    private String buildFullName(LeadEntity lead) {
        String first = lead.getFirstName() != null ? lead.getFirstName() : "";
        String last = lead.getLastName() != null ? lead.getLastName() : "";
        String full = (first + " " + last).trim();
        return full.isEmpty() ? "there" : full;
    }

    private String requirementCategoryOrDefault(LeadEntity lead) {
        if (lead.getRequirementCategories() == null || lead.getRequirementCategories().isEmpty()) {
            return "General Enquiry";
        }
        return lead.getRequirementCategories().stream()
                .map(com.crm.entity.LeadRequirementCategoryEntity::getCategory)
                .filter(c -> c != null && !c.isBlank())
                .collect(java.util.stream.Collectors.joining(", "));
    }

    private String buildSubject(EmailRequestDto request, LeadEntity lead) {
        String category = requirementCategoryOrDefault(lead);
        String type = request.getTemplateType().toUpperCase();
        return switch (type) {
            case "MEET_REMINDER" -> companyName + " | Meeting Reminder - " + category;
            case "NORMAL_REMINDER" -> companyName + " | Reminder - " + category;
            default -> companyName + " | Follow-up - " + category;
        };
    }

    private String buildHtmlBody(EmailRequestDto request, LeadEntity lead, String fullName, String logoSrc) {
        String category = requirementCategoryOrDefault(lead);
        String type = request.getTemplateType().toUpperCase();

        String bodyContent = switch (type) {
            case "MEET_REMINDER" -> """
                <p>This is a reminder about your upcoming meeting with us regarding
                <strong>%s</strong>.</p>
                <table style="margin:16px 0; border-collapse:collapse; font-size:14px;">
                    <tr><td style="padding:4px 12px 4px 0; color:#777777;">Date</td><td><strong>%s</strong></td></tr>
                    <tr><td style="padding:4px 12px 4px 0; color:#777777;">Time</td><td><strong>%s</strong></td></tr>
                    %s
                </table>
                <p>Please make sure to be available on time. If you need to reschedule, contact us using the details below.</p>
                """.formatted(
                    escape(category),
                    escapeOrDash(request.getMeetingDate()),
                    escapeOrDash(request.getMeetingTime()),
                    buildMeetingLinkRow(request.getMeetingLink())
            );
            case "NORMAL_REMINDER" -> """
                <p>This is a quick reminder regarding your requirement:
                <strong>%s</strong>.</p>
                <p>%s</p>
                """.formatted(
                    escape(category),
                    hasText(request.getCustomMessage())
                            ? escape(request.getCustomMessage())
                            : "We wanted to check in with you. Please let us know if you have any questions or need further assistance."
            );
            default -> """
                <p>We are following up on your requirement:
                <strong>%s</strong>.</p>
                <p>%s</p>
                """.formatted(
                    escape(category),
                    hasText(request.getCustomMessage())
                            ? escape(request.getCustomMessage())
                            : "We would love to hear from you regarding the next steps. Reach out to us at your convenience using the details below."
            );
        };

        String logoHtml = hasText(logoSrc)
                ? "<img src=\"" + logoSrc + "\" alt=\"" + companyName + "\" style=\"height:40px; display:block; margin-bottom:10px;\"/>"
                : "";

        return """
            <!DOCTYPE html>
            <html>
            <body style="margin:0; padding:0; background:#f4f4f4; font-family:Arial, Helvetica, sans-serif;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f4f4; padding:24px 0;">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0" style="background:#ffffff; border-radius:8px; overflow:hidden;">
                                <tr>
                                    <td style="padding:20px 24px;">
                                        %s
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:24px; color:#333333; font-size:14px; line-height:1.6;">
                                        <p>Hi %s,</p>
                                        %s
                                        <p style="margin-top:24px;">Regards,<br/><strong>%s</strong></p>
                                        <hr style="border:none; border-top:1px solid #eeeeee; margin:20px 0;"/>
                                        <p style="text-align:center; margin:0 0 8px; font-size:11px; color:#999999;">Contact Support</p>
                                        <table cellpadding="0" cellspacing="0" style="margin:0 auto; font-size:12px; color:#666666;">
                                            <tr>
                                                <td>%s</td>
                                                <td style="padding:0 8px; color:#cccccc;">|</td>
                                                <td>%s</td>
                                                <td style="padding:0 8px; color:#cccccc;">|</td>
                                                <td>Visit : <a href="%s" style="color:#c1440e; text-decoration:none;">%s</a></td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="background:#f0f0f0; padding:12px 24px; font-size:11px; color:#999999; text-align:center;">
                                        This is an automated message from %s.
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(logoHtml, escape(fullName), bodyContent, companyName,
                escape(companyPhone), escape(companyEmail), companyWebsite, escape(companyWebsite),
                companyName);
    }

    private String buildMeetingLinkRow(String meetingLink) {
        if (!hasText(meetingLink)) return "";
        return "<tr><td style=\"padding:4px 12px 4px 0; color:#777777;\">Link</td><td><a href=\"" + meetingLink + "\">" + meetingLink + "</a></td></tr>";
    }

    private boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private String escapeOrDash(String s) {
        return hasText(s) ? escape(s) : "-";
    }

    // basic HTML escaping so lead data / notes can't break the email markup
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}