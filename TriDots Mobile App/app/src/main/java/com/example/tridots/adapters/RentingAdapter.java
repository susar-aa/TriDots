package com.example.tridots.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tridots.detailed_screens.DetailedActivity;
import com.example.tridots.R;
import com.example.tridots.models.RentingAd;

import java.util.List;

public class RentingAdapter extends RecyclerView.Adapter<RentingAdapter.RentingViewHolder> {

    private List<RentingAd> rentingAds;
    private Context context;

    public RentingAdapter(List<RentingAd> rentingAds, Context context) {
        this.rentingAds = rentingAds;
        this.context = context;
    }

    @NonNull
    @Override
    public RentingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use the new reusable layout file
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_featured_ad, parent, false);
        return new RentingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RentingViewHolder holder, int position) {
        RentingAd ad = rentingAds.get(position);

        // Set data to the new views
        holder.title.setText(ad.getProductName());
        holder.subtitle.setText(ad.getBrand()); // Using brand as the subtitle
        holder.price.setText("Rs: " + ad.getPricePerDay() + " / Day"); // Highlighting price per day

        // Load image using Glide
        String imageUrl = ad.getProductImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ad_placeholder)
                    .error(R.drawable.image_error_placeholder)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ad_placeholder);
        }

        // Click listener remains the same
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailedActivity.class);
            intent.putExtra("image_url", ad.getProductImage());
            intent.putExtra("product_name", ad.getProductName());
            intent.putExtra("brand", ad.getBrand());
            intent.putExtra("model", ad.getModel());
            intent.putExtra("description", ad.getProductDescription());
            intent.putExtra("price_per_hour", String.valueOf(ad.getPricePerHour()));
            intent.putExtra("price_per_day", String.valueOf(ad.getPricePerDay()));
            intent.putExtra("location", ad.getProductLocation());
            intent.putExtra("availability_status", ad.getAvailabilityStatus());
            intent.putExtra("average_rating", ad.getAverageRating());
            intent.putExtra("review_count", ad.getReviewCount());
            intent.putExtra("keywords", ad.getKeywords());
            intent.putExtra("ad_type", "renting");
            intent.putExtra("ad_id", ad.getRentId());
            intent.putExtra("lister_id", ad.getListerId());
            intent.putExtra("lister_name", ad.getListerName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return rentingAds.size();
    }

    // Updated ViewHolder to match the new layout
    public static class RentingViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, subtitle, price;

        public RentingViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            title = itemView.findViewById(R.id.item_title);
            subtitle = itemView.findViewById(R.id.item_subtitle);
            price = itemView.findViewById(R.id.item_price);
        }
    }
}
