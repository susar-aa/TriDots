package com.example.tridots.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tridots.R;
import com.example.tridots.models.ProductImage;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {

    private List<ProductImage> imageList;
    private String baseUrlForImages; // Base URL if image_url in DB is relative

    public ImageSliderAdapter(List<ProductImage> imageList, String baseUrlForImages) {
        this.imageList = imageList;
        this.baseUrlForImages = baseUrlForImages;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ProductImage image = imageList.get(position);

        String imageUrl = image.getImage_url();
        // Prepend base URL if your image_url from DB is relative (e.g., just "product1.jpg")
        // If image_url from DB is already a full URL (e.g., "http://example.com/images/product1.jpg"), then just use it directly.
        if (baseUrlForImages != null && !imageUrl.startsWith("http")) { // Basic check for relative path
            imageUrl = baseUrlForImages + imageUrl;
        }

        Glide.with(holder.imageView.getContext())
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ad_placeholder) // Placeholder while loading
                .error(R.drawable.image_error_placeholder) // Error image if loading fails
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewSlider);
        }
    }
}