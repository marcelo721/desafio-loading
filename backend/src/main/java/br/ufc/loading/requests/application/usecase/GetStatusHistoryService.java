package br.ufc.loading.requests.application.usecase;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.port.in.GetStatusHistoryUseCase;
import br.ufc.loading.requests.application.port.out.StatusHistoryRepositoryPort;
import br.ufc.loading.requests.domain.model.ServiceRequest;
import br.ufc.loading.requests.domain.model.StatusHistoryEntry;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class GetStatusHistoryService implements GetStatusHistoryUseCase {

    private final ServiceRequestFinder finder;
    private final StatusHistoryRepositoryPort historyRepository;

    public GetStatusHistoryService(ServiceRequestFinder finder, StatusHistoryRepositoryPort historyRepository) {
        this.finder = finder;
        this.historyRepository = historyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatusHistoryEntry> execute(UUID requestId, AuthenticatedActor actor) {
        ServiceRequest request = finder.findAccessible(requestId, actor);
        return historyRepository.findByRequestId(request.getId());
    }
}
