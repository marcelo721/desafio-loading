package br.ufc.loading.requests.infrastructure.adapter.in.web.controller;

import br.ufc.loading.requests.application.command.CreateServiceRequestCommand;
import br.ufc.loading.requests.application.command.UpdateServiceRequestCommand;
import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.model.PageQuery;
import br.ufc.loading.requests.application.model.RequestSortField;
import br.ufc.loading.requests.application.model.SearchServiceRequestsQuery;
import br.ufc.loading.requests.application.model.SortDirection;
import br.ufc.loading.requests.application.port.in.ChangeRequestStatusUseCase;
import br.ufc.loading.requests.application.port.in.CreateServiceRequestUseCase;
import br.ufc.loading.requests.application.port.in.DeleteServiceRequestUseCase;
import br.ufc.loading.requests.application.port.in.GetServiceRequestUseCase;
import br.ufc.loading.requests.application.port.in.GetStatusHistoryUseCase;
import br.ufc.loading.requests.application.port.in.SearchServiceRequestsUseCase;
import br.ufc.loading.requests.application.port.in.UpdateServiceRequestUseCase;
import br.ufc.loading.requests.domain.enums.Priority;
import br.ufc.loading.requests.domain.enums.RequestStatus;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.request.ChangeStatusRequest;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.request.ServiceRequestPayload;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response.PageResponse;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response.ServiceRequestResponse;
import br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response.StatusHistoryResponse;
import br.ufc.loading.requests.infrastructure.adapter.in.web.mapper.WebMapper;
import br.ufc.loading.requests.infrastructure.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/requests")
@Tag(name = "Solicitações", description = "Cadastro, consulta, edição, exclusão e fluxo de status")
@SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
public class ServiceRequestController {

    private final CreateServiceRequestUseCase createUseCase;
    private final GetServiceRequestUseCase getUseCase;
    private final UpdateServiceRequestUseCase updateUseCase;
    private final DeleteServiceRequestUseCase deleteUseCase;
    private final SearchServiceRequestsUseCase searchUseCase;
    private final ChangeRequestStatusUseCase changeStatusUseCase;
    private final GetStatusHistoryUseCase historyUseCase;

    public ServiceRequestController(CreateServiceRequestUseCase createUseCase,
                                    GetServiceRequestUseCase getUseCase,
                                    UpdateServiceRequestUseCase updateUseCase,
                                    DeleteServiceRequestUseCase deleteUseCase,
                                    SearchServiceRequestsUseCase searchUseCase,
                                    ChangeRequestStatusUseCase changeStatusUseCase,
                                    GetStatusHistoryUseCase historyUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.updateUseCase = updateUseCase;
        this.deleteUseCase = deleteUseCase;
        this.searchUseCase = searchUseCase;
        this.changeStatusUseCase = changeStatusUseCase;
        this.historyUseCase = historyUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra uma solicitação",
            description = "O status inicial é sempre OPEN, independentemente do corpo enviado.")
    @ApiResponse(responseCode = "201", description = "Solicitação criada")
    @ApiResponse(responseCode = "400", description = "Campos obrigatórios ausentes ou inválidos")
    public ServiceRequestResponse create(@Valid @RequestBody ServiceRequestPayload body,
                                         @AuthenticationPrincipal AuthenticatedActor actor) {
        return WebMapper.toResponse(createUseCase.execute(
                new CreateServiceRequestCommand(body.title(), body.description(), body.category(), body.priority()),
                actor));
    }

    @GetMapping
    @Operation(summary = "Lista solicitações com filtros, busca textual, ordenação e paginação",
            description = "Usuários comuns veem apenas as próprias solicitações; administradores veem todas.")
    public PageResponse<ServiceRequestResponse> search(
            @Parameter(description = "Filtra por status") @RequestParam(required = false) RequestStatus status,
            @Parameter(description = "Filtra por prioridade") @RequestParam(required = false) Priority priority,
            @Parameter(description = "Busca em título, descrição e categoria")
            @RequestParam(required = false) String search,
            @Parameter(description = "createdAt, updatedAt, title, priority ou status")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "ASC ou DESC") @RequestParam(defaultValue = "DESC") String direction,
            @Parameter(description = "Página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página (1 a 100)") @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal AuthenticatedActor actor) {
        SearchServiceRequestsQuery query = new SearchServiceRequestsQuery(
                status,
                priority,
                search,
                new PageQuery(page, size),
                RequestSortField.fromProperty(sortBy),
                SortDirection.parse(direction));
        return PageResponse.from(searchUseCase.execute(query, actor), WebMapper::toResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta os dados de uma solicitação")
    @ApiResponse(responseCode = "404", description = "Solicitação não encontrada")
    public ServiceRequestResponse getById(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedActor actor) {
        return WebMapper.toResponse(getUseCase.execute(id, actor));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edita título, descrição, categoria e prioridade",
            description = "O status só pode ser alterado pelo endpoint PATCH /{id}/status.")
    public ServiceRequestResponse update(@PathVariable UUID id,
                                         @Valid @RequestBody ServiceRequestPayload body,
                                         @AuthenticationPrincipal AuthenticatedActor actor) {
        return WebMapper.toResponse(updateUseCase.execute(id,
                new UpdateServiceRequestCommand(body.title(), body.description(), body.category(), body.priority()),
                actor));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Exclui uma solicitação")
    public void delete(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedActor actor) {
        deleteUseCase.execute(id, actor);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Avança o status da solicitação",
            description = "Fluxo permitido: OPEN -> IN_PROGRESS -> COMPLETED. Saltos e retrocessos retornam 422.")
    @ApiResponse(responseCode = "422", description = "Transição de status inválida")
    public ServiceRequestResponse changeStatus(@PathVariable UUID id,
                                               @Valid @RequestBody ChangeStatusRequest body,
                                               @AuthenticationPrincipal AuthenticatedActor actor) {
        return WebMapper.toResponse(changeStatusUseCase.execute(id, body.status(), actor));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Lista o histórico de mudanças de status em ordem cronológica")
    public List<StatusHistoryResponse> history(@PathVariable UUID id,
                                               @AuthenticationPrincipal AuthenticatedActor actor) {
        return historyUseCase.execute(id, actor).stream().map(WebMapper::toResponse).toList();
    }
}
