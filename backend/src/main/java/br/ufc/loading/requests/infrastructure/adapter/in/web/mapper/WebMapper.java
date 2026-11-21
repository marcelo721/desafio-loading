package br.ufc.loading.requests.infrastructure.adapter.in.web.mapper;

import br.ufc.loading.requests.application.model.DashboardSummary;
import br.ufc.loading.requests.domain.model.AuditEntry;
import br.ufc.loading.requests.domain.model.ServiceRequest;
import br.ufc.loading.requests.domain.model.StatusHistoryEntry;
import br.ufc.loading.requests.domain.model.User;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response.AuditEntryResponse;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response.DashboardSummaryResponse;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response.ServiceRequestResponse;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response.StatusHistoryResponse;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response.UserResponse;

public final class WebMapper {

    private WebMapper() {
    }

    public static ServiceRequestResponse toResponse(ServiceRequest request) {
        return new ServiceRequestResponse(
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

    public static StatusHistoryResponse toResponse(StatusHistoryEntry entry) {
        return new StatusHistoryResponse(entry.id(), entry.fromStatus(), entry.toStatus(),
                entry.changedById(), entry.changedByName(), entry.changedAt());
    }

    public static DashboardSummaryResponse toResponse(DashboardSummary summary) {
        return new DashboardSummaryResponse(summary.total(), summary.open(), summary.inProgress(),
                summary.completed());
    }

    public static AuditEntryResponse toResponse(AuditEntry entry) {
        return new AuditEntryResponse(entry.id(), entry.entityType(), entry.entityId(), entry.operation(),
                entry.userId(), entry.userName(), entry.occurredAt(), entry.details());
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(user.id(), user.name(), user.email(), user.role());
    }
}
