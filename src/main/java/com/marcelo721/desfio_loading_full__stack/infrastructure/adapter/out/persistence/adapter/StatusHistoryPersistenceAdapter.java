package com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.out.persistence.adapter;

import com.marcelo721.desfio_loading_full__stack.application.port.out.StatusHistoryRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.domain.model.StatusHistoryEntry;
import com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;
import com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.out.persistence.repository.StatusHistoryJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class StatusHistoryPersistenceAdapter implements StatusHistoryRepositoryPort {

    private final StatusHistoryJpaRepository repository;

    public StatusHistoryPersistenceAdapter(StatusHistoryJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public StatusHistoryEntry save(StatusHistoryEntry entry) {
        return PersistenceMapper.toDomain(repository.save(PersistenceMapper.toEntity(entry)));
    }

    @Override
    public List<StatusHistoryEntry> findByRequestId(UUID requestId) {
        return repository.findByRequestIdOrderByChangedAtAsc(requestId).stream()
                .map(PersistenceMapper::toDomain)
                .toList();
    }
}
