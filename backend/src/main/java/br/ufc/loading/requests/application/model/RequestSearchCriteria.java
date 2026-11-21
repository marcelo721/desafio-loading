package br.ufc.loading.requests.application.model;

import br.ufc.loading.requests.domain.enums.Priority;
import br.ufc.loading.requests.domain.enums.RequestStatus;

import java.util.UUID;

public record RequestSearchCriteria(RequestStatus status, Priority priority, String text, UUID ownerId) {

    public RequestSearchCriteria withStatus(RequestStatus newStatus) {
        return new RequestSearchCriteria(newStatus, priority, text, ownerId);
    }

    public static RequestSearchCriteria ownedBy(UUID ownerId) {
        return new RequestSearchCriteria(null, null, null, ownerId);
    }
}
