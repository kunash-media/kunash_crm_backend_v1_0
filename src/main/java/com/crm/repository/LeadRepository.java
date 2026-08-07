package com.crm.repository;

import com.crm.entity.LeadEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<LeadEntity, Long> {

    Optional<LeadEntity> findByLeadStrIdAndDeletedLeadFalse(String leadStrId);

    Optional<LeadEntity> findByLeadPrimeIdAndDeletedLeadFalse(Long leadPrimeId);

    Page<LeadEntity> findByDeletedLeadFalse(Pageable pageable);

    boolean existsByPhoneAndDeletedLeadFalse(String phone);

    boolean existsByEmailAndDeletedLeadFalse(String email);

    boolean existsByPhoneAndDeletedLeadFalseAndLeadPrimeIdNot(String phone, Long leadPrimeId);

    boolean existsByEmailAndDeletedLeadFalseAndLeadPrimeIdNot(String email, Long leadPrimeId);

    Optional<LeadEntity> findByPhoneAndDeletedLeadFalse(String phone);

    @Query("select l.phone from LeadEntity l where l.deletedLead = false and l.phone is not null")
    List<String> findAllActivePhones();

    Page<LeadEntity> findByDeletedLeadFalseAndLeadConvertedFalse(Pageable pageable);

    List<LeadEntity> findByLeadPrimeIdIn(List<Long> leadPrimeIds);

    @org.springframework.data.jpa.repository.Query(
            "SELECT FUNCTION('DATE_FORMAT', l.createdAt, '%Y-%m') as monthKey, COUNT(l) as cnt " +
                    "FROM LeadEntity l " +
                    "WHERE l.deletedLead = false AND l.createdAt >= :fromDate " +
                    "GROUP BY FUNCTION('DATE_FORMAT', l.createdAt, '%Y-%m')"
    )
    List<Object[]> countLeadsGroupedByMonth(@org.springframework.data.repository.query.Param("fromDate") java.time.LocalDateTime fromDate);


    @Query("SELECT COUNT(l) FROM LeadEntity l WHERE l.deletedLead = false " +
            "AND l.leadConverted = false AND l.leadOutcome IS NULL " +
            "AND l.followUpDate = :date AND l.followupStatus <> 'done'")
    long countTodayFollowups(@Param("date") LocalDate date);

    @Query("SELECT COUNT(l) FROM LeadEntity l WHERE l.deletedLead = false " +
            "AND l.leadConverted = false AND l.leadOutcome IS NULL " +
            "AND l.followUpDate IS NOT NULL")
    long countTotalFollowups();

    long countByDeletedLeadFalse();

    long countByFollowUpDateAndFollowupStatusNotAndDeletedLeadFalse(LocalDate followUpDate, String followupStatus);

    long countByFollowUpDateIsNotNullAndDeletedLeadFalse();

    long countByLeadConvertedTrueAndDeletedLeadFalse();

    long countByLeadOutcomeAndDeletedLeadFalse(String leadOutcome);

    Page<LeadEntity> findByLeadOutcomeAndDeletedLeadFalse(String leadOutcome, Pageable pageable);


    // lead search suggestion
    @Query(
            "SELECT l FROM LeadEntity l WHERE l.deletedLead = false AND (" +
                    "LOWER(l.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                    "LOWER(l.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                    "l.phone LIKE CONCAT('%', :query, '%') OR " +
                    "LOWER(l.company) LIKE LOWER(CONCAT('%', :query, '%'))) " +
                    "ORDER BY l.updatedAt DESC"
    )
    List<LeadEntity> searchLeadsForSuggestion(
            @Param("query") String query,
            Pageable pageable);
}