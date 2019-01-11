package cz.chalupa.zonky.marketplace.observer;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import cz.chalupa.zonky.marketplace.observer.client.ZonkyMarketplaceApi;
import cz.chalupa.zonky.marketplace.observer.client.ZonkyRetrofitClient;
import cz.chalupa.zonky.marketplace.observer.client.ZonkyRetrofitClient.ZonkyRetrofitClientBuilder;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class ZonkyMarketplaceObserverConfiguration {

    @Bean
    public ZonkyRetrofitClient zonkyRetrofitClient(
            @NonNull @Value("${zonky.api.baseUrl}") String zonkyApiBaseUrl,
            @Value("${zonky.api.timeout:#{null}}") Optional<Integer> timeout,
            @Value("${zonky.api.timeoutUnit:#{null}}") Optional<TimeUnit> timeoutUnit) {

        ZonkyRetrofitClientBuilder builder = ZonkyRetrofitClient.builder().baseUrl(zonkyApiBaseUrl);
        timeout.ifPresent(builder::timeoutValue);
        timeoutUnit.ifPresent(builder::timeoutUnit);
        return builder.build();
    }

    @Bean
    public ZonkyMarketplaceApi zonkyMarketplaceApi(@NonNull ZonkyRetrofitClient client) {
        return client.createMarketplaceApi();
    }
}
