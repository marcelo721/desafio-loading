package com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.out.persistence.repository;

import com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.out.persistence.entity.AuditLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogJpaRepository extends JpaRepository<AuditLogJpaEntity, UUID> {
}
