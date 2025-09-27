package com.example.tridots.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tridots.models.BannerImage;
import com.example.tridots.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final List<BannerImage> bannerImages;
    private final Context context;

    public BannerAdapter(List<BannerImage> bannerImages, Context context) {
        this.bannerImages = bannerImages;
        this.context = context;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner_image, parent, false); // Create item_banner_image.xml
        return new BannerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        BannerImage currentImage = bannerImages.get(position);
        String imageUrl = "https://lionsgoldencircle.com" + currentImage.getImagePath(); // Construct full URL

        // Use Picasso or Glide to load the image
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ad_placeholder) // Optional placeholder image
                .error(R.drawable.error_placeholdeers)         // Optional error image
                .fit()
                .centerCrop()
                .into(holder.bannerImageView);
    }

    @Override
    public int getItemCount() {
        return bannerImages.size();
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImageView;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImageView = itemView.findViewById(R.id.bannerImageView); // ID in item_banner_image.xml
        }
    }
}