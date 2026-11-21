package br.ufc.loading.requests.application.port.in;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.domain.model.StatusHistoryEntry;

import java.util.List;
import java.util.UUID;

public interface GetStatusHistoryUseCase {

    List<StatusHistoryEntry> execute(UUID requestId, AuthenticatedActor actor);
}
