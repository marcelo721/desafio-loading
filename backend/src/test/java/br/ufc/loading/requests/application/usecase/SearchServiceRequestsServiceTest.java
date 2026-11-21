package br.ufc.loading.requests.application.usecase;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.model.PageQuery;
import br.ufc.loading.requests.application.model.PageResult;
import br.ufc.loading.requests.application.model.RequestSearchCriteria;
import br.ufc.loading.requests.application.model.RequestSortField;
import br.ufc.loading.requests.application.model.SearchServiceRequestsQuery;
import br.ufc.loading.requests.application.model.SortDirection;
import br.ufc.loading.requests.application.port.out.ServiceRequestRepositoryPort;
import br.ufc.loading.requests.domain.enums.Priority;
import br.ufc.loading.requests.domain.enums.RequestStatus;
import br.ufc.loading.requests.domain.enums.Role;
import br.ufc.loading.requests.domain.exception.DomainValidationException;
import br.ufc.loading.requests.domain.model.ServiceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceRequestsServiceTest {

    @Mock
    private ServiceRequestRepositoryPort requestRepository;

    private SearchServiceRequestsService service;

    @BeforeEach
    void setUp() {
        service = new SearchServiceRequestsService(requestRepository);
    }

    private SearchServiceRequestsQuery query() {
        return new SearchServiceRequestsQuery(RequestStatus.OPEN, Priority.HIGH, "printer",
                new PageQuery(0, 10), RequestSortField.CREATED_AT, SortDirection.DESC);
    }

    @Test
    void shouldScopeRegularUsersToTheirOwnRequests() {
        AuthenticatedActor user = new AuthenticatedActor(UUID.randomUUID(), "Maria", Role.USER);
        PageResult<ServiceRequest> empty = new PageResult<>(List.of(), 0, 10, 0, 0);
        when(requestRepository.search(any(), any(), any(), any())).thenReturn(empty);

        service.execute(query(), user);

        ArgumentCaptor<RequestSearchCriteria> captor = ArgumentCaptor.forClass(RequestSearchCriteria.class);
        verify(requestRepository).search(captor.capture(), eq(new PageQuery(0, 10)),
                eq(RequestSortField.CREATED_AT), eq(SortDirection.DESC));
        assertThat(captor.getValue().ownerId()).isEqualTo(user.id());
        assertThat(captor.getValue().status()).isEqualTo(RequestStatus.OPEN);
        assertThat(captor.getValue().priority()).isEqualTo(Priority.HIGH);
        assertThat(captor.getValue().text()).isEqualTo("printer");
    }

    @Test
    void shouldNotScopeAdministrators() {
        AuthenticatedActor admin = new AuthenticatedActor(UUID.randomUUID(), "Admin", Role.ADMIN);
        PageResult<ServiceRequest> empty = new PageResult<>(List.of(), 0, 10, 0, 0);
        when(requestRepository.search(any(), any(), any(), any())).thenReturn(empty);

        service.execute(query(), admin);

        ArgumentCaptor<RequestSearchCriteria> captor = ArgumentCaptor.forClass(RequestSearchCriteria.class);
        verify(requestRepository).search(captor.capture(), any(), any(), any());
        assertThat(captor.getValue().ownerId()).isNull();
    }

    @Test
    void shouldRejectUnknownSortField() {
        assertThatThrownBy(() -> RequestSortField.fromProperty("password"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("password");
    }

    @Test
    void shouldRejectInvalidPagination() {
        assertThatThrownBy(() -> new PageQuery(-1, 10)).isInstanceOf(DomainValidationException.class);
        assertThatThrownBy(() -> new PageQuery(0, 0)).isInstanceOf(DomainValidationException.class);
        assertThatThrownBy(() -> new PageQuery(0, PageQuery.MAX_SIZE + 1))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void shouldParseSortDirectionIgnoringCase() {
        assertThat(SortDirection.parse("asc")).isEqualTo(SortDirection.ASC);
        assertThat(SortDirection.parse("DESC")).isEqualTo(SortDirection.DESC);
        assertThat(SortDirection.parse(null)).isEqualTo(SortDirection.DESC);
        assertThatThrownBy(() -> SortDirection.parse("sideways")).isInstanceOf(DomainValidationException.class);
    }
}
