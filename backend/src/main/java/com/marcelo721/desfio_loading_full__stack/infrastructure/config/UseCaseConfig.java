package com.marcelo721.desfio_loading_full__stack.infrastructure.config;


import com.marcelo721.desfio_loading_full__stack.application.policy.RequestAccessPolicy;
import com.marcelo721.desfio_loading_full__stack.application.port.in.*;
import com.marcelo721.desfio_loading_full__stack.application.port.out.*;
import com.marcelo721.desfio_loading_full__stack.application.usecase.*;
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
