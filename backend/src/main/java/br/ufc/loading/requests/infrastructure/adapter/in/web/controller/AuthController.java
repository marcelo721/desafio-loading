package br.ufc.loading.requests.infrastructure.adapter.in.web.controller;

import br.ufc.loading.requests.application.command.AuthenticateUserCommand;
import br.ufc.loading.requests.application.command.RegisterUserCommand;
import br.ufc.loading.requests.application.model.AuthenticationResult;
import br.ufc.loading.requests.application.port.in.AuthenticateUserUseCase;
import br.ufc.loading.requests.application.port.in.RegisterUserUseCase;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.request.LoginRequest;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.request.RegisterRequest;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response.ApiError;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response.AuthResponse;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response.UserResponse;
import br.ufc.loading.requests.infrastructure.adapter.in.web.mapper.WebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Cadastro de usuários e login")
public class AuthController {

    private static final String BEARER = "Bearer";

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          AuthenticateUserUseCase authenticateUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra um novo usuário com perfil USER")
    @ApiResponse(responseCode = "201", description = "Usuário cadastrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public UserResponse register(@Valid @RequestBody RegisterRequest body) {
        return WebMapper.toResponse(registerUserUseCase.execute(
                new RegisterUserCommand(body.name(), body.email(), body.password())));
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica o usuário e retorna um token JWT")
    @ApiResponse(responseCode = "200", description = "Autenticado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public AuthResponse login(@Valid @RequestBody LoginRequest body) {
        AuthenticationResult result = authenticateUserUseCase.execute(
                new AuthenticateUserCommand(body.email(), body.password()));
        return new AuthResponse(result.token(), BEARER, WebMapper.toResponse(result.user()));
    }
}
