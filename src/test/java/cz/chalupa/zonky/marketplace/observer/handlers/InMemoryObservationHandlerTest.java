package cz.chalupa.zonky.marketplace.observer.handlers;

import java.time.Instant;
import java.util.Collections;

import cz.chalupa.zonky.marketplace.observer.client.ZonkyLoanDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryObservationHandlerTest {

    private InMemoryObservationHandler handler;

    @BeforeEach
    void setUp() {
        handler = new InMemoryObservationHandler();
    }

    @Test
    void startedShouldCleanUpStateForNewObservation() {
        Instant start = Instant.now();
        handler.started();
        Instant finish = Instant.now();

        assertThat(handler.getLoans()).isEmpty();
        assertThat(handler.getStarted()).isBetween(start, finish);
        assertThat(handler.getFinished()).isNull();
        assertThat(handler.isCompleted()).isFalse();
        assertThat(handler.isFailed()).isFalse();
        assertThat(handler.isTotalChanged()).isFalse();
    }

    @Test
    void completedShouldCompleteObservationState() {
        handler.started();

        Instant start = Instant.now();
        handler.completed();
        Instant finish = Instant.now();

        assertThat(handler.getFinished()).isBetween(start, finish);
        assertThat(handler.isCompleted()).isTrue();
        assertThat(handler.isFailed()).isFalse();
        assertThat(handler.isTotalChanged()).isFalse();
    }

    @Test
    void failedShouldCompleteObservationStateAsFailed() {
        handler.started();

        Instant start = Instant.now();
        handler.failed();
        Instant finish = Instant.now();

        assertThat(handler.getFinished()).isBetween(start, finish);
        assertThat(handler.isCompleted()).isFalse();
        assertThat(handler.isFailed()).isTrue();
        assertThat(handler.isTotalChanged()).isFalse();
    }

    @Test
    void totalChangedShouldMarkTotalNumberOfLoansChanged() {
        handler.started();
        handler.totalChanged();

        assertThat(handler.getFinished()).isNull();
        assertThat(handler.isCompleted()).isFalse();
        assertThat(handler.isFailed()).isFalse();
        assertThat(handler.isTotalChanged()).isTrue();
    }

    @Test
    void handleBatchShouldAppendLoans() {
        ZonkyLoanDTO loan1 = ZonkyLoanDTO.builder().build();
        ZonkyLoanDTO loan2 = ZonkyLoanDTO.builder().build();

        handler.started();
        handler.handleBatch(Collections.singletonList(loan1));
        handler.handleBatch(Collections.singletonList(loan2));

        assertThat(handler.getLoans()).hasSize(2).containsExactly(loan1, loan2);
    }
}
