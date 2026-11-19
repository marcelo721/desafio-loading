package com.marcelo721.desfio_loading_full__stack.application.command;


import com.marcelo721.desfio_loading_full__stack.domain.enums.Priority;

public record UpdateServiceRequestCommand(String title, String description, String category, Priority priority) {
}
