package com.example.tridots.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tridots.R;
import com.example.tridots.detailed_screens.VehicleDetailActivity; // Ensure this is the correct detail activity
import com.example.tridots.models.VehicleAd;

import java.util.List;
import java.util.Locale;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private final List<VehicleAd> vehicleAds;
    private final Context context;

    public VehicleAdapter(List<VehicleAd> vehicleAds, Context context) {
        this.vehicleAds = vehicleAds;
        this.context = context;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use the new reusable layout file
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_featured_ad, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        VehicleAd ad = vehicleAds.get(position);

        // Set data to the new views
        holder.title.setText(ad.getVehicleName());
        holder.subtitle.setText(ad.getLocation()); // Using location as the subtitle

        // Format the price based on the price type
        String priceText;
        if ("per_hour".equals(ad.getPriceType())) {
            priceText = String.format(Locale.US, "Rs. %,.0f / Hour", ad.getAmount());
        } else if ("per_day".equals(ad.getPriceType())) {
            priceText = String.format(Locale.US, "Rs. %,.0f / Day", ad.getAmount());
        } else if ("per_km".equals(ad.getPriceType())) {
            priceText = String.format(Locale.US, "Rs. %,.0f / KM", ad.getAmount());
        } else {
            priceText = String.format(Locale.US, "Rs. %,.2f", ad.getAmount());
        }
        holder.price.setText(priceText);

        // Handle multiple images, show the first one as a thumbnail
        String firstImageUrl = null;
        if (ad.getVehicleImages() != null && !ad.getVehicleImages().isEmpty()) {
            firstImageUrl = ad.getVehicleImages().split(",")[0].trim();
        }

        // Load image using Glide for consistency
        if (firstImageUrl != null && !firstImageUrl.isEmpty()) {
            Glide.with(context)
                    .load(firstImageUrl)
                    .placeholder(R.drawable.ad_placeholder)
                    .error(R.drawable.image_error_placeholder)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ad_placeholder);
        }

        // Set OnClickListener to open detail view
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VehicleDetailActivity.class);
            // Pass only the vehicle ID, the detail activity will fetch the rest
            intent.putExtra("vehicle_id", ad.getVehicleId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return vehicleAds.size();
    }

    // Updated ViewHolder to match the new item_featured_ad.xml layout
    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, subtitle, price;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            title = itemView.findViewById(R.id.item_title);
            subtitle = itemView.findViewById(R.id.item_subtitle);
            price = itemView.findViewById(R.id.item_price);
        }
    }
}
