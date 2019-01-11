package cz.chalupa.zonky.marketplace.observer.handlers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import cz.chalupa.zonky.marketplace.observer.client.ZonkyLoanDTO;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Getter
@Component
public class InMemoryObservationHandler implements ObservationHandler {

    private List<ZonkyLoanDTO> loans = new ArrayList<>();
    private Instant started;
    private Instant finished;
    private boolean failed;
    private boolean completed;
    private boolean totalChanged;

    @Override
    public void started() {
        loans = new ArrayList<>();
        failed = completed = totalChanged = false;
        started = Instant.now();
        finished = null;
    }

    @Override
    public void completed() {
        completed = true;
        finished = Instant.now();
    }

    @Override
    public void failed() {
        failed = true;
        finished = Instant.now();
    }

    @Override
    public void totalChanged() {
        totalChanged = true;
    }

    @Override
    public void handleBatch(@NonNull List<ZonkyLoanDTO> loans) {
        this.loans.addAll(loans);
    }
}
