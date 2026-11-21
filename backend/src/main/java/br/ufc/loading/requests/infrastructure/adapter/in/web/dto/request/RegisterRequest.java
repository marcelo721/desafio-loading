package br.ufc.loading.requests.infrastructure.adapter.in.web.dto.request;

import br.ufc.loading.requests.application.usecase.RegisterUserService;
import br.ufc.loading.requests.domain.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @Schema(example = "Maria Silva")
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = User.NAME_MAX_LENGTH, message = "O nome deve ter no máximo {max} caracteres.")
        String name,

        @Schema(example = "maria@empresa.com")
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = User.EMAIL_MAX_LENGTH, message = "O e-mail deve ter no máximo {max} caracteres.")
        String email,

        @Schema(example = "minhaSenha123")
        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = RegisterUserService.PASSWORD_MIN_LENGTH, max = RegisterUserService.PASSWORD_MAX_LENGTH,
                message = "A senha deve ter entre {min} e {max} caracteres.")
        String password) {
}
