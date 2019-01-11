package cz.chalupa.zonky.marketplace.observer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import cz.chalupa.zonky.marketplace.observer.client.ZonkyLoanDTO;
import cz.chalupa.zonky.marketplace.observer.client.ZonkyMarketplaceApi;
import cz.chalupa.zonky.marketplace.observer.handlers.ObservationHandler;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;

@Slf4j
@Service
@AllArgsConstructor
public class MarketplaceObserver {

    // sufficient for application scenario, for stable load we can use Hystrix for example
    private static final int MAX_TRIES_FOR_BATCH = 2;

    @NonNull private final ZonkyMarketplaceApi marketplaceApi;
    @NonNull private final List<ObservationHandler> handlers;
    @Value("${zonky.api.batchSize}") private final int batchSize;

    public void observe() {
        log.info("action=observerMarketplace status=START");
        notifyStarted();

        int page = 0;
        boolean failure = false;
        boolean completed = false;

        Long total = null;

        while(!completed && !failure) {
            log.info("action=getLoansBatch status=START page={} batchSize={}", page, batchSize);
            Response<List<ZonkyLoanDTO>> response = getLoansBatch(page);
            if (response != null) {
                Long currentTotal = getCurrentTotal(response);
                checkTotalChanges(total, currentTotal);
                total = currentTotal;

                List<ZonkyLoanDTO> loans = response.body() != null ? response.body() : Collections.emptyList();
                handleBatch(loans);
                completed = isCompleted(loans);
                log.info(
                        "action=getLoansBatch status=FINISH page={} of={} batchSize={} total={} loans={} completed={}",
                        page, totalPages(total), batchSize, total, loans.size(), completed
                );
            } else {
                failure = true;
                log.info("action=getLoansBatch status=FAILURE page={} batchSize={}", page, batchSize);
                notifyFailed();
            }
            page++;
        }

        if (completed) {
            notifyCompleted();
        }
        log.info("action=observerMarketplace status={}", failure ? "FAILURE" : "FINISH");
    }

    private Response<List<ZonkyLoanDTO>> getLoansBatch(int page) {
        for (int tryNr = 1; tryNr <= MAX_TRIES_FOR_BATCH; tryNr++) {
            Response<List<ZonkyLoanDTO>> response = getLoansBatch(page, tryNr);
            if (response != null) {
                return response;
            }
        }
        return null;
    }

    private Response<List<ZonkyLoanDTO>> getLoansBatch(int page, int tryNr) {
        try {
            log.info("action=tryToGetLoansBatch status=START page={} batchSize={} try={}", page, batchSize, tryNr);
            Response<List<ZonkyLoanDTO>> response = marketplaceApi.getLoans(page, batchSize).execute();
            log.info("action=tryToGetLoansBatch status=FINISH page={} batchSize={} try={}", page, batchSize, tryNr);
            return response;
        } catch (IOException e) {
            log.info("action=tryToGetLoansBatch status=FAILURE page={} batchSize={} try={}", page, batchSize, tryNr, e);
        }
        return null;
    }

    private Long getCurrentTotal(Response<List<ZonkyLoanDTO>> response) {
        String total = response.headers().get("X-Total");
        return total != null ? Long.parseLong(total) : null;
    }

    private void checkTotalChanges(Long total, Long currentTotal) {
        if (total != null && !total.equals(currentTotal)) {
            log.warn("event=totalNumberOfLoansChangeDuringDownload total={} currentTotal={}", total, currentTotal);
            notifyTotalChanged();
        }
    }

    private long totalPages(long total) {
        return total / batchSize + 1;
    }

    private boolean isCompleted(List<ZonkyLoanDTO> loans) {
        // This is best check to load all loans for now:
        // - in case there is lesser loans than requested, we can finish
        // - in case the page is full, there can be new record on next page (even if all pages loaded) as new loans may be created during current download
        //
        // The problem may be here in case nr of new loans > batch, so the download of all loans will never end
        return loans.size() < batchSize;
    }

    private void notifyStarted() {
        handlers.forEach(ObservationHandler::started);
    }

    private void notifyCompleted() {
        handlers.forEach(ObservationHandler::completed);
    }

    private void notifyFailed() {
        handlers.forEach(ObservationHandler::failed);
    }

    private void notifyTotalChanged() {
        handlers.forEach(ObservationHandler::totalChanged);
    }

    private void handleBatch(List<ZonkyLoanDTO> loans) {
        handlers.forEach(h -> h.handleBatch(loans));
    }
}
