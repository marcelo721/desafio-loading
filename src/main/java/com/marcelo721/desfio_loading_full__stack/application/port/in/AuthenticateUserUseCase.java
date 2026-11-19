package com.marcelo721.desfio_loading_full__stack.application.port.in;


import com.marcelo721.desfio_loading_full__stack.application.command.AuthenticateUserCommand;
import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticationResult;

public interface AuthenticateUserUseCase {

    AuthenticationResult execute(AuthenticateUserCommand command);
}
