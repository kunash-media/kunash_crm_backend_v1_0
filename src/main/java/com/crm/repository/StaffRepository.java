package com.crm.repository;

import com.crm.entity.StaffEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long> {

    boolean existsByStaffEmail(String staffEmail);

    boolean existsByStaffMobile(String staffMobile);

    boolean existsByStaffEmailAndStaffPrimeIdNot(String staffEmail, Long staffPrimeId);

    boolean existsByStaffMobileAndStaffPrimeIdNot(String staffMobile, Long staffPrimeId);

    List<StaffEntity> findByStaffActiveTrue();

    Page<StaffEntity> findByStaffActiveTrue(org.springframework.data.domain.Pageable pageable);

    long countByStaffActiveTrue();
}