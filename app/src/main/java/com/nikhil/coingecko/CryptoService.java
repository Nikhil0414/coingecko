package com.nikhil.coingecko;

// CryptoService.java
// CryptoService.java
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface CryptoService {
    @Headers("X-CMC_PRO_API_KEY: db49de8c-33ed-484c-8392-e2171ffa8794")
    @GET("v1/cryptocurrency/listings/latest")
    Call<CryptoApiResponse> getCryptocurrencyList();
}

