package com.nikhil.coingecko;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ViewAllActivity extends AppCompatActivity {
    private ListView cryptocurrencyListView;
    private CryptoAdapter cryptoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        cryptocurrencyListView = findViewById(R.id.cryptocurrencyListView);
        cryptoAdapter = new CryptoAdapter(this, new ArrayList<>());
        cryptocurrencyListView.setAdapter(cryptoAdapter);

        // Fetch cryptocurrency data from CoinMarketCap API
        fetchData();
    }

    private static class FetchDataAsyncTask extends AsyncTask<Void, Void, List<CryptoModel>> {
        private WeakReference<ViewAllActivity> activityReference;

        FetchDataAsyncTask(ViewAllActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<CryptoModel> doInBackground(Void... voids) {
            List<CryptoModel> cryptoList = new ArrayList<>();

            try {
                // Create a Retrofit instance
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://pro-api.coinmarketcap.com/") // Update the base URL as needed
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                // Create the CryptoService using the Retrofit instance
                CryptoService cryptoService = retrofit.create(CryptoService.class);

                // Make the API call
                Call<CryptoApiResponse> call = cryptoService.getCryptocurrencyList();
                Response<CryptoApiResponse> response = call.execute();

                if (response.isSuccessful()) {
                    // If the response is successful, get the data from the response
                    CryptoApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        cryptoList = apiResponse.getData();
                    }
                } else {
                    // Handle unsuccessful response
                    // You might want to log an error or handle it in a way that fits your app
                    Log.e("CryptoApp", "API request failed: " + response.message());
                }
            } catch (IOException e) {
                // Handle network-related exceptions
                e.printStackTrace();
            }

            return cryptoList;
        }


        @Override
        protected void onPostExecute(List<CryptoModel> cryptoList) {
            super.onPostExecute(cryptoList);

            // Update the adapter with the fetched data
            ViewAllActivity activity = activityReference.get();
            if (activity != null) {
                activity.cryptoAdapter.clear();
                activity.cryptoAdapter.addAll(cryptoList);
                activity.cryptoAdapter.notifyDataSetChanged();
            }
        }
    }

    private void fetchData() {
        // Example using the static nested AsyncTask
        new FetchDataAsyncTask(this).execute();
    }
}
