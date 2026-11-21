package br.ufc.loading.requests.domain.enums;

import java.util.Optional;

public enum RequestStatus {
    OPEN("Aberta"),
    IN_PROGRESS("Em andamento"),
    COMPLETED("Concluída");

    private final String displayName;

    RequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public Optional<RequestStatus> next() {
        RequestStatus[] all = values();
        int nextIndex = ordinal() + 1;
        return nextIndex < all.length ? Optional.of(all[nextIndex]) : Optional.empty();
    }

    public boolean canTransitionTo(RequestStatus target) {
        return next().map(expected -> expected == target).orElse(false);
    }
}
