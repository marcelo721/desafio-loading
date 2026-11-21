package br.ufc.loading.requests.application.command;

import br.ufc.loading.requests.domain.enums.Priority;

public record UpdateServiceRequestCommand(String title, String description, String category, Priority priority) {
}
