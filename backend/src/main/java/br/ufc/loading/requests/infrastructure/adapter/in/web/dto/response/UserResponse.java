package br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response;

import br.ufc.loading.requests.domain.enums.Role;

import java.util.UUID;

public record UserResponse(UUID id, String name, String email, Role role) {
}
