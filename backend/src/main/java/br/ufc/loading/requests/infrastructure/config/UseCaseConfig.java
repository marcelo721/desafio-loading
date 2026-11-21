package br.ufc.loading.requests.infrastructure.config;

import br.ufc.loading.requests.application.policy.RequestAccessPolicy;
import br.ufc.loading.requests.application.port.in.AuthenticateUserUseCase;
import br.ufc.loading.requests.application.port.in.ChangeRequestStatusUseCase;
import br.ufc.loading.requests.application.port.in.CreateServiceRequestUseCase;
import br.ufc.loading.requests.application.port.in.DeleteServiceRequestUseCase;
import br.ufc.loading.requests.application.port.in.GetDashboardSummaryUseCase;
import br.ufc.loading.requests.application.port.in.GetServiceRequestUseCase;
import br.ufc.loading.requests.application.port.in.GetStatusHistoryUseCase;
import br.ufc.loading.requests.application.port.in.ListAuditEntriesUseCase;
import br.ufc.loading.requests.application.port.in.RegisterUserUseCase;
import br.ufc.loading.requests.application.port.in.SearchServiceRequestsUseCase;
import br.ufc.loading.requests.application.port.in.UpdateServiceRequestUseCase;
import br.ufc.loading.requests.application.port.out.AuditLogPort;
import br.ufc.loading.requests.application.port.out.PasswordHasherPort;
import br.ufc.loading.requests.application.port.out.ServiceRequestRepositoryPort;
import br.ufc.loading.requests.application.port.out.StatusHistoryRepositoryPort;
import br.ufc.loading.requests.application.port.out.TokenProviderPort;
import br.ufc.loading.requests.application.port.out.UserRepositoryPort;
import br.ufc.loading.requests.application.usecase.AuditTrail;
import br.ufc.loading.requests.application.usecase.AuthenticateUserService;
import br.ufc.loading.requests.application.usecase.ChangeRequestStatusService;
import br.ufc.loading.requests.application.usecase.CreateServiceRequestService;
import br.ufc.loading.requests.application.usecase.DeleteServiceRequestService;
import br.ufc.loading.requests.application.usecase.GetDashboardSummaryService;
import br.ufc.loading.requests.application.usecase.GetServiceRequestService;
import br.ufc.loading.requests.application.usecase.GetStatusHistoryService;
import br.ufc.loading.requests.application.usecase.ListAuditEntriesService;
import br.ufc.loading.requests.application.usecase.RegisterUserService;
import br.ufc.loading.requests.application.usecase.SearchServiceRequestsService;
import br.ufc.loading.requests.application.usecase.ServiceRequestFinder;
import br.ufc.loading.requests.application.usecase.UpdateServiceRequestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class UseCaseConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public RequestAccessPolicy requestAccessPolicy() {
        return new RequestAccessPolicy();
    }

    @Bean
    public AuditTrail auditTrail(AuditLogPort auditLogPort, Clock clock) {
        return new AuditTrail(auditLogPort, clock);
    }

    @Bean
    public ServiceRequestFinder serviceRequestFinder(ServiceRequestRepositoryPort requestRepository,
                                                     RequestAccessPolicy accessPolicy) {
        return new ServiceRequestFinder(requestRepository, accessPolicy);
    }

    @Bean
    public CreateServiceRequestUseCase createServiceRequestUseCase(ServiceRequestRepositoryPort requestRepository,
                                                                   StatusHistoryRepositoryPort historyRepository,
                                                                   AuditTrail auditTrail,
                                                                   Clock clock) {
        return new CreateServiceRequestService(requestRepository, historyRepository, auditTrail, clock);
    }

    @Bean
    public GetServiceRequestUseCase getServiceRequestUseCase(ServiceRequestFinder finder) {
        return new GetServiceRequestService(finder);
    }

    @Bean
    public UpdateServiceRequestUseCase updateServiceRequestUseCase(ServiceRequestFinder finder,
                                                                   ServiceRequestRepositoryPort requestRepository,
                                                                   AuditTrail auditTrail,
                                                                   Clock clock) {
        return new UpdateServiceRequestService(finder, requestRepository, auditTrail, clock);
    }

    @Bean
    public DeleteServiceRequestUseCase deleteServiceRequestUseCase(ServiceRequestFinder finder,
                                                                   ServiceRequestRepositoryPort requestRepository,
                                                                   AuditTrail auditTrail) {
        return new DeleteServiceRequestService(finder, requestRepository, auditTrail);
    }

    @Bean
    public SearchServiceRequestsUseCase searchServiceRequestsUseCase(ServiceRequestRepositoryPort requestRepository) {
        return new SearchServiceRequestsService(requestRepository);
    }

    @Bean
    public ChangeRequestStatusUseCase changeRequestStatusUseCase(ServiceRequestFinder finder,
                                                                 ServiceRequestRepositoryPort requestRepository,
                                                                 StatusHistoryRepositoryPort historyRepository,
                                                                 AuditTrail auditTrail,
                                                                 Clock clock) {
        return new ChangeRequestStatusService(finder, requestRepository, historyRepository, auditTrail, clock);
    }

    @Bean
    public GetStatusHistoryUseCase getStatusHistoryUseCase(ServiceRequestFinder finder,
                                                           StatusHistoryRepositoryPort historyRepository) {
        return new GetStatusHistoryService(finder, historyRepository);
    }

    @Bean
    public GetDashboardSummaryUseCase getDashboardSummaryUseCase(ServiceRequestRepositoryPort requestRepository) {
        return new GetDashboardSummaryService(requestRepository);
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepositoryPort userRepository,
                                                   PasswordHasherPort passwordHasher,
                                                   AuditTrail auditTrail,
                                                   Clock clock) {
        return new RegisterUserService(userRepository, passwordHasher, auditTrail, clock);
    }

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(UserRepositoryPort userRepository,
                                                           PasswordHasherPort passwordHasher,
                                                           TokenProviderPort tokenProvider,
                                                           AuditTrail auditTrail) {
        return new AuthenticateUserService(userRepository, passwordHasher, tokenProvider, auditTrail);
    }

    @Bean
    public ListAuditEntriesUseCase listAuditEntriesUseCase(AuditLogPort auditLogPort) {
        return new ListAuditEntriesService(auditLogPort);
    }
}
