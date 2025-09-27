package com.example.tridots.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tridots.R;
import com.example.tridots.models.Product;

import java.util.List;

public class VariantAdapter extends RecyclerView.Adapter<VariantAdapter.VariantViewHolder> {

    private Context context;
    private List<Product.Variant> variants;
    private Product.Variant selectedVariant; // To keep track of the currently selected item
    private OnVariantClickListener listener;

    // Interface for click events on variant items
    public interface OnVariantClickListener {
        void onVariantClick(Product.Variant variant);
    }

    public VariantAdapter(Context context, List<Product.Variant> variants, Product.Variant initialSelectedVariant, OnVariantClickListener listener) {
        this.context = context;
        this.variants = variants;
        this.selectedVariant = initialSelectedVariant; // Set initial selected variant
        this.listener = listener;
    }

    @NonNull
    @Override
    public VariantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single variant option
        View view = LayoutInflater.from(context).inflate(R.layout.item_variant_option, parent, false);
        return new VariantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VariantViewHolder holder, int position) {
        Product.Variant variant = variants.get(position);

        holder.variantName.setText(variant.getVariantName());

        // Load variant thumbnail image
        String imageUrl = variant.getVariantImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image) // Placeholder if image not loaded
                    .error(R.drawable.error_placeholdeers) // Error image if loading fails
                    .into(holder.variantThumbnail);
        } else {
            holder.variantThumbnail.setImageResource(R.drawable.placeholder_image); // Default if no URL
        }

        // Set the selected state based on the comparison with the current selectedVariant
        holder.itemView.setSelected(variant.equals(selectedVariant));

        // Set click listener for the entire item view
        holder.itemView.setOnClickListener(v -> {
            // Get the position of the previously selected item to unhighlight it
            int oldSelectedPosition = -1;
            if (selectedVariant != null) {
                for (int i = 0; i < variants.size(); i++) {
                    if (variants.get(i).equals(selectedVariant)) {
                        oldSelectedPosition = i;
                        break;
                    }
                }
            }

            selectedVariant = variant; // Update the new selected variant

            // Notify changes to refresh UI. This is efficient as it only redraws affected items.
            if (oldSelectedPosition != -1) {
                notifyItemChanged(oldSelectedPosition); // Unselect old item
            }
            notifyItemChanged(position); // Select new item

            // Notify the listener (BottomSheet) about the click
            if (listener != null) {
                listener.onVariantClick(variant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return variants != null ? variants.size() : 0;
    }

    // ViewHolder class
    public static class VariantViewHolder extends RecyclerView.ViewHolder {
        ImageView variantThumbnail;
        TextView variantName;

        public VariantViewHolder(@NonNull View itemView) {
            super(itemView);
            variantThumbnail = itemView.findViewById(R.id.variantThumbnail);
            variantName = itemView.findViewById(R.id.variantName);
        }
    }
}