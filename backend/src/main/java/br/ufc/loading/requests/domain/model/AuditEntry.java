package br.ufc.loading.requests.domain.model;

import br.ufc.loading.requests.domain.enums.AuditOperation;
import br.ufc.loading.requests.domain.enums.AuditedEntity;

import java.time.Instant;
import java.util.UUID;

public record AuditEntry(
        UUID id,
        AuditedEntity entityType,
        UUID entityId,
        AuditOperation operation,
        UUID userId,
        String userName,
        Instant occurredAt,
        String details) {

    public static final int DETAILS_MAX_LENGTH = 500;

    public static AuditEntry create(AuditedEntity entityType, UUID entityId, AuditOperation operation,
                                    UUID userId, String userName, Instant occurredAt, String details) {
        return new AuditEntry(UUID.randomUUID(), entityType, entityId, operation, userId, userName,
                occurredAt, truncate(details));
    }

    private static String truncate(String details) {
        if (details == null) {
            return null;
        }
        return details.length() > DETAILS_MAX_LENGTH ? details.substring(0, DETAILS_MAX_LENGTH) : details;
    }
}
