package com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.in.web.mapper;

import com.marcelo721.desfio_loading_full__stack.application.model.DashboardSummary;
import com.marcelo721.desfio_loading_full__stack.domain.model.AuditEntry;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;
import com.marcelo721.desfio_loading_full__stack.domain.model.StatusHistoryEntry;
import com.marcelo721.desfio_loading_full__stack.domain.model.User;
import com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.in.web.dto.response.*;
import com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.web.dto.response.*;

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
