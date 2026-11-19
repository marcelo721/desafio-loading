package com.marcelo721.desfio_loading_full__stack.application.model;


import com.marcelo721.desfio_loading_full__stack.domain.enums.Role;

import java.util.UUID;

public record AuthenticatedActor(UUID id, String name, Role role) {

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
