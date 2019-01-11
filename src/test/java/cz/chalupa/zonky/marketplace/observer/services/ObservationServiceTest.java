package cz.chalupa.zonky.marketplace.observer.services;

import java.util.Arrays;
import java.util.List;

import cz.chalupa.zonky.marketplace.observer.client.ZonkyLoanDTO;
import cz.chalupa.zonky.marketplace.observer.handlers.InMemoryObservationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObservationServiceTest {

    private static final List<ZonkyLoanDTO> LOANS = Arrays.asList(
            ZonkyLoanDTO.builder().build(),
            ZonkyLoanDTO.builder().build(),
            ZonkyLoanDTO.builder().build(),
            ZonkyLoanDTO.builder().build(),
            ZonkyLoanDTO.builder().build()
    );

    private final InMemoryObservationHandler handler = new InMemoryObservationHandler();
    private final ObservationService service = new ObservationService(handler);

    @BeforeEach
    void setUp() {
        handler.started();
        handler.handleBatch(LOANS);
        handler.completed();
    }

    @Test
    void shouldProvideObservationInfo() {
        assertThat(service.getObservation(LOANS.size(), 0)).satisfies(info -> {
            assertThat(info.getLoans()).containsAll(LOANS);
            assertThat(info.getStarted()).isEqualTo(handler.getStarted());
            assertThat(info.getFinished()).isEqualTo(handler.getFinished());
            assertThat(info.getTotalObserverLoans()).isEqualTo(LOANS.size());
            assertThat(info.isCompleted()).isEqualTo(handler.isCompleted());
            assertThat(info.isFailed()).isEqualTo(handler.isFailed());
            assertThat(info.isTotalChanged()).isEqualTo(handler.isTotalChanged());
        });
    }

    @Test
    void shouldPaginateLoans() {
        assertThat(service.getObservation(0, 0).getLoans()).isEmpty();
        assertThat(service.getObservation(5, 0).getLoans()).hasSize(5);
        assertThat(service.getObservation(6, 0).getLoans()).hasSize(5);
        assertThat(service.getObservation(5, 1).getLoans()).hasSize(4);
        assertThat(service.getObservation(5, 2).getLoans()).hasSize(3);
        assertThat(service.getObservation(5, 3).getLoans()).hasSize(2);
        assertThat(service.getObservation(5, 4).getLoans()).hasSize(1);
        assertThat(service.getObservation(5, 5).getLoans()).isEmpty();
        assertThat(service.getObservation(1, 100).getLoans()).isEmpty();
    }
}
