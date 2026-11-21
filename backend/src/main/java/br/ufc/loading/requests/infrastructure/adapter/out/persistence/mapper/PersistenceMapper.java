package br.ufc.loading.requests.infrastructure.adapter.out.persistence.mapper;

import br.ufc.loading.requests.domain.model.AuditEntry;
import br.ufc.loading.requests.domain.model.ServiceRequest;
import br.ufc.loading.requests.domain.model.StatusHistoryEntry;
import br.ufc.loading.requests.domain.model.User;
import br.ufc.loading.requests.infrastructure.adapter.out.persistence.entity.AuditLogJpaEntity;
import br.ufc.loading.requests.infrastructure.adapter.out.persistence.entity.ServiceRequestJpaEntity;
import br.ufc.loading.requests.infrastructure.adapter.out.persistence.entity.StatusHistoryJpaEntity;
import br.ufc.loading.requests.infrastructure.adapter.out.persistence.entity.UserJpaEntity;

public final class PersistenceMapper {

    private PersistenceMapper() {
    }

    public static ServiceRequestJpaEntity toEntity(ServiceRequest request) {
        return new ServiceRequestJpaEntity(
                request.getId(),
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                request.getPriority(),
                request.getStatus(),
                request.getOwnerId(),
                request.getOwnerName(),
                request.getCreatedAt(),
                request.getUpdatedAt());
    }

    public static ServiceRequest toDomain(ServiceRequestJpaEntity entity) {
        return ServiceRequest.restore(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getPriority(),
                entity.getStatus(),
                entity.getOwnerId(),
                entity.getOwnerName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(user.id(), user.name(), user.email(), user.passwordHash(),
                user.role(), user.createdAt());
    }

    public static User toDomain(UserJpaEntity entity) {
        return new User(entity.getId(), entity.getName(), entity.getEmail(), entity.getPasswordHash(),
                entity.getRole(), entity.getCreatedAt());
    }

    public static StatusHistoryJpaEntity toEntity(StatusHistoryEntry entry) {
        return new StatusHistoryJpaEntity(entry.id(), entry.requestId(), entry.fromStatus(), entry.toStatus(),
                entry.changedById(), entry.changedByName(), entry.changedAt());
    }

    public static StatusHistoryEntry toDomain(StatusHistoryJpaEntity entity) {
        return new StatusHistoryEntry(entity.getId(), entity.getRequestId(), entity.getFromStatus(),
                entity.getToStatus(), entity.getChangedBy(), entity.getChangedByName(), entity.getChangedAt());
    }

    public static AuditLogJpaEntity toEntity(AuditEntry entry) {
        return new AuditLogJpaEntity(entry.id(), entry.entityType(), entry.entityId(), entry.operation(),
                entry.userId(), entry.userName(), entry.occurredAt(), entry.details());
    }

    public static AuditEntry toDomain(AuditLogJpaEntity entity) {
        return new AuditEntry(entity.getId(), entity.getEntityType(), entity.getEntityId(),
                entity.getOperation(), entity.getUserId(), entity.getUserName(), entity.getOccurredAt(),
                entity.getDetails());
    }
}
