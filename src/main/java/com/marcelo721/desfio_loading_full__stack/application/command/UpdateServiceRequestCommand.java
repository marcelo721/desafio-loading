package com.marcelo721.desfio_loading_full__stack.application.command;

import br.ufc.loading.requests.domain.enums.Priority;

public record UpdateServiceRequestCommand(String title, String description, String category, Priority priority) {
}
