package com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.out.persistence.adapter;
import com.marcelo721.desfio_loading_full__stack.application.model.*;
import com.marcelo721.desfio_loading_full__stack.application.port.out.ServiceRequestRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;
import com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.out.persistence.entity.ServiceRequestJpaEntity;
import com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;
import com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.out.persistence.repository.ServiceRequestJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ServiceRequestPersistenceAdapter implements ServiceRequestRepositoryPort {

    private final ServiceRequestJpaRepository repository;

    public ServiceRequestPersistenceAdapter(ServiceRequestJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public ServiceRequest save(ServiceRequest request) {
        ServiceRequestJpaEntity saved = repository.save(PersistenceMapper.toEntity(request));
        return PersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<ServiceRequest> findById(UUID id) {
        return repository.findById(id).map(PersistenceMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public PageResult<ServiceRequest> search(RequestSearchCriteria criteria, PageQuery pageQuery,
                                             RequestSortField sortField, SortDirection sortDirection) {
        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortField.property()).and(Sort.by(Sort.Direction.ASC, "id"));

        Page<ServiceRequestJpaEntity> page = repository.findAll(
                ServiceRequestSpecifications.matching(criteria),
                PageRequest.of(pageQuery.page(), pageQuery.size(), sort));

        return new PageResult<>(
                page.getContent().stream().map(PersistenceMapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    public long count(RequestSearchCriteria criteria) {
        return repository.count(ServiceRequestSpecifications.matching(criteria));
    }
}
