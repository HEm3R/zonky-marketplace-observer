package cz.chalupa.zonky.marketplace.observer.client;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ZonkyRetrofitClient {

    @NonNull private final Retrofit retrofit;

    public static ZonkyRetrofitClientBuilder builder() {
        return new ZonkyRetrofitClientBuilder();
    }

    public ZonkyMarketplaceApi createMarketplaceApi() {
        return retrofit.create(ZonkyMarketplaceApi.class);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ZonkyRetrofitClientBuilder {

        private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule()).registerModule(new Jdk8Module());

        private static final int DEFAULT_TIMEOUT_VALUE = 10;
        private static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.SECONDS;

        private String baseUrl;
        private int timeoutValue = DEFAULT_TIMEOUT_VALUE;
        private TimeUnit timeoutUnit = DEFAULT_TIMEOUT_UNIT;

        /**
         * Sets {@code baseUrl} of API.
         *
         * @param baseUrl base url where API is located (http://...)
         * @return this
         */
        public ZonkyRetrofitClientBuilder baseUrl(@NonNull String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Sets {@code timeoutValue} for read and connection.
         *
         * <ul>
         * <li>See {@link OkHttpClient.Builder#readTimeout(long, TimeUnit)}</li>
         * <li>See {@link OkHttpClient.Builder#connectTimeout(long, TimeUnit)}</li>
         * </ul>
         *
         * @param timeoutValue value of read & connection timeout
         * @return this
         * @see OkHttpClient.Builder
         */
        public ZonkyRetrofitClientBuilder timeoutValue(int timeoutValue) {
            this.timeoutValue = timeoutValue;
            return this;
        }

        /**
         * Sets {@code timeoutUnit} for read and connection.
         *
         * <ul>
         * <li>See {@link OkHttpClient.Builder#readTimeout(long, TimeUnit)}</li>
         * <li>See {@link OkHttpClient.Builder#connectTimeout(long, TimeUnit)}</li>
         * </ul>
         *
         * @param timeoutUnit unit of read & connection timeout
         * @return this
         * @see OkHttpClient.Builder
         */
        public ZonkyRetrofitClientBuilder timeoutUnit(@NonNull TimeUnit timeoutUnit) {
            this.timeoutUnit = timeoutUnit;
            return this;
        }

        public ZonkyRetrofitClient build() {
            Objects.requireNonNull(baseUrl, "baseUrl must not be null");

            OkHttpClient client = buildClient(timeoutValue, timeoutUnit);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(JacksonConverterFactory.create(MAPPER))
                    .client(client).build();

            return new ZonkyRetrofitClient(retrofit);
        }

        private static OkHttpClient buildClient(int timeoutValue, TimeUnit timeoutUnit) {
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                    .readTimeout(timeoutValue, timeoutUnit)
                    .connectTimeout(timeoutValue, timeoutUnit);
            return okHttpClientBuilder.addInterceptor(new JsonHeadersInterceptor()).build();
        }

        static class JsonHeadersInterceptor implements Interceptor {

            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder builder = request.newBuilder();
                addHeaderIfNull(request, builder, "Accept");
                addHeaderIfNull(request, builder, "Content-Type");
                return chain.proceed(builder.build());
            }

            private void addHeaderIfNull(Request request, Request.Builder builder, String headerName) {
                if (request.header(headerName) == null) {
                    builder.addHeader(headerName, "application/json");
                }
            }
        }
    }
}
