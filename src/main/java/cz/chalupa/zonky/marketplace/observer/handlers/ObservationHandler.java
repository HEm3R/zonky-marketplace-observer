package cz.chalupa.zonky.marketplace.observer.handlers;

import java.util.List;

import cz.chalupa.zonky.marketplace.observer.client.ZonkyLoanDTO;
import lombok.NonNull;

public interface ObservationHandler {

    void started();
    void completed();
    void failed();
    void totalChanged();
    void handleBatch(@NonNull List<ZonkyLoanDTO> loans);
}
