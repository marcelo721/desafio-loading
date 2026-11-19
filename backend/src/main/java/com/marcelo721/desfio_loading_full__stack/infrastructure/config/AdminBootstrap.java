package com.marcelo721.desfio_loading_full__stack.infrastructure.config;

import com.marcelo721.desfio_loading_full__stack.application.port.out.PasswordHasherPort;
import com.marcelo721.desfio_loading_full__stack.application.port.out.UserRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.domain.enums.Role;
import com.marcelo721.desfio_loading_full__stack.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final AppProperties properties;
    private final Clock clock;

    public AdminBootstrap(UserRepositoryPort userRepository, PasswordHasherPort passwordHasher,
                          AppProperties properties, Clock clock) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    public void run(ApplicationArguments args) {
        AppProperties.BootstrapAdmin admin = properties.bootstrapAdmin();
        if (admin == null || admin.email() == null || admin.email().isBlank()
                || admin.password() == null || admin.password().isBlank()) {
            return;
        }

        String email = admin.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            return;
        }

        userRepository.save(User.register(admin.name(), email, passwordHasher.hash(admin.password()),
                Role.ADMIN, clock.instant()));
        log.info("Usuário administrador inicial criado: {}", email);
    }
}
