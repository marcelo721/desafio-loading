package com.marcelo721.desfio_loading_full__stack.domain.exceptions;


import com.marcelo721.desfio_loading_full__stack.domain.enums.RequestStatus;

public class InvalidStatusTransitionException extends DomainException {

    private final RequestStatus currentStatus;
    private final RequestStatus requestedStatus;

    public InvalidStatusTransitionException(RequestStatus currentStatus, RequestStatus requestedStatus) {
        super(buildMessage(currentStatus, requestedStatus));
        this.currentStatus = currentStatus;
        this.requestedStatus = requestedStatus;
    }

    public RequestStatus getCurrentStatus() {
        return currentStatus;
    }

    public RequestStatus getRequestedStatus() {
        return requestedStatus;
    }

    private static String buildMessage(RequestStatus current, RequestStatus requested) {
        return current.next()
                .map(allowed -> "Transição inválida: de '" + current.displayName() + "' para '"
                        + requested.displayName() + "'. A única etapa permitida a partir de '"
                        + current.displayName() + "' é '" + allowed.displayName() + "'.")
                .orElse("Transição inválida: solicitações '" + current.displayName()
                        + "' não podem mais mudar de status.");
    }
}
