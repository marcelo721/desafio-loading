package br.ufc.loading.requests.infrastructure.adapter.out.persistence.adapter;

import br.ufc.loading.requests.application.model.PageQuery;
import br.ufc.loading.requests.application.model.PageResult;
import br.ufc.loading.requests.application.port.out.AuditLogPort;
import br.ufc.loading.requests.domain.model.AuditEntry;
import br.ufc.loading.requests.infrastructure.adapter.out.persistence.entity.AuditLogJpaEntity;
import br.ufc.loading.requests.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;
import br.ufc.loading.requests.infrastructure.adapter.out.persistence.repository.AuditLogJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class AuditLogPersistenceAdapter implements AuditLogPort {

    private final AuditLogJpaRepository repository;

    public AuditLogPersistenceAdapter(AuditLogJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditEntry save(AuditEntry entry) {
        return PersistenceMapper.toDomain(repository.save(PersistenceMapper.toEntity(entry)));
    }

    @Override
    public PageResult<AuditEntry> findAll(PageQuery pageQuery) {
        Sort sort = Sort.by(Sort.Direction.DESC, "occurredAt").and(Sort.by(Sort.Direction.ASC, "id"));
        Page<AuditLogJpaEntity> page = repository.findAll(PageRequest.of(pageQuery.page(), pageQuery.size(), sort));

        return new PageResult<>(
                page.getContent().stream().map(PersistenceMapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
