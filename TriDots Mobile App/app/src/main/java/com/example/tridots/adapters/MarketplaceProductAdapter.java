// File: app/src/main/java/com/example/tridots/adapters/MarketplaceProductAdapter.java
package com.example.tridots.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Import Toast

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tridots.R;
import com.example.tridots.models.Product; // Import your Product model
import com.squareup.picasso.Picasso; // For image loading

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.util.Log;

public class MarketplaceProductAdapter extends RecyclerView.Adapter<MarketplaceProductAdapter.ProductViewHolder> {

    private static final String TAG = "MarketplaceProdAdapter";

    // Define a new interface for item click events
    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    private List<Product> productList;
    private OnItemClickListener itemClickListener; // New listener instance

    // Constructor now accepts the OnItemClickListener
    public MarketplaceProductAdapter(List<Product> initialProductList, OnItemClickListener listener) {
        this.productList = new ArrayList<>(initialProductList);
        this.itemClickListener = listener; // Assign the listener
    }

    /**
     * Updates the adapter's internal list with new data and notifies the RecyclerView.
     */
    public void updateProducts(List<Product> newProductList) {
        this.productList.clear();
        this.productList.addAll(newProductList);
        notifyDataSetChanged();
        Log.d(TAG, "Adapter updated with " + newProductList.size() + " products.");
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_marketplace_product, parent, false);
        Log.d(TAG, "onCreateViewHolder: Inflating item_marketplace_product.xml");
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.textViewProductName.setText(product.getProductName());
        holder.textViewProductPrice.setText(String.format(Locale.getDefault(), "Price: LKR %.2f", product.getPrice()));
        holder.textViewProductStatus.setText("Status: " + product.getProductStatus()); // Corrected getter
        holder.textViewApprovedStatus.setText("Approval: " + product.getApprovedStatus()); // Corrected getter

        String imageUrl = product.getThumbnailImageUrl(); // Corrected getter
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_placeholdeers)
                    .into(holder.imageViewProductThumbnail);
            Log.d(TAG, "Loading image for " + product.getProductName() + " from: " + imageUrl);
        } else {
            holder.imageViewProductThumbnail.setImageResource(R.drawable.ic_menu_gallery);
            Log.d(TAG, "No thumbnail image URL for " + product.getProductName());
        }

        holder.imageButtonOptions.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(), "Options for " + product.getProductName(), Toast.LENGTH_SHORT).show();
        });

        // Set the click listener for the entire item view
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(product); // Trigger the item click listener
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProductThumbnail;
        TextView textViewProductName;
        TextView textViewProductPrice;
        TextView textViewProductStatus;
        TextView textViewApprovedStatus;
        ImageButton imageButtonOptions;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProductThumbnail = itemView.findViewById(R.id.imageViewProductThumbnail);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            textViewProductStatus = itemView.findViewById(R.id.textViewProductStatus);
            textViewApprovedStatus = itemView.findViewById(R.id.textViewApprovedStatus);
            imageButtonOptions = itemView.findViewById(R.id.imageButtonOptions);
        }
    }
}
