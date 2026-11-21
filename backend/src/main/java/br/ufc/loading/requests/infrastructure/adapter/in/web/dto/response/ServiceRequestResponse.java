package br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response;

import br.ufc.loading.requests.domain.enums.Priority;
import br.ufc.loading.requests.domain.enums.RequestStatus;

import java.time.Instant;
import java.util.UUID;

public record ServiceRequestResponse(
        UUID id,
        String title,
        String description,
        String category,
        Priority priority,
        RequestStatus status,
        UUID ownerId,
        String ownerName,
        Instant createdAt,
        Instant updatedAt) {
}
