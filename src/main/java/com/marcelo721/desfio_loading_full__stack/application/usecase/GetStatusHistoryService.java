package com.marcelo721.desfio_loading_full__stack.application.usecase;

import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.port.in.GetStatusHistoryUseCase;
import com.marcelo721.desfio_loading_full__stack.application.port.out.StatusHistoryRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;
import com.marcelo721.desfio_loading_full__stack.domain.model.StatusHistoryEntry;
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
