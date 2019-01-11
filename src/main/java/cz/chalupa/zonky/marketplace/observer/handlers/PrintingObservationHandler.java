package cz.chalupa.zonky.marketplace.observer.handlers;

import java.util.List;

import cz.chalupa.zonky.marketplace.observer.client.ZonkyLoanDTO;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PrintingObservationHandler implements ObservationHandler {

    @Override
    public void started() {
        System.out.println("Observation started"); // NOSONAR
    }

    @Override
    public void completed() {
        System.out.println("Observation completed"); // NOSONAR
    }

    @Override
    public void failed() {
        System.err.println("Observation failed"); // NOSONAR
    }

    @Override
    public void totalChanged() {
        System.err.println("Total number of loans changed during observation"); // NOSONAR
    }

    @Override
    public void handleBatch(@NonNull List<ZonkyLoanDTO> loans) {
        loans.forEach(System.out::println); // NOSONAR
    }
}
