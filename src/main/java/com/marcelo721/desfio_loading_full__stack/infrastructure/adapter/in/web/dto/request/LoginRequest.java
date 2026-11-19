package com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @Schema(example = "admin@loading.dev")
        @NotBlank(message = "O e-mail é obrigatório.")
        String email,

        @Schema(example = "Admin@12345")
        @NotBlank(message = "A senha é obrigatória.")
        String password) {
}
