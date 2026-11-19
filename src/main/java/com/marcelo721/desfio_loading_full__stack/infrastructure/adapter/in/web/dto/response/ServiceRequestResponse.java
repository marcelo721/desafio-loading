package com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.in.web.dto.response;

import com.marcelo721.desfio_loading_full__stack.domain.enums.Priority;
import com.marcelo721.desfio_loading_full__stack.domain.enums.RequestStatus;

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
