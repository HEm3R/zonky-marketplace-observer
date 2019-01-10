package cz.chalupa.zonky.marketplace.observer;

import java.time.ZoneOffset;
import java.util.TimeZone;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ZonkyMarketplaceObserverApplicationTest {

    @Test
    void applicationContextShouldLoad() {
        log.info("Application context loaded");
    }

    @Test
    void defaultTimeZoneShouldBeUTC() {
        assertThat(TimeZone.getDefault()).isEqualTo(TimeZone.getTimeZone(ZoneOffset.UTC));
    }
}
