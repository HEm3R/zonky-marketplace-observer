package cz.chalupa.zonky.marketplace.observer;

import java.time.ZoneOffset;
import java.util.TimeZone;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class ZonkyMarketplaceObserverApplication {

    static {
        // Set default timezone to UTC
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
    }

    public static void main(String[] args) {
        SpringApplication.run(ZonkyMarketplaceObserverApplication.class, args);
    }
}
