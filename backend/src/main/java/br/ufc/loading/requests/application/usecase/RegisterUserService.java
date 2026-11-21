package br.ufc.loading.requests.application.usecase;

import br.ufc.loading.requests.application.command.RegisterUserCommand;
import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.port.in.RegisterUserUseCase;
import br.ufc.loading.requests.application.port.out.PasswordHasherPort;
import br.ufc.loading.requests.application.port.out.UserRepositoryPort;
import br.ufc.loading.requests.domain.enums.AuditOperation;
import br.ufc.loading.requests.domain.enums.AuditedEntity;
import br.ufc.loading.requests.domain.enums.Role;
import br.ufc.loading.requests.domain.exception.DomainValidationException;
import br.ufc.loading.requests.domain.exception.EmailAlreadyRegisteredException;
import br.ufc.loading.requests.domain.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class RegisterUserService implements RegisterUserUseCase {

    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 72;

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final AuditTrail auditTrail;
    private final Clock clock;

    public RegisterUserService(UserRepositoryPort userRepository,
                               PasswordHasherPort passwordHasher,
                               AuditTrail auditTrail,
                               Clock clock) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.auditTrail = auditTrail;
        this.clock = clock;
    }

    @Override
    @Transactional
    public User execute(RegisterUserCommand command) {
        validatePassword(command.password());

        String normalizedEmail = command.email() == null ? "" : command.email().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyRegisteredException();
        }

        User user = User.register(command.name(), normalizedEmail,
                passwordHasher.hash(command.password()), Role.USER, clock.instant());
        User saved = userRepository.save(user);

        AuthenticatedActor actor = new AuthenticatedActor(saved.id(), saved.name(), saved.role());
        auditTrail.record(AuditedEntity.USER, saved.id(), AuditOperation.REGISTER, actor,
                "Usuário cadastrado: " + saved.email());
        return saved;
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
            throw new DomainValidationException("A senha deve ter entre " + PASSWORD_MIN_LENGTH
                    + " e " + PASSWORD_MAX_LENGTH + " caracteres.");
        }
    }
}
