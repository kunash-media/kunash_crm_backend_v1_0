package com.crm.service.serviceImple;

import com.crm.dto.stats.ReasonBucketRequestDto;
import com.crm.entity.LeadEntity;
import com.crm.entity.ReasonBucketEntity;
import com.crm.dto.stats.BucketCountDto;
import com.crm.dto.stats.ReasonReportResponseDto;
import com.crm.dto.stats.TrendPointDto;
import com.crm.enum_status.ReportGranularity;
import com.crm.repository.LeadRepository;
import com.crm.repository.ReasonBucketRepository;
import com.crm.util.LeadReportSpecifications;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LeadReasonReportService {

    private final LeadRepository leadRepository;
    private final ReasonBucketRepository bucketRepository;
    private final TextBucketMatcherService matcherService;

    public LeadReasonReportService(LeadRepository leadRepository,
                                   ReasonBucketRepository bucketRepository,
                                   TextBucketMatcherService matcherService) {
        this.leadRepository = leadRepository;
        this.bucketRepository = bucketRepository;
        this.matcherService = matcherService;
    }

    public ReasonReportResponseDto getLostReasonReport(
            String source, Long staffPrimeId, String requirementCategory,
            LocalDate fromDate, LocalDate toDate, ReportGranularity granularity) {

        var spec = LeadReportSpecifications.build(
                "lost", source, staffPrimeId, requirementCategory, fromDate, toDate);
        List<LeadEntity> leads = leadRepository.findAll(spec);

        List<ReasonBucketEntity> buckets = bucketRepository
                .findByActiveTrueAndApplicableToIn(List.of("LOST", "BOTH"));

        return buildReport(leads, buckets, granularity, LeadEntity::getLostReason, LeadEntity::getUpdatedAt);
    }

    public ReasonReportResponseDto getWonReasonReport(
            String source, Long staffPrimeId, String requirementCategory,
            LocalDate fromDate, LocalDate toDate, ReportGranularity granularity) {

        var spec = LeadReportSpecifications.build(
                "won", source, staffPrimeId, requirementCategory, fromDate, toDate);
        List<LeadEntity> leads = leadRepository.findAll(spec);

        List<ReasonBucketEntity> buckets = bucketRepository
                .findByActiveTrueAndApplicableToIn(List.of("WON", "BOTH"));

        return buildReport(leads, buckets, granularity, this::concatWonText, LeadEntity::getUpdatedAt);
    }

    public List<ReasonBucketEntity> getAllBuckets() {
        return bucketRepository.findAll();
    }

    private String concatWonText(LeadEntity lead) {
        StringBuilder sb = new StringBuilder();
        if (lead.getNotes() != null) sb.append(lead.getNotes()).append(" ");
        if (lead.getFollowups() != null) {
            for (var f : lead.getFollowups()) {
                if (Boolean.TRUE.equals(f.getDeletedFollowup())) continue;
                if (f.getFollowupNotes() != null) sb.append(f.getFollowupNotes()).append(" ");
            }
        }
        return sb.toString();
    }

    private ReasonReportResponseDto buildReport(
            List<LeadEntity> leads, List<ReasonBucketEntity> buckets,
            ReportGranularity granularity,
            Function<LeadEntity, String> textExtractor,
            Function<LeadEntity, LocalDateTime> dateExtractor) {

        Map<String, Long> overallCounts = new LinkedHashMap<>();
        Map<String, Map<String, Long>> trendMap = new TreeMap<>();

        for (LeadEntity lead : leads) {
            String text = textExtractor.apply(lead);
            List<String> matched = matcherService.matchBuckets(text, buckets);
            String period = periodLabel(dateExtractor.apply(lead), granularity);

            for (String bucketName : matched) {
                overallCounts.merge(bucketName, 1L, Long::sum);
                trendMap.computeIfAbsent(period, k -> new LinkedHashMap<>())
                        .merge(bucketName, 1L, Long::sum);
            }
        }

        List<BucketCountDto> breakdown = overallCounts.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(e -> new BucketCountDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        List<TrendPointDto> trend = trendMap.entrySet().stream()
                .map(e -> new TrendPointDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return new ReasonReportResponseDto(leads.size(), breakdown, trend);
    }

    private String periodLabel(LocalDateTime dt, ReportGranularity g) {
        if (dt == null) return "unknown";
        switch (g) {
            case WEEKLY:
                WeekFields wf = WeekFields.ISO;
                int week = dt.get(wf.weekOfWeekBasedYear());
                return dt.getYear() + "-W" + String.format("%02d", week);
            case QUARTERLY:
                int q = (dt.getMonthValue() - 1) / 3 + 1;
                return dt.getYear() + "-Q" + q;
            case HALF_YEARLY:
                int h = dt.getMonthValue() <= 6 ? 1 : 2;
                return dt.getYear() + "-H" + h;
            case YEARLY:
                return String.valueOf(dt.getYear());
            case MONTHLY:
            default:
                return dt.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
    }

    public ReasonBucketEntity createBucket(ReasonBucketRequestDto dto) {
        if (dto.getBucketName() == null || dto.getBucketName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bucketName is required");
        }
        ReasonBucketEntity bucket = new ReasonBucketEntity();
        bucket.setBucketName(dto.getBucketName().trim());
        bucket.setApplicableTo(dto.getApplicableTo() != null ? dto.getApplicableTo() : "BOTH");
        bucket.setKeywords(dto.getKeywords());
        bucket.setActive(dto.getActive() != null ? dto.getActive() : true);
        return bucketRepository.save(bucket);
    }

    public ReasonBucketEntity updateBucket(Long bucketPrimeId, ReasonBucketRequestDto dto) {
        ReasonBucketEntity bucket = bucketRepository.findById(bucketPrimeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bucket not found: " + bucketPrimeId));

        if (dto.getBucketName() != null && !dto.getBucketName().isBlank()) {
            bucket.setBucketName(dto.getBucketName().trim());
        }
        if (dto.getApplicableTo() != null) {
            bucket.setApplicableTo(dto.getApplicableTo());
        }
        if (dto.getKeywords() != null) {
            bucket.setKeywords(dto.getKeywords());
        }
        if (dto.getActive() != null) {
            bucket.setActive(dto.getActive());
        }
        return bucketRepository.save(bucket);
    }

    public ReasonBucketEntity toggleBucketActive(Long bucketPrimeId) {
        ReasonBucketEntity bucket = bucketRepository.findById(bucketPrimeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bucket not found: " + bucketPrimeId));
        bucket.setActive(!Boolean.TRUE.equals(bucket.getActive()));
        return bucketRepository.save(bucket);
    }
}