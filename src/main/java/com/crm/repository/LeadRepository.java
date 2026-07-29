package com.crm.repository;

import com.crm.entity.LeadEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}