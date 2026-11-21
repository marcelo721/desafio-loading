package br.ufc.loading.requests.infrastructure.adapter.out.persistence.repository;

import br.ufc.loading.requests.infrastructure.adapter.out.persistence.entity.ServiceRequestJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ServiceRequestJpaRepository
        extends JpaRepository<ServiceRequestJpaEntity, UUID>, JpaSpecificationExecutor<ServiceRequestJpaEntity> {
}
