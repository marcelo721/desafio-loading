package com.marcelo721.desfio_loading_full__stack.application.model;


import com.marcelo721.desfio_loading_full__stack.domain.enums.Priority;
import com.marcelo721.desfio_loading_full__stack.domain.enums.RequestStatus;

import java.util.UUID;

public record RequestSearchCriteria(RequestStatus status, Priority priority, String text, UUID ownerId) {

    public RequestSearchCriteria withStatus(RequestStatus newStatus) {
        return new RequestSearchCriteria(newStatus, priority, text, ownerId);
    }

    public static RequestSearchCriteria ownedBy(UUID ownerId) {
        return new RequestSearchCriteria(null, null, null, ownerId);
    }
}
