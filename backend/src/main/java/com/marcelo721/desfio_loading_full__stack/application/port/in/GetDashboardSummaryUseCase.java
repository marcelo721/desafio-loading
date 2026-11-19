package com.marcelo721.desfio_loading_full__stack.application.port.in;


import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.model.DashboardSummary;

public interface GetDashboardSummaryUseCase {

    DashboardSummary execute(AuthenticatedActor actor);
}
