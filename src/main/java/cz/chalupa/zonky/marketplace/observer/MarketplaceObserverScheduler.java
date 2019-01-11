package cz.chalupa.zonky.marketplace.observer;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "marketplace.scheduler.enabled", matchIfMissing = true)
public class MarketplaceObserverScheduler {

    @NonNull MarketplaceObserver observer;

    @Scheduled(cron = "0 */5 * * * ?")
    public void runMarketplaceObservation() {
        log.info("action=runMarketplaceObservation status=START");
        observer.observe();
        log.info("action=runMarketplaceObservation status=FINISH");
    }
}
