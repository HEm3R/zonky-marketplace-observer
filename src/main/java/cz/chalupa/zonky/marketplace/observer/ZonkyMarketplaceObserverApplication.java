package cz.chalupa.zonky.marketplace.observer;

import java.time.ZoneOffset;
import java.util.Scanner;
import java.util.TimeZone;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class ZonkyMarketplaceObserverApplication implements CommandLineRunner {

    static {
        // Set default timezone to UTC
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
    }

    @Override
    public void run(String... args) {
        System.out.println();
        System.out.println("+----------------------------+");
        System.out.println("| Zonky Marketplace Observer |");
        System.out.println("+----------------------------+");
        System.out.println("| Type 'exit' to quit        |");
        System.out.println("+----------------------------+");
        System.out.println();

        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()) {
            String input = sc.nextLine();
            if ("exit".equals(input)) {
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ZonkyMarketplaceObserverApplication.class, args);
    }
}
