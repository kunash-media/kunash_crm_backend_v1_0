package com.crm.repository;

import com.crm.entity.NotificationStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationStateEntity, Long> {

    List<NotificationStateEntity> findByAdminId(String adminId);

    boolean existsByAdminIdAndFingerprint(String adminId, String fingerprint);

}