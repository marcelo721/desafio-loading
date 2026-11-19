package com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.out.persistence.repository;

import com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.out.persistence.entity.StatusHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StatusHistoryJpaRepository extends JpaRepository<StatusHistoryJpaEntity, UUID> {

    List<StatusHistoryJpaEntity> findByRequestIdOrderByChangedAtAsc(UUID requestId);
}
