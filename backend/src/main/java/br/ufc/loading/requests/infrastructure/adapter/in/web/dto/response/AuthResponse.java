package br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response;

public record AuthResponse(String token, String tokenType, UserResponse user) {
}
