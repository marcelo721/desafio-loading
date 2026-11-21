package br.ufc.loading.requests.application.usecase;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.model.DashboardSummary;
import br.ufc.loading.requests.application.model.RequestSearchCriteria;
import br.ufc.loading.requests.application.port.in.GetDashboardSummaryUseCase;
import br.ufc.loading.requests.application.port.out.ServiceRequestRepositoryPort;
import br.ufc.loading.requests.domain.enums.RequestStatus;
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
