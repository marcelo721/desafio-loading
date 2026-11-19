package com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.in.web.dto.response;

public record AuthResponse(String token, String tokenType, UserResponse user) {
}
