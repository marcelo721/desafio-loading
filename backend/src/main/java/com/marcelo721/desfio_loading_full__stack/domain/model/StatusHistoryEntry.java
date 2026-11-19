package com.marcelo721.desfio_loading_full__stack.domain.model;


import com.marcelo721.desfio_loading_full__stack.domain.enums.RequestStatus;

import java.time.Instant;
import java.util.UUID;

public record StatusHistoryEntry(
        UUID id,
        UUID requestId,
        RequestStatus fromStatus,
        RequestStatus toStatus,
        UUID changedById,
        String changedByName,
        Instant changedAt) {

    public static StatusHistoryEntry initial(UUID requestId, RequestStatus initialStatus,
                                             UUID changedById, String changedByName, Instant now) {
        return new StatusHistoryEntry(UUID.randomUUID(), requestId, null, initialStatus,
                changedById, changedByName, now);
    }

    public static StatusHistoryEntry transition(UUID requestId, RequestStatus from, RequestStatus to,
                                                UUID changedById, String changedByName, Instant now) {
        return new StatusHistoryEntry(UUID.randomUUID(), requestId, from, to,
                changedById, changedByName, now);
    }
}
