package com.marcelo721.desfio_loading_full__stack.application.port.in;


import com.marcelo721.desfio_loading_full__stack.application.command.RegisterUserCommand;
import com.marcelo721.desfio_loading_full__stack.domain.model.User;

public interface RegisterUserUseCase {

    User execute(RegisterUserCommand command);
}
