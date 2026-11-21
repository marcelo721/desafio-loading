package br.ufc.loading.requests.application.model;

import br.ufc.loading.requests.domain.enums.Role;

import java.util.UUID;

public record AuthenticatedActor(UUID id, String name, Role role) {

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
