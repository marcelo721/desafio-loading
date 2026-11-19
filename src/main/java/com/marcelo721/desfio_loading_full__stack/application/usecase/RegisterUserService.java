package com.marcelo721.desfio_loading_full__stack.application.usecase;

import com.marcelo721.desfio_loading_full__stack.application.command.RegisterUserCommand;
import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.port.in.RegisterUserUseCase;
import com.marcelo721.desfio_loading_full__stack.application.port.out.PasswordHasherPort;
import com.marcelo721.desfio_loading_full__stack.application.port.out.UserRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditOperation;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditedEntity;
import com.marcelo721.desfio_loading_full__stack.domain.enums.Role;
import com.marcelo721.desfio_loading_full__stack.domain.exceptions.DomainValidationException;
import com.marcelo721.desfio_loading_full__stack.domain.exceptions.EmailAlreadyRegisteredException;
import com.marcelo721.desfio_loading_full__stack.domain.model.User;
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
