package com.marcelo721.desfio_loading_full__stack.application.usecase;

import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.model.DashboardSummary;
import com.marcelo721.desfio_loading_full__stack.application.model.RequestSearchCriteria;
import com.marcelo721.desfio_loading_full__stack.application.port.in.GetDashboardSummaryUseCase;
import com.marcelo721.desfio_loading_full__stack.application.port.out.ServiceRequestRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.domain.enums.RequestStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class GetDashboardSummaryService implements GetDashboardSummaryUseCase {

    private final ServiceRequestRepositoryPort requestRepository;

    public GetDashboardSummaryService(ServiceRequestRepositoryPort requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummary execute(AuthenticatedActor actor) {
        UUID ownerScope = actor.isAdmin() ? null : actor.id();
        RequestSearchCriteria scope = RequestSearchCriteria.ownedBy(ownerScope);

        long open = requestRepository.count(scope.withStatus(RequestStatus.OPEN));
        long inProgress = requestRepository.count(scope.withStatus(RequestStatus.IN_PROGRESS));
        long completed = requestRepository.count(scope.withStatus(RequestStatus.COMPLETED));

        return new DashboardSummary(open + inProgress + completed, open, inProgress, completed);
    }
}
