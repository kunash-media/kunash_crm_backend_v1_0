package com.crm.repository;

import com.crm.entity.ClientListEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientListRepository extends JpaRepository<ClientListEntity, Long> {

    Page<ClientListEntity> findByDeletedClientFalse(Pageable pageable);

    Optional<ClientListEntity> findByClientPrimeIdAndDeletedClientFalse(Long clientPrimeId);

    boolean existsBySourceLeadIdAndDeletedClientFalse(Long sourceLeadId);

    long countByDeletedClientFalse();

    long countByTypeAndDeletedClientFalse(String type);

    // ── Pending Payment Alerts ──
    List<ClientListEntity> findByPendingAmountGreaterThanAndDeletedClientFalseOrderByPendingAmountDesc(Double amount);

    long countByPendingAmountGreaterThanAndDeletedClientFalse(Double amount);
}