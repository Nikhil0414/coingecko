package com.nikhil.coingecko;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private ListView cryptocurrencyListView;
    private CryptoAdapter cryptoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(android.R.color.black));
        }

        ImageView bitlogoImageView = findViewById(R.id.bitlogoImageView);
        String imageUrl2 = "https://s2.coinmarketcap.com/static/img/coins/64x64/1.png";
        Picasso.get().load(imageUrl2).into(bitlogoImageView);

        cryptocurrencyListView = findViewById(R.id.cryptocurrencyListView);
        cryptoAdapter = new CryptoAdapter(this, new ArrayList<>());
        cryptocurrencyListView.setAdapter(cryptoAdapter);
        cryptocurrencyListView.setDivider(null);


        // Fetch cryptocurrency data from CoinMarketCap API
        fetchData();

}

    private static class FetchDataAsyncTask extends AsyncTask<Void, Void, List<CryptoModel>> {
        private WeakReference<MainActivity> activityReference;

        FetchDataAsyncTask(MainActivity context) {
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

            // Update the adapter with the fetched data, limiting to 20 items
            MainActivity activity = activityReference.get();
            if (activity != null) {
                int limit = 21;

                // Check if the fetched list has more than 20 items
                if (cryptoList.size() > limit) {
                    // Create a sublist containing the first 20 items
                    List<CryptoModel> limitedList = cryptoList.subList(0, limit);

                    // Display the content of the first item in the CardView
                    if (!limitedList.isEmpty()) {
                        CryptoModel firstItem = limitedList.get(0);
                        updateCardViewContent(activity, firstItem);
                    }

                    // Clear and update the adapter with the remaining items
                    activity.cryptoAdapter.clear();
                    activity.cryptoAdapter.addAll(limitedList.subList(1, limitedList.size()));
                } else {
                    // If the fetched list has 20 or fewer items, use it directly
                    activity.cryptoAdapter.clear();
                    activity.cryptoAdapter.addAll(cryptoList.subList(1, cryptoList.size()));

                    // Display the content of the first item in the CardView
                    if (!cryptoList.isEmpty()) {
                        CryptoModel firstItem = cryptoList.get(0);
                        updateCardViewContent(activity, firstItem);
                    }
                }

                // Notify the adapter that the data set has changed
                activity.cryptoAdapter.notifyDataSetChanged();
            }
        }


        private void updateCardViewContent(MainActivity activity, CryptoModel cryptoModel) {
            // Update the content of the CardView with the first item
            CardView cardView = activity.findViewById(R.id.cardView);
            TextView symbolTextView = activity.findViewById(R.id.symbolTextView);
            TextView nameTextView = activity.findViewById(R.id.nameTextView);
            TextView priceTextView = activity.findViewById(R.id.priceTextView);

            String symbol =  cryptoModel.getSymbol();
            String name =  cryptoModel.getName();

            symbolTextView.setText(symbol);
            nameTextView.setText(name);

            CryptoModel.Quote.USD usd = cryptoModel.getQuote().getUSD();
            if (usd != null) {

                String formattedPrice = String.format("$%.0f USD", usd.getPrice());
                priceTextView.setText(formattedPrice);}

        }
    }

    private void fetchData() {
        // Example using the static nested AsyncTask
        new FetchDataAsyncTask(this).execute();
    }

    public void viewAllClicked(View view) {
        // Start the new activity when the TextView is clicked
        startActivity(new Intent(MainActivity.this, ViewAllActivity.class));
    }
}
