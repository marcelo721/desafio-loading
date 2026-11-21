package br.ufc.loading.requests.application.port.in;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.model.DashboardSummary;

public interface GetDashboardSummaryUseCase {

    DashboardSummary execute(AuthenticatedActor actor);
}
