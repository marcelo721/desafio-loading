package br.ufc.loading.requests.infrastructure.adapter.in.web.controller;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.model.PageQuery;
import br.ufc.loading.requests.application.port.in.ListAuditEntriesUseCase;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response.AuditEntryResponse;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response.PageResponse;
import br.ufc.loading.requests.infrastructure.adapter.in.web.mapper.WebMapper;
import br.ufc.loading.requests.infrastructure.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
@Tag(name = "Auditoria", description = "Registro de operações realizadas (somente ADMIN)")
@SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
public class AuditController {

    private final ListAuditEntriesUseCase listAuditEntriesUseCase;

    public AuditController(ListAuditEntriesUseCase listAuditEntriesUseCase) {
        this.listAuditEntriesUseCase = listAuditEntriesUseCase;
    }

    @GetMapping
    @Operation(summary = "Lista os registros de auditoria, do mais recente para o mais antigo")
    public PageResponse<AuditEntryResponse> list(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size,
                                                 @AuthenticationPrincipal AuthenticatedActor actor) {
        return PageResponse.from(listAuditEntriesUseCase.execute(new PageQuery(page, size), actor),
                WebMapper::toResponse);
    }
}
