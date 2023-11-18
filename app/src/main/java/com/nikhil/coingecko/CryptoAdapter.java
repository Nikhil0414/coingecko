package com.nikhil.coingecko;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CryptoAdapter extends ArrayAdapter<CryptoModel> {
    public CryptoAdapter(Context context, List<CryptoModel> cryptoList) {
        super(context, 0, cryptoList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the view is being reused, otherwise inflate a new view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.crypto_list_item, parent, false);
        }

        // Get the CryptoModel for this position
        CryptoModel crypto = getItem(position);

        // Find the TextViews in the layout
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView symbolTextView = convertView.findViewById(R.id.symbolTextView);
        TextView priceTextView = convertView.findViewById(R.id.priceTextView);
        ImageView logoImageView = convertView.findViewById(R.id.logoImageView);

        // Set the values to the TextViews
        if (crypto != null) {
            nameTextView.setText(crypto.getName());
            symbolTextView.setText(crypto.getSymbol());

            String imageUrl = "https://s2.coinmarketcap.com/static/img/coins/64x64/1.png"; // Replace with your API URL

            Picasso.get().load(imageUrl).into(logoImageView);


            // Access the nested structure to get the price
            CryptoModel.Quote.USD usd = crypto.getQuote().getUSD();
            if (usd != null) {
                String formattedPrice = String.format("$%.0f USD", usd.getPrice());
                priceTextView.setText(formattedPrice);}
        }

        return convertView;
    }

}
