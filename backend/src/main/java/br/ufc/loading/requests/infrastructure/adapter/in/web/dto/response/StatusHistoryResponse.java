package br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response;

import br.ufc.loading.requests.domain.enums.RequestStatus;

import java.time.Instant;
import java.util.UUID;

public record StatusHistoryResponse(
        UUID id,
        RequestStatus fromStatus,
        RequestStatus toStatus,
        UUID changedById,
        String changedByName,
        Instant changedAt) {
}
