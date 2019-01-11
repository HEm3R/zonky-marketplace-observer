package cz.chalupa.zonky.marketplace.observer.client;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ZonkyMarketplaceApi {

    @GET("/loans/marketplace")
    Call<List<ZonkyLoanDTO>> getLoans(@Header("X-Page") int page, @Header("X-Size") int size);
}
