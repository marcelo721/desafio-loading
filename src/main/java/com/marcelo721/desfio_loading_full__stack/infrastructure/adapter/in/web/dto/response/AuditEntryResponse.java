package com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.in.web.dto.response;



import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditOperation;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditedEntity;

import java.time.Instant;
import java.util.UUID;

public record AuditEntryResponse(
        UUID id,
        AuditedEntity entityType,
        UUID entityId,
        AuditOperation operation,
        UUID userId,
        String userName,
        Instant occurredAt,
        String details) {
}
