package com.crm.repository;

import com.crm.entity.GoogleAuthTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoogleAuthTokenRepository extends JpaRepository<GoogleAuthTokenEntity, Long> {
    Optional<GoogleAuthTokenEntity> findFirstByOrderByIdAsc();
}