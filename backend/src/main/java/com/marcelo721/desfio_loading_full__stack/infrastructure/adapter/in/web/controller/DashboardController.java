package com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.in.web.controller;


import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.port.in.GetDashboardSummaryUseCase;
import com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.in.web.dto.response.DashboardSummaryResponse;
import com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.in.web.mapper.WebMapper;
import com.marcelo721.desfio_loading_full__stack.infrastructure.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Indicadores das solicitações")
@SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
public class DashboardController {

    private final GetDashboardSummaryUseCase summaryUseCase;

    public DashboardController(GetDashboardSummaryUseCase summaryUseCase) {
        this.summaryUseCase = summaryUseCase;
    }

    @GetMapping("/summary")
    @Operation(summary = "Totais de solicitações: geral, abertas, em andamento e concluídas",
            description = "Usuários comuns recebem os totais das próprias solicitações; administradores, os globais.")
    public DashboardSummaryResponse summary(@AuthenticationPrincipal AuthenticatedActor actor) {
        return WebMapper.toResponse(summaryUseCase.execute(actor));
    }
}
