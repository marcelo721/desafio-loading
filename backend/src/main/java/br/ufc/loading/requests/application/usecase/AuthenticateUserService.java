package br.ufc.loading.requests.application.usecase;

import br.ufc.loading.requests.application.command.AuthenticateUserCommand;
import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.model.AuthenticationResult;
import br.ufc.loading.requests.application.port.in.AuthenticateUserUseCase;
import br.ufc.loading.requests.application.port.out.PasswordHasherPort;
import br.ufc.loading.requests.application.port.out.TokenProviderPort;
import br.ufc.loading.requests.application.port.out.UserRepositoryPort;
import br.ufc.loading.requests.domain.enums.AuditOperation;
import br.ufc.loading.requests.domain.enums.AuditedEntity;
import br.ufc.loading.requests.domain.exception.InvalidCredentialsException;
import br.ufc.loading.requests.domain.model.User;
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
