package com.crm.repository;

import com.crm.entity.ReasonBucketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReasonBucketRepository extends JpaRepository<ReasonBucketEntity, Long> {
    List<ReasonBucketEntity> findByActiveTrueAndApplicableToIn(List<String> applicableTo);
}