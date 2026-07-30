package com.crm.service.serviceImple;

import com.crm.dto.stats.LeadStatsResponse;
import com.crm.repository.LeadRepository;
import com.crm.service.StatCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class StatCardServiceImple implements StatCardService {

    private static final Logger log = LoggerFactory.getLogger(StatCardServiceImple.class);

    private final LeadRepository leadRepository;

    public StatCardServiceImple(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    @Override
    public LeadStatsResponse getLeadStats() {
        log.info("Fetching lead stat card counts");

        long totalLeads = leadRepository.countByDeletedLeadFalse();
        long todayFollowups = leadRepository.countTodayFollowups(LocalDate.now());
        long totalFollowups = leadRepository.countTotalFollowups();
        long won = leadRepository.countByLeadConvertedTrueAndDeletedLeadFalse();
        long lost = leadRepository.countByLeadOutcomeAndDeletedLeadFalse("lost");

        log.info("Lead stats — total={}, todayFollowups={}, totalFollowups={}, won={}, lost={}",
                totalLeads, todayFollowups, totalFollowups, won, lost);

        return new LeadStatsResponse(totalLeads, todayFollowups, totalFollowups, won, lost);
    }
}