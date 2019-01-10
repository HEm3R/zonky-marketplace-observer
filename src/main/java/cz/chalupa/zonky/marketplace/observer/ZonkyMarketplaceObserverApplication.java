package cz.chalupa.zonky.marketplace.observer;

import java.time.ZoneOffset;
import java.util.TimeZone;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ZonkyMarketplaceObserverApplication implements CommandLineRunner {

    static {
        // Set default timezone to UTC
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
    }

    @Override
    public void run(String... args) {
        log.info("Zonky Marketplace Observer Application");
    }

    public static void main(String[] args) {
        SpringApplication.run(ZonkyMarketplaceObserverApplication.class, args);
    }
}
