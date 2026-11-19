package com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.in.web.dto.response;


import com.marcelo721.desfio_loading_full__stack.domain.enums.Role;

import java.util.UUID;

public record UserResponse(UUID id, String name, String email, Role role) {
}
