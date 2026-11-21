package br.ufc.loading.requests.domain.model;

import br.ufc.loading.requests.domain.enums.Priority;
import br.ufc.loading.requests.domain.enums.RequestStatus;
import br.ufc.loading.requests.domain.exception.DomainValidationException;
import br.ufc.loading.requests.domain.exception.InvalidStatusTransitionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceRequestTest {

    private static final Instant NOW = Instant.parse("2026-03-10T12:00:00Z");
    private static final Instant LATER = NOW.plusSeconds(3600);

    private ServiceRequest newRequest() {
        return ServiceRequest.open("Printer broken", "The printer on floor 2 is jammed", "Equipment",
                Priority.MEDIUM, UUID.randomUUID(), "Maria", NOW);
    }

    @Test
    void shouldStartAsOpenWithTrimmedFields() {
        ServiceRequest request = ServiceRequest.open("  Printer broken  ", " Jammed ", " Equipment ",
                Priority.LOW, UUID.randomUUID(), "Maria", NOW);

        assertThat(request.getStatus()).isEqualTo(RequestStatus.OPEN);
        assertThat(request.getTitle()).isEqualTo("Printer broken");
        assertThat(request.getDescription()).isEqualTo("Jammed");
        assertThat(request.getCategory()).isEqualTo("Equipment");
        assertThat(request.getCreatedAt()).isEqualTo(NOW);
        assertThat(request.getUpdatedAt()).isEqualTo(NOW);
        assertThat(request.getId()).isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectBlankTitle(String title) {
        assertThatThrownBy(() -> ServiceRequest.open(title, "desc", "cat", Priority.LOW,
                UUID.randomUUID(), "Maria", NOW))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Título");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectBlankDescriptionAndCategory(String blank) {
        assertThatThrownBy(() -> ServiceRequest.open("title", blank, "cat", Priority.LOW,
                UUID.randomUUID(), "Maria", NOW))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Descrição");
        assertThatThrownBy(() -> ServiceRequest.open("title", "desc", blank, Priority.LOW,
                UUID.randomUUID(), "Maria", NOW))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Categoria");
    }

    @Test
    void shouldRejectMissingPriority() {
        assertThatThrownBy(() -> ServiceRequest.open("title", "desc", "cat", null,
                UUID.randomUUID(), "Maria", NOW))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Prioridade");
    }

    @Test
    void shouldRejectTitleLongerThanLimit() {
        String longTitle = "a".repeat(ServiceRequest.TITLE_MAX_LENGTH + 1);

        assertThatThrownBy(() -> ServiceRequest.open(longTitle, "desc", "cat", Priority.LOW,
                UUID.randomUUID(), "Maria", NOW))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("120");
    }

    @Test
    void shouldFollowTheFullStatusFlow() {
        ServiceRequest request = newRequest();

        RequestStatus first = request.moveTo(RequestStatus.IN_PROGRESS, LATER);
        RequestStatus second = request.moveTo(RequestStatus.COMPLETED, LATER.plusSeconds(60));

        assertThat(first).isEqualTo(RequestStatus.OPEN);
        assertThat(second).isEqualTo(RequestStatus.IN_PROGRESS);
        assertThat(request.getStatus()).isEqualTo(RequestStatus.COMPLETED);
        assertThat(request.getUpdatedAt()).isEqualTo(LATER.plusSeconds(60));
    }

    @Test
    void shouldNotAllowSkippingFromOpenToCompleted() {
        ServiceRequest request = newRequest();

        assertThatThrownBy(() -> request.moveTo(RequestStatus.COMPLETED, LATER))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("Aberta")
                .hasMessageContaining("Concluída")
                .hasMessageContaining("Em andamento");
        assertThat(request.getStatus()).isEqualTo(RequestStatus.OPEN);
        assertThat(request.getUpdatedAt()).isEqualTo(NOW);
    }

    @Test
    void shouldNotAllowGoingBackwards() {
        ServiceRequest request = newRequest();
        request.moveTo(RequestStatus.IN_PROGRESS, LATER);

        assertThatThrownBy(() -> request.moveTo(RequestStatus.OPEN, LATER))
                .isInstanceOf(InvalidStatusTransitionException.class);
        assertThat(request.getStatus()).isEqualTo(RequestStatus.IN_PROGRESS);
    }

    @Test
    void shouldNotAllowChangingACompletedRequest() {
        ServiceRequest request = newRequest();
        request.moveTo(RequestStatus.IN_PROGRESS, LATER);
        request.moveTo(RequestStatus.COMPLETED, LATER);

        assertThatThrownBy(() -> request.moveTo(RequestStatus.COMPLETED, LATER))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("não podem mais mudar");
    }

    @Test
    void shouldRejectNullTargetStatus() {
        ServiceRequest request = newRequest();

        assertThatThrownBy(() -> request.moveTo(null, LATER))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void shouldEditFieldsAndRefreshUpdatedAtWithoutChangingStatus() {
        ServiceRequest request = newRequest();
        request.moveTo(RequestStatus.IN_PROGRESS, LATER);

        request.edit("New title", "New description", "New category", Priority.HIGH, LATER.plusSeconds(10));

        assertThat(request.getTitle()).isEqualTo("New title");
        assertThat(request.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(request.getStatus()).isEqualTo(RequestStatus.IN_PROGRESS);
        assertThat(request.getUpdatedAt()).isEqualTo(LATER.plusSeconds(10));
        assertThat(request.getCreatedAt()).isEqualTo(NOW);
    }

    @Test
    void shouldKeepOriginalValuesWhenEditIsInvalid() {
        ServiceRequest request = newRequest();

        assertThatThrownBy(() -> request.edit(" ", "desc", "cat", Priority.LOW, LATER))
                .isInstanceOf(DomainValidationException.class);
        assertThat(request.getTitle()).isEqualTo("Printer broken");
    }
}
