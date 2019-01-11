package cz.chalupa.zonky.marketplace.observer.services;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import cz.chalupa.zonky.marketplace.observer.client.ZonkyLoanDTO;
import cz.chalupa.zonky.marketplace.observer.handlers.InMemoryObservationHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ObservationService {

    @NonNull private final InMemoryObservationHandler handler;

    public ObservationInfo getObservation(int limit, int offset) {
        return ObservationInfo.builder()
                .loans(paginateLoans(limit, offset))
                .started(handler.getStarted())
                .finished(handler.getFinished())
                .failed(handler.isFailed())
                .completed(handler.isCompleted())
                .totalChanged(handler.isTotalChanged())
                .totalObserverLoans(handler.getLoans().size())
                .build();
    }

    private List<ZonkyLoanDTO> paginateLoans(int limit, int offset) {
        int size = handler.getLoans().size();
        if (offset >= size) {
            return Collections.emptyList();
        }
        int toOffset = offset + limit;
        if (toOffset > size) {
            toOffset = size;
        }
        return handler.getLoans().subList(offset, toOffset);
    }

    @Getter
    @Builder
    public static class ObservationInfo {

        private final List<ZonkyLoanDTO> loans;
        private final Instant started;
        private final Instant finished;
        private final boolean failed;
        private final boolean completed;
        private final boolean totalChanged;
        private final int totalObserverLoans;
    }
}
