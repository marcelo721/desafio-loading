package br.ufc.loading.requests.application.usecase;


import com.marcelo721.desfio_loading_full__stack.application.command.RegisterUserCommand;
import com.marcelo721.desfio_loading_full__stack.application.port.out.AuditLogPort;
import com.marcelo721.desfio_loading_full__stack.application.port.out.PasswordHasherPort;
import com.marcelo721.desfio_loading_full__stack.application.port.out.UserRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.application.usecase.AuditTrail;
import com.marcelo721.desfio_loading_full__stack.application.usecase.RegisterUserService;
import com.marcelo721.desfio_loading_full__stack.domain.enums.Role;
import com.marcelo721.desfio_loading_full__stack.domain.exceptions.DomainValidationException;
import com.marcelo721.desfio_loading_full__stack.domain.exceptions.EmailAlreadyRegisteredException;
import com.marcelo721.desfio_loading_full__stack.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    private static final Instant NOW = Instant.parse("2026-03-10T12:00:00Z");

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordHasherPort passwordHasher;

    @Mock
    private AuditLogPort auditLogPort;

    private RegisterUserService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        service = new RegisterUserService(userRepository, passwordHasher, new AuditTrail(auditLogPort, clock), clock);
    }

    @Test
    void shouldRegisterUserWithNormalizedEmailHashedPasswordAndUserRole() {
        when(userRepository.existsByEmail("maria@example.com")).thenReturn(false);
        when(passwordHasher.hash("secret-pass")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registered = service.execute(new RegisterUserCommand("Maria", "  Maria@Example.com ", "secret-pass"));

        assertThat(registered.email()).isEqualTo("maria@example.com");
        assertThat(registered.passwordHash()).isEqualTo("hashed");
        assertThat(registered.role()).isEqualTo(Role.USER);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().createdAt()).isEqualTo(NOW);
    }

    @Test
    void shouldRejectAlreadyRegisteredEmail() {
        when(userRepository.existsByEmail("maria@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.execute(new RegisterUserCommand("Maria", "maria@example.com", "secret-pass")))
                .isInstanceOf(EmailAlreadyRegisteredException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldRejectShortPassword() {
        assertThatThrownBy(() -> service.execute(new RegisterUserCommand("Maria", "maria@example.com", "short")))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("senha");

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldRejectInvalidEmail() {
        when(userRepository.existsByEmail("not-an-email")).thenReturn(false);
        when(passwordHasher.hash("secret-pass")).thenReturn("hashed");

        assertThatThrownBy(() -> service.execute(new RegisterUserCommand("Maria", "not-an-email", "secret-pass")))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("E-mail");
    }
}
