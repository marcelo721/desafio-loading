package br.ufc.loading.requests.application.port.in;

import br.ufc.loading.requests.application.command.RegisterUserCommand;
import br.ufc.loading.requests.domain.model.User;

public interface RegisterUserUseCase {

    User execute(RegisterUserCommand command);
}
