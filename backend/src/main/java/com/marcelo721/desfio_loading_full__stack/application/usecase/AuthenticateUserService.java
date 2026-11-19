package com.marcelo721.desfio_loading_full__stack.application.usecase;
import com.marcelo721.desfio_loading_full__stack.application.command.AuthenticateUserCommand;
import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticationResult;
import com.marcelo721.desfio_loading_full__stack.application.port.in.AuthenticateUserUseCase;
import com.marcelo721.desfio_loading_full__stack.application.port.out.PasswordHasherPort;
import com.marcelo721.desfio_loading_full__stack.application.port.out.TokenProviderPort;
import com.marcelo721.desfio_loading_full__stack.application.port.out.UserRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditOperation;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditedEntity;
import com.marcelo721.desfio_loading_full__stack.domain.exceptions.InvalidCredentialsException;
import com.marcelo721.desfio_loading_full__stack.domain.model.User;
import org.springframework.transaction.annotation.Transactional;

public class AuthenticateUserService implements AuthenticateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenProviderPort tokenProvider;
    private final AuditTrail auditTrail;

    public AuthenticateUserService(UserRepositoryPort userRepository,
                                   PasswordHasherPort passwordHasher,
                                   TokenProviderPort tokenProvider,
                                   AuditTrail auditTrail) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
        this.auditTrail = auditTrail;
    }

    @Override
    @Transactional
    public AuthenticationResult execute(AuthenticateUserCommand command) {
        String normalizedEmail = command.email() == null ? "" : command.email().trim().toLowerCase();
        String rawPassword = command.password() == null ? "" : command.password();

        User user = userRepository.findByEmail(normalizedEmail)
                .filter(candidate -> passwordHasher.matches(rawPassword, candidate.passwordHash()))
                .orElseThrow(InvalidCredentialsException::new);

        AuthenticatedActor actor = new AuthenticatedActor(user.id(), user.name(), user.role());
        auditTrail.record(AuditedEntity.USER, user.id(), AuditOperation.LOGIN, actor,
                "Login realizado: " + user.email());
        return new AuthenticationResult(tokenProvider.issue(user), user);
    }
}
