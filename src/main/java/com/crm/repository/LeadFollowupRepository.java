package com.crm.repository;

import com.crm.entity.LeadFollowupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadFollowupRepository extends JpaRepository<LeadFollowupEntity, Long> {

    List<LeadFollowupEntity> findByLead_LeadPrimeIdAndDeletedFollowupFalseOrderByFollowupDateDesc(Long leadPrimeId);

    Optional<LeadFollowupEntity> findFirstByLead_LeadPrimeIdAndDeletedFollowupFalseOrderByFollowupDateDesc(Long leadPrimeId);

    long countByLead_LeadPrimeIdAndDeletedFollowupFalse(Long leadPrimeId);
}