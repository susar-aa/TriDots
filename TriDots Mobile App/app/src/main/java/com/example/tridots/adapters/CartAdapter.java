package com.example.tridots.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Temporary, should use custom modal for confirmation

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tridots.Marketplace.CartManager;
import com.example.tridots.R;
import com.example.tridots.models.CartItem; // Import CartItem
import com.example.tridots.models.Product; // Still need Product for its properties

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private static final String TAG = "CartAdapter"; // Tag for logging

    private Context context;
    private List<CartItem> cartItems; // Now a list of CartItem
    private OnCartUpdateListener listener;

    public interface OnCartUpdateListener {
        void onCartUpdated();
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartUpdateListener listener) {
        this.context = context;
        this.cartItems = cartItems; // Initialize with cartItems
        this.listener = listener;
        // Register this adapter as a listener to CartManager so it can be notified of changes
        // This assumes the listener passed (usually the Activity) will also register/unregister.
        // For CartAdapter itself to be a listener directly: CartManager.getInstance().addCartUpdateListener(this);
        // But the current setup passes the Activity's listener, which is typically desired.
        // Ensure the Activity (CartActivity) registers/unregisters its listener appropriately.
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();
        Product.Variant variant = cartItem.getVariant(); // Get the variant from the CartItem

        // Use CartItem's helper methods for display
        holder.productName.setText(cartItem.getDisplayName());
        holder.productPrice.setText(String.format(Locale.getDefault(), "LKR %.2f", cartItem.getUnitPrice()));
        holder.productQuantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.itemTotalPrice.setText(String.format(Locale.getDefault(), "LKR %.2f", cartItem.getTotalPrice()));

        // Load image using CartItem's helper method
        String imageUrl = cartItem.getDisplayImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image) // Add a placeholder image in res/drawable
                    .error(R.drawable.error_placeholdeers) // Add an error image in res/drawable
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.ad_placeholder); // Default image
        }

        // Determine available stock based on whether a variant exists
        int availableStock = (variant != null) ? variant.getVariantStockQuantity() : product.getStockQuantity();

        // Handle quantity change buttons
        holder.buttonIncreaseQuantity.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() + 1;
            if (newQuantity <= availableStock) { // Check against determined available stock
                CartManager.getInstance().updateProductQuantity(product, variant, newQuantity); // Pass variant to CartManager
            } else {
                Toast.makeText(context, "Max stock reached for " + cartItem.getDisplayName(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.buttonDecreaseQuantity.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() - 1;
            if (newQuantity > 0) {
                CartManager.getInstance().updateProductQuantity(product, variant, newQuantity); // Pass variant to CartManager
            } else {
                // If quantity becomes 0, show confirmation (Toast is a temporary solution per instructions)
                // Implement a custom modal UI for user confirmation here instead of Toast.
                Toast.makeText(context, "Removing " + cartItem.getDisplayName() + " from cart.", Toast.LENGTH_SHORT).show();
                CartManager.getInstance().removeProduct(product, variant); // Pass variant for removal
            }
        });

        holder.buttonRemoveItem.setOnClickListener(v -> {
            // Show confirmation dialog before removing (Toast is a temporary solution per instructions)
            // Implement a custom modal UI for user confirmation here instead of Toast.
            Toast.makeText(context, "Confirm removal of " + cartItem.getDisplayName(), Toast.LENGTH_SHORT).show();
            CartManager.getInstance().removeProduct(product, variant); // Pass variant for removal
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    /**
     * Call this method from the Activity's onResume or after cart changes
     * to pull the latest data from CartManager and notify the adapter.
     */
    public void updateCartItems() {
        this.cartItems.clear();
        this.cartItems.addAll(CartManager.getInstance().getCartItems()); // Get fresh data from CartManager
        notifyDataSetChanged(); // Notify RecyclerView that data has changed
        Log.d(TAG, "Cart items updated in adapter. New size: " + this.cartItems.size());
    }


    // ViewHolder class remains the same for UI elements
    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productQuantity;
        TextView itemTotalPrice;
        Button buttonDecreaseQuantity, buttonIncreaseQuantity, buttonRemoveItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            itemTotalPrice = itemView.findViewById(R.id.itemTotalPrice);
            buttonDecreaseQuantity = itemView.findViewById(R.id.buttonDecreaseQuantity);
            buttonIncreaseQuantity = itemView.findViewById(R.id.buttonIncreaseQuantity);
            buttonRemoveItem = itemView.findViewById(R.id.buttonRemoveItem);
        }
    }
}
