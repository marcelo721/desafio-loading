package br.ufc.loading.requests.domain.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class RequestStatusTest {

    @ParameterizedTest
    @CsvSource({
            "OPEN, IN_PROGRESS, true",
            "IN_PROGRESS, COMPLETED, true",
            "OPEN, COMPLETED, false",
            "OPEN, OPEN, false",
            "IN_PROGRESS, OPEN, false",
            "IN_PROGRESS, IN_PROGRESS, false",
            "COMPLETED, OPEN, false",
            "COMPLETED, IN_PROGRESS, false",
            "COMPLETED, COMPLETED, false"
    })
    void shouldOnlyAllowTheNextStepOfTheFlow(RequestStatus current, RequestStatus target, boolean expected) {
        assertThat(current.canTransitionTo(target)).isEqualTo(expected);
    }

    @Test
    void shouldExposeNextStatusUntilCompleted() {
        assertThat(RequestStatus.OPEN.next()).contains(RequestStatus.IN_PROGRESS);
        assertThat(RequestStatus.IN_PROGRESS.next()).contains(RequestStatus.COMPLETED);
        assertThat(RequestStatus.COMPLETED.next()).isEmpty();
    }
}
