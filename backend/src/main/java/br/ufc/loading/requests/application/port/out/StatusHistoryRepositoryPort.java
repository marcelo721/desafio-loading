package br.ufc.loading.requests.application.port.out;

import br.ufc.loading.requests.domain.model.StatusHistoryEntry;

import java.util.List;
import java.util.UUID;

public interface StatusHistoryRepositoryPort {

    StatusHistoryEntry save(StatusHistoryEntry entry);

    List<StatusHistoryEntry> findByRequestId(UUID requestId);
}
