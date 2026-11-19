package com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.in.web.dto.response;


import com.marcelo721.desfio_loading_full__stack.domain.enums.RequestStatus;

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
