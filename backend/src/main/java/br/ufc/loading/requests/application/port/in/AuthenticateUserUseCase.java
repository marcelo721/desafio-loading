package br.ufc.loading.requests.application.port.in;

import br.ufc.loading.requests.application.command.AuthenticateUserCommand;
import br.ufc.loading.requests.application.model.AuthenticationResult;

public interface AuthenticateUserUseCase {

    AuthenticationResult execute(AuthenticateUserCommand command);
}
