package br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response;

import br.ufc.loading.requests.domain.enums.AuditOperation;
import br.ufc.loading.requests.domain.enums.AuditedEntity;

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
