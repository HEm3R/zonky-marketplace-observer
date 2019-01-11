package cz.chalupa.zonky.marketplace.observer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import cz.chalupa.zonky.marketplace.observer.client.ZonkyLoanDTO;
import cz.chalupa.zonky.marketplace.observer.client.ZonkyMarketplaceApi;
import cz.chalupa.zonky.marketplace.observer.client.ZonkyRetrofitClient;
import cz.chalupa.zonky.marketplace.observer.handlers.ObservationHandler;
import net.jadler.stubbing.server.jdk.JdkStubHttpServer;
import okhttp3.Headers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import retrofit2.Call;
import retrofit2.Response;

import static net.jadler.Jadler.closeJadler;
import static net.jadler.Jadler.initJadlerUsing;
import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.port;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketplaceObserverTest {

    @Mock private ZonkyMarketplaceApi marketplaceApi;
    @Mock private ObservationHandler handler;

    private MarketplaceObserver observer;

    @Test
    void shouldCompleteObservationIfNrOfLoansInBatchIsSmallerThanBatchSize() throws IOException {
        observer = new MarketplaceObserver(marketplaceApi, Collections.singletonList(handler), 2);

        Response<List<ZonkyLoanDTO>> response = response();
        Call<List<ZonkyLoanDTO>> call = call();
        Headers headers = mock(Headers.class);

        when(call.execute()).thenReturn(response);
        when(marketplaceApi.getLoans(0, 2)).thenReturn(call);
        when(response.headers()).thenReturn(headers);
        when(headers.get("X-Total")).thenReturn("1");

        List<ZonkyLoanDTO> loans = Collections.singletonList(ZonkyLoanDTO.builder().id(1L).build());
        when(response.body()).thenReturn(loans);

        observer.observe();

        verify(handler).started();
        verify(handler).handleBatch(loans);
        verify(handler).completed();
        verify(handler, never()).totalChanged();
        verify(handler, never()).failed();
    }

    @Test
    void shouldCompleteObservationIfLastBatchIsEmpty() throws IOException {
        observer = new MarketplaceObserver(marketplaceApi, Collections.singletonList(handler), 1);

        Response<List<ZonkyLoanDTO>> response1 = response();
        Response<List<ZonkyLoanDTO>> response2 = response();
        Call<List<ZonkyLoanDTO>> call1 = call();
        Call<List<ZonkyLoanDTO>> call2 = call();
        Headers headers = mock(Headers.class);

        when(call1.execute()).thenReturn(response1);
        when(call2.execute()).thenReturn(response2);
        doReturn(call1).when(marketplaceApi).getLoans(0, 1);
        doReturn(call2).when(marketplaceApi).getLoans(1, 1);
        when(response1.headers()).thenReturn(headers);
        when(response2.headers()).thenReturn(headers);
        when(headers.get("X-Total")).thenReturn("1");

        List<ZonkyLoanDTO> loans = Collections.singletonList(ZonkyLoanDTO.builder().id(1L).build());
        when(response1.body()).thenReturn(loans);
        when(response2.body()).thenReturn(Collections.emptyList());

        observer.observe();

        verify(handler).started();
        verify(handler).handleBatch(loans);
        verify(handler).completed();
        verify(handler, never()).totalChanged();
        verify(handler, never()).failed();
    }

    @Test
    void shouldNotifyAboutTotalChangesIdTotalChangedDuringDownload() throws IOException {
        observer = new MarketplaceObserver(marketplaceApi, Collections.singletonList(handler), 1);

        Response<List<ZonkyLoanDTO>> response1 = response();
        Response<List<ZonkyLoanDTO>> response2 = response();
        Response<List<ZonkyLoanDTO>> response3 = response();
        Call<List<ZonkyLoanDTO>> call1 = call();
        Call<List<ZonkyLoanDTO>> call2 = call();
        Call<List<ZonkyLoanDTO>> call3 = call();
        Headers headers1 = mock(Headers.class);
        Headers headers2 = mock(Headers.class);

        when(call1.execute()).thenReturn(response1);
        when(call2.execute()).thenReturn(response2);
        when(call3.execute()).thenReturn(response3);
        doReturn(call1).when(marketplaceApi).getLoans(0, 1);
        doReturn(call2).when(marketplaceApi).getLoans(1, 1);
        doReturn(call3).when(marketplaceApi).getLoans(2, 1);
        when(response1.headers()).thenReturn(headers1);
        when(response2.headers()).thenReturn(headers2);
        when(response3.headers()).thenReturn(headers2);
        when(headers1.get("X-Total")).thenReturn("1");
        when(headers2.get("X-Total")).thenReturn("2");

        List<ZonkyLoanDTO> loans1 = Collections.singletonList(ZonkyLoanDTO.builder().id(1L).build());
        List<ZonkyLoanDTO> loans2 = Collections.singletonList(ZonkyLoanDTO.builder().id(2L).build());
        when(response1.body()).thenReturn(loans1);
        when(response2.body()).thenReturn(loans2);
        when(response3.body()).thenReturn(null);

        observer.observe();

        verify(handler).started();
        verify(handler).handleBatch(loans1);
        verify(handler).handleBatch(loans2);
        verify(handler).completed();
        verify(handler).totalChanged();
        verify(handler, never()).failed();
    }

    @Test
    void shouldFailIfBatchCannotBeLoaded() throws IOException {
        observer = new MarketplaceObserver(marketplaceApi, Collections.singletonList(handler), 1);

        Call<List<ZonkyLoanDTO>> call = call();

        when(call.execute()).thenThrow(new IOException());
        when(marketplaceApi.getLoans(0, 1)).thenReturn(call);

        observer.observe();

        verify(handler).started();
        verify(handler).failed();
        verify(handler, never()).handleBatch(any());
        verify(handler, never()).completed();
        verify(handler, never()).totalChanged();
    }

    @Test
    void shouldRetryBatchDownload() throws IOException {
        observer = new MarketplaceObserver(marketplaceApi, Collections.singletonList(handler), 2);

        Response<List<ZonkyLoanDTO>> response = response();
        Call<List<ZonkyLoanDTO>> call = call();
        Headers headers = mock(Headers.class);

        when(call.execute()).thenThrow(new IOException()).thenReturn(response);
        when(marketplaceApi.getLoans(0, 2)).thenReturn(call);
        when(response.headers()).thenReturn(headers);
        when(headers.get("X-Total")).thenReturn("1");

        List<ZonkyLoanDTO> loans = Collections.singletonList(ZonkyLoanDTO.builder().id(1L).build());
        when(response.body()).thenReturn(loans);

        observer.observe();

        verify(handler).started();
        verify(handler).handleBatch(loans);
        verify(handler).completed();
        verify(handler, never()).totalChanged();
        verify(handler, never()).failed();
    }

    @Nested
    class MarketplaceObserverParserTest {

        @Captor ArgumentCaptor<List<ZonkyLoanDTO>> captor;

        private ZonkyMarketplaceApi api;

        @BeforeEach
        void setUp() {
            initJadlerUsing(new JdkStubHttpServer());
            api = ZonkyRetrofitClient.builder().baseUrl("http://localhost:" + port()).build().createMarketplaceApi();
        }

        @AfterEach
        void tearDown() {
            closeJadler();
        }

        @Test
        void shouldHandleAndParseRealJsonResponse() {
            String response = ""
                    + "["
                    + "  {"
                    + "    \"id\": 1,"
                    + "    \"url\": \"https://app.zonky.cz/loan/1\","
                    + "    \"name\": \"Loan refinancing\","
                    + "    \"story\": \"Dear investors, ...\","
                    + "    \"purpose\": \"6\","
                    + "    \"photos\": ["
                    + "      {"
                    + "        \"name\": \"6\","
                    + "        \"url\": \"/loans/31959/photos/1987\""
                    + "      }"
                    + "    ],"
                    + "    \"nickName\": \"zonky0\","
                    + "    \"termInMonths\": 42,"
                    + "    \"interestRate\": 0.0599,"
                    + "    \"rating\": \"AAA\","
                    + "    \"topped\": null,"
                    + "    \"amount\": 200000,"
                    + "    \"remainingInvestment\": 152600,"
                    + "    \"reservedAmount\": 1000,"
                    + "    \"investmentRate\": 0.237,"
                    + "    \"covered\": false,"
                    + "    \"datePublished\": \"2016-04-19T18:25:41.208+02:00\","
                    + "    \"published\": true,"
                    + "    \"deadline\": \"2016-04-26T18:23:53.101+02:00\","
                    + "    \"investmentsCount\": 72,"
                    + "    \"questionsCount\": 3,"
                    + "    \"region\": \"6\","
                    + "    \"mainIncomeType\": \"EMPLOYMENT\","
                    + "    \"insuranceActive\": true,"
                    + "    \"insuranceHistory\": ["
                    + "      {"
                    + "        \"policyPeriodFrom\": \"2016-04-18\","
                    + "        \"policyPeriodTo\": \"2016-04-19\""
                    + "      }"
                    + "    ]"
                    + "  }"
                    + "]"
                    + "";

            onRequest().havingMethodEqualTo("GET").havingPathEqualTo("/loans/marketplace").respond()
                    .withStatus(200)
                    .withHeader("X-Total", "1")
                    .withBody(response);

            observer = new MarketplaceObserver(api, Collections.singletonList(handler), 2);
            observer.observe();

            verify(handler).started();
            verify(handler).completed();
            verify(handler, never()).totalChanged();
            verify(handler, never()).failed();

            verify(handler).handleBatch(captor.capture());
            assertThat(captor.getValue().get(0)).satisfies(l -> {
                assertThat(l.getId()).isEqualTo(1L);
            });
        }
    }

    @SuppressWarnings("unchecked")
    private static Response<List<ZonkyLoanDTO>> response() {
        return mock(Response.class);
    }

    @SuppressWarnings("unchecked")
    private static Call<List<ZonkyLoanDTO>> call() {
        return mock(Call.class);
    }
}
