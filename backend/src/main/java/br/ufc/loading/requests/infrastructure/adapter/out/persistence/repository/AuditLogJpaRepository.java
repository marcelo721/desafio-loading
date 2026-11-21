package br.ufc.loading.requests.infrastructure.adapter.out.persistence.repository;

import br.ufc.loading.requests.infrastructure.adapter.out.persistence.entity.AuditLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogJpaRepository extends JpaRepository<AuditLogJpaEntity, UUID> {
}
