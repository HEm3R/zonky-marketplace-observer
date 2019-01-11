package cz.chalupa.zonky.marketplace.observer.client;

import java.io.IOException;

import cz.chalupa.zonky.marketplace.observer.client.ZonkyRetrofitClient.ZonkyRetrofitClientBuilder.JsonHeadersInterceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZonkyRetrofitClientTest {

    @Nested
    class JsonHeaderInterceptorTest {

        private final JsonHeadersInterceptor intercept = new JsonHeadersInterceptor();

        @Mock private Chain chain;
        @Mock private Request request;
        @Mock private Request.Builder builder;

        @BeforeEach
        void setUp() {
            when(chain.request()).thenReturn(request);
            when(request.newBuilder()).thenReturn(builder);
        }

        @Test
        void shouldAddHeaders() throws IOException {
            intercept.intercept(chain);
            verify(builder).addHeader("Accept", "application/json");
            verify(builder).addHeader("Content-Type", "application/json");
        }

        @Test
        void shouldNotAddHeadersIfAlreadyPresent() throws IOException {
            when(request.header(any())).thenAnswer(invocation -> {
                if ("Accept".equals(invocation.getArgument(0).toString())) return "application/xml";
                if ("Content-Type".equals(invocation.getArgument(0).toString())) return "application/xml";
                return null;
            });

            intercept.intercept(chain);

            verify(builder, never()).addHeader(anyString(), anyString());
        }
    }
}
