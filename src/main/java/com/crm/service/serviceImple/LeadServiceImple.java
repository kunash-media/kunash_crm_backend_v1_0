package com.crm.service.serviceImple;

import com.crm.dto.request.LeadFollowupRequestDto;
import com.crm.dto.request.LeadRequestDto;
import com.crm.dto.response.LeadFollowupResponseDto;
import com.crm.dto.response.LeadResponseDto;
import com.crm.dto.stats.MonthlyLeadCountDto;
import com.crm.entity.LeadEntity;
import com.crm.entity.LeadFollowupEntity;
import com.crm.repository.LeadFollowupRepository;
import com.crm.repository.LeadRepository;
import com.crm.service.LeadService;
import com.crm.util.LeadPhoneBloomFilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class LeadServiceImple implements LeadService {

    private final Logger log = LoggerFactory.getLogger(LeadServiceImple.class);
    private final LeadRepository leadRepository;
    private final LeadFollowupRepository leadFollowupRepository;
    private final LeadPhoneBloomFilterService bloomFilterService;


    @Autowired
    public LeadServiceImple(LeadRepository leadRepository,
                            LeadFollowupRepository leadFollowupRepository, LeadPhoneBloomFilterService bloomFilterService) {
        this.leadRepository = leadRepository;
        this.leadFollowupRepository = leadFollowupRepository;
        this.bloomFilterService = bloomFilterService;
    }

    @Override
    @Transactional
    public LeadResponseDto createLead(LeadRequestDto dto, MultipartFile docFile) {
        if (leadRepository.existsByPhoneAndDeletedLeadFalse(dto.getPhone())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT, "A lead with this phone number already exists");
        }
        if (dto.getEmail() != null && leadRepository.existsByEmailAndDeletedLeadFalse(dto.getEmail())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT, "A lead with this email already exists");
        }

        LeadEntity entity = new LeadEntity();
        mapDtoToEntity(dto, entity);
        attachFile(entity, docFile);
        LeadEntity saved = leadRepository.save(entity);
        bloomFilterService.add(saved.getPhone());
        return mapEntityToResponse(saved);
    }

    @Override
    @Transactional
    public LeadResponseDto updateLead(Long leadPrimeId, LeadRequestDto dto, MultipartFile docFile) {
        LeadEntity entity = leadRepository.findByLeadPrimeIdAndDeletedLeadFalse(leadPrimeId)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + leadPrimeId));

        if (leadRepository.existsByPhoneAndDeletedLeadFalseAndLeadPrimeIdNot(dto.getPhone(), leadPrimeId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT, "A lead with this phone number already exists");
        }
        if (dto.getEmail() != null
                && leadRepository.existsByEmailAndDeletedLeadFalseAndLeadPrimeIdNot(dto.getEmail(), leadPrimeId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT, "A lead with this email already exists");
        }

        mapDtoToEntity(dto, entity);
        if (docFile != null && !docFile.isEmpty()) {
            attachFile(entity, docFile);
        }
        LeadEntity saved = leadRepository.save(entity);
        bloomFilterService.add(saved.getPhone());
        return mapEntityToResponse(saved);
    }

    @Override
    public LeadResponseDto getLeadByPrimeId(Long leadPrimeId) {
        LeadEntity entity = leadRepository.findByLeadPrimeIdAndDeletedLeadFalse(leadPrimeId)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + leadPrimeId));
        return mapEntityToResponse(entity);
    }

    @Override
    public LeadResponseDto getLeadByStrId(String leadStrId) {
        LeadEntity entity = leadRepository.findByLeadStrIdAndDeletedLeadFalse(leadStrId)
                .orElseThrow(() -> new RuntimeException("Lead not found with leadStrId: " + leadStrId));
        return mapEntityToResponse(entity);
    }

    @Override
    public Page<LeadResponseDto> getAllLeads(int page, int size) {
        long total = leadRepository.count();
        int effectiveSize = size;
        if ((long) page * size >= total && total > 0) {
            effectiveSize = (int) Math.max(size, total);
        }
        Pageable pageable = PageRequest.of(0, Math.max(effectiveSize, 1));
        Page<LeadEntity> pageResult = leadRepository.findByDeletedLeadFalseAndLeadConvertedFalse(pageable);
        return pageResult.map(this::mapEntityToResponse);
    }

    @Override
    @Transactional
    public LeadResponseDto convertLead(Long leadPrimeId) {
        LeadEntity entity = leadRepository.findByLeadPrimeIdAndDeletedLeadFalse(leadPrimeId)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + leadPrimeId));
        entity.setLeadConverted(true);
        leadRepository.save(entity);
        return mapEntityToResponse(entity);
    }

    @Override
    @Transactional
    public void deleteBulk(List<Long> leadPrimeIds) {
        List<LeadEntity> entities = leadRepository.findByLeadPrimeIdIn(leadPrimeIds);
        for (LeadEntity e : entities) {
            e.setDeletedLead(true);
        }
        leadRepository.saveAll(entities);
    }

    private static final String[] MONTH_LABELS = {
            "Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"
    };

    @Override
    public List<MonthlyLeadCountDto> getMonthlyLeadCounts(int monthsBack) {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDateTime fromDate = today.minusMonths(monthsBack - 1L).withDayOfMonth(1).atStartOfDay();

        // fetch actual counts from DB, keyed by "yyyy-MM"
        java.util.Map<String, Long> countsByMonth = new java.util.HashMap<>();
        for (Object[] row : leadRepository.countLeadsGroupedByMonth(fromDate)) {
            String monthKey = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            countsByMonth.put(monthKey, count);
        }

        // build a complete, ordered list — months with zero leads still appear (chart needs continuous x-axis)
        List<MonthlyLeadCountDto> result = new java.util.ArrayList<>();
        for (int i = monthsBack - 1; i >= 0; i--) {
            java.time.LocalDate monthStart = today.minusMonths(i).withDayOfMonth(1);
            String monthKey = monthStart.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
            String monthLabel = MONTH_LABELS[monthStart.getMonthValue() - 1];
            long count = countsByMonth.getOrDefault(monthKey, 0L);
            result.add(new MonthlyLeadCountDto(monthKey, monthLabel, monthStart.getYear(), count));
        }
        return result;
    }

    @Override
    @Transactional
    public void deleteLead(Long leadPrimeId) {
        LeadEntity entity = leadRepository.findByLeadPrimeIdAndDeletedLeadFalse(leadPrimeId)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + leadPrimeId));
        entity.setDeletedLead(true);
        leadRepository.save(entity);
    }

    @Override
    public byte[] getDocFileBytes(Long leadPrimeId) {
        return leadRepository.findByLeadPrimeIdAndDeletedLeadFalse(leadPrimeId)
                .map(LeadEntity::getDocFile)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + leadPrimeId));
    }

    @Override
    public String getDocFileType(Long leadPrimeId) {
        return leadRepository.findByLeadPrimeIdAndDeletedLeadFalse(leadPrimeId)
                .map(LeadEntity::getDocFileType)
                .orElse("application/octet-stream");
    }

    @Override
    public String getDocFileName(Long leadPrimeId) {
        return leadRepository.findByLeadPrimeIdAndDeletedLeadFalse(leadPrimeId)
                .map(LeadEntity::getDocFileName)
                .orElse("file");
    }

    @Override
    @Transactional
    public LeadResponseDto addFollowup(Long leadPrimeId, LeadFollowupRequestDto dto) {
        LeadEntity lead = leadRepository.findByLeadPrimeIdAndDeletedLeadFalse(leadPrimeId)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + leadPrimeId));

        LeadFollowupEntity followup = new LeadFollowupEntity();
        followup.setLead(lead);
        followup.setFollowupDate(dto.getFollowupDate());
        followup.setFollowupStatus(dto.getFollowupStatus());
        followup.setFollowupNotes(dto.getFollowupNotes());
        leadFollowupRepository.save(followup);

        resyncLeadFromLatestFollowup(lead);
        return mapEntityToResponse(lead);
    }

    @Override
    public List<LeadFollowupRequestDto> getFollowups(Long leadPrimeId) {
        return leadFollowupRepository.findByLead_LeadPrimeIdAndDeletedFollowupFalseOrderByFollowupDateDesc(leadPrimeId)
                .stream()
                .map(f -> {
                    LeadFollowupRequestDto d = new LeadFollowupRequestDto();
                    d.setFollowupDate(f.getFollowupDate());
                    d.setFollowupStatus(f.getFollowupStatus());
                    d.setFollowupNotes(f.getFollowupNotes());
                    return d;
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<LeadFollowupResponseDto> getFollowupsDetailed(Long leadPrimeId) {
        return leadFollowupRepository.findByLead_LeadPrimeIdAndDeletedFollowupFalseOrderByFollowupDateDesc(leadPrimeId)
                .stream()
                .map(f -> {
                    com.crm.dto.response.LeadFollowupResponseDto d = new com.crm.dto.response.LeadFollowupResponseDto();
                    d.setFollowupPrimeId(f.getFollowupPrimeId());
                    d.setFollowupDate(f.getFollowupDate());
                    d.setFollowupStatus(f.getFollowupStatus());
                    d.setFollowupNotes(f.getFollowupNotes());
                    d.setCreatedAt(f.getCreatedAt());
                    return d;
                })
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public LeadResponseDto updateFollowup(Long leadPrimeId, Long followupPrimeId, LeadFollowupRequestDto dto) {
        LeadEntity lead = leadRepository.findByLeadPrimeIdAndDeletedLeadFalse(leadPrimeId)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + leadPrimeId));

        LeadFollowupEntity followup = leadFollowupRepository.findById(followupPrimeId)
                .orElseThrow(() -> new RuntimeException("Followup not found with id: " + followupPrimeId));

        if (!followup.getLead().getLeadPrimeId().equals(leadPrimeId)) {
            throw new RuntimeException("Followup " + followupPrimeId + " does not belong to lead " + leadPrimeId);
        }
        if (Boolean.TRUE.equals(followup.getDeletedFollowup())) {
            throw new RuntimeException("Followup " + followupPrimeId + " is deleted and cannot be edited");
        }

        followup.setFollowupDate(dto.getFollowupDate());
        followup.setFollowupStatus(dto.getFollowupStatus());
        followup.setFollowupNotes(dto.getFollowupNotes());
        leadFollowupRepository.save(followup);

        resyncLeadFromLatestFollowup(lead);
        return mapEntityToResponse(lead);
    }

    @Override
    @Transactional
    public LeadResponseDto deleteFollowup(Long leadPrimeId, Long followupPrimeId) {
        LeadEntity lead = leadRepository.findByLeadPrimeIdAndDeletedLeadFalse(leadPrimeId)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + leadPrimeId));

        LeadFollowupEntity followup = leadFollowupRepository.findById(followupPrimeId)
                .orElseThrow(() -> new RuntimeException("Followup not found with id: " + followupPrimeId));

        if (!followup.getLead().getLeadPrimeId().equals(leadPrimeId)) {
            throw new RuntimeException("Followup " + followupPrimeId + " does not belong to lead " + leadPrimeId);
        }
        if (Boolean.TRUE.equals(followup.getDeletedFollowup())) {
            throw new RuntimeException("Followup " + followupPrimeId + " is already deleted");
        }

        followup.setDeletedFollowup(true);
        leadFollowupRepository.save(followup);

        resyncLeadFromLatestFollowup(lead);
        return mapEntityToResponse(lead);
    }

    @Override
    @Transactional
    public void updateLeadOutcome(Long leadPrimeId, String outcome, String lostReason) {
        log.info("Updating lead {} outcome to '{}'", leadPrimeId, outcome);

        LeadEntity lead = leadRepository.findById(leadPrimeId)
                .filter(l -> !Boolean.TRUE.equals(l.getDeletedLead()))
                .orElseThrow(() -> new NoSuchElementException("Lead not found with id: " + leadPrimeId));

        if ("lost".equalsIgnoreCase(outcome) && (lostReason == null || lostReason.isBlank())) {
            throw new IllegalArgumentException("lostReason is required when marking a lead as lost");
        }

        // "open" reverts a lost/won lead back to the active pipeline
        String normalizedOutcome = "open".equalsIgnoreCase(outcome) ? null : outcome;
        lead.setLeadOutcome(normalizedOutcome);
        lead.setLostReason("lost".equalsIgnoreCase(outcome) ? lostReason : null);
        leadRepository.save(lead);

        log.info("Lead {} outcome set to '{}' (reason={})", leadPrimeId, normalizedOutcome, lead.getLostReason());
    }

    @Override
    public Page<LeadResponseDto> getLeadsByOutcome(String outcome, int page, int size) {
        long total = leadRepository.countByLeadOutcomeAndDeletedLeadFalse(outcome);
        int effectiveSize = size;
        if ((long) page * size >= total && total > 0) {
            effectiveSize = (int) Math.max(size, total);
        }
        Pageable pageable = PageRequest.of(0, Math.max(effectiveSize, 1));
        Page<LeadEntity> pageResult = leadRepository.findByLeadOutcomeAndDeletedLeadFalse(outcome, pageable);
        return pageResult.map(this::mapEntityToResponse);
    }

    // Keeps LeadEntity.followupStatus/followUpDate as an accurate cache of the
    // latest surviving (non-deleted) child row — called after any followup
    // add/edit/delete so the parent snapshot never shows stale or misleading
    // data to the user.
    private void resyncLeadFromLatestFollowup(LeadEntity lead) {
        leadFollowupRepository
                .findFirstByLead_LeadPrimeIdAndDeletedFollowupFalseOrderByFollowupDateDesc(lead.getLeadPrimeId())
                .ifPresentOrElse(
                        latest -> {
                            lead.setFollowupStatus(latest.getFollowupStatus());
                            lead.setFollowUpDate(latest.getFollowupDate());
                        },
                        () -> {
                            // no active followups left — don't leave a misleading stale status
                            lead.setFollowupStatus(null);
                        }
                );
        leadRepository.save(lead);
    }

    @Override
    public LeadResponseDto checkPhoneExists(String phone) {
        if (phone == null || phone.trim().length() < 10) return null;

        // fast negative path — bloom filter guarantees "definitely not present"
        if (!bloomFilterService.mightExist(phone)) {
            return null;
        }

        // bloom filter said "maybe" — confirm against the DB (could be a false positive)
        return leadRepository.findByPhoneAndDeletedLeadFalse(phone)
                .map(this::mapEntityToResponse)
                .orElse(null);
    }

    private void attachFile(LeadEntity entity, MultipartFile docFile) {
        if (docFile != null && !docFile.isEmpty()) {
            try {
                entity.setDocFile(docFile.getBytes());
                entity.setDocFileName(docFile.getOriginalFilename());
                entity.setDocFileType(docFile.getContentType());
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to read uploaded file", e);
            }
        }
    }

    private void mapDtoToEntity(LeadRequestDto dto, LeadEntity entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setCompany(dto.getCompany());
        entity.setStatus(dto.getStatus());
        entity.setPriority(dto.getPriority());
        entity.setSource(dto.getSource());
        entity.setRequirementCategory(dto.getRequirementCategory());
        entity.setTags(dto.getTags());
        entity.setFollowUpDate(dto.getFollowUpDate());
        entity.setFollowupStatus(dto.getFollowupStatus());
        entity.setNotes(dto.getNotes());
        if (dto.getLeadConverted() != null) {
            entity.setLeadConverted(dto.getLeadConverted());
        }

    }

    private LeadResponseDto mapEntityToResponse(LeadEntity entity) {
        LeadResponseDto res = new LeadResponseDto();
        res.setLeadPrimeId(entity.getLeadPrimeId());
        res.setLeadStrId(entity.getLeadStrId());
        res.setFirstName(entity.getFirstName());
        res.setLastName(entity.getLastName());
        res.setEmail(entity.getEmail());
        res.setPhone(entity.getPhone());
        res.setCompany(entity.getCompany());
        res.setStatus(entity.getStatus());
        res.setPriority(entity.getPriority());
        res.setSource(entity.getSource());
        res.setRequirementCategory(entity.getRequirementCategory());
        res.setTags(entity.getTags());
        res.setFollowUpDate(entity.getFollowUpDate());
        res.setFollowupStatus(entity.getFollowupStatus());
        res.setNotes(entity.getNotes());
        res.setDeletedLead(entity.getDeletedLead());
        res.setLeadConverted(entity.getLeadConverted());
        res.setDocFileName(entity.getDocFileName());
        if (entity.getDocFile() != null && entity.getDocFile().length > 0) {
            res.setDocFileUrl("/api/lead/v1/" + entity.getLeadPrimeId() + "/docFile");
        }
        res.setFollowupCount((int) leadFollowupRepository.countByLead_LeadPrimeIdAndDeletedFollowupFalse(entity.getLeadPrimeId()));
        res.setCreatedAt(entity.getCreatedAt());
        res.setUpdatedAt(entity.getUpdatedAt());
        res.setLeadOutcome(entity.getLeadOutcome());
        res.setLostReason(entity.getLostReason());
        return res;
    }
}