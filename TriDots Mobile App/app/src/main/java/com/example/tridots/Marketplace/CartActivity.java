// Path: app/src/main/java/com/example/tridots/Marketplace/CartActivity.java

package com.example.tridots.Marketplace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tridots.R;
import com.example.tridots.adapters.CartAdapter;

import java.math.BigDecimal;
import java.util.Locale;

// Implement the correct interface from CartAdapter
public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartUpdateListener {

    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private TextView textViewCartTotal;
    private Button buttonCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Your Cart");
        }


        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        textViewCartTotal = findViewById(R.id.textViewCartTotal);
        buttonCheckout = findViewById(R.id.buttonCheckout);

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        // Ensure CartManager.getInstance().getCartItems() returns the actual list of CartItem objects
        cartAdapter = new CartAdapter(this, CartManager.getInstance().getCartItems(), this); // Pass 'this' as listener
        recyclerViewCart.setAdapter(cartAdapter);

        // Initial display update based on current cart state
        updateOverallCartSummary();

        buttonCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getCartItemCount() > 0) {
                // Modified: Now launches AddressSelectionActivity instead of directly CheckoutActivity
                Intent intent = new Intent(CartActivity.this, AddressSelectionActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(CartActivity.this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
            }
        });
// ...
    }

    /**
     * This method updates the UI elements *outside* the RecyclerView,
     * such as the total price and checkout button state.
     * It should NOT trigger the adapter to refresh its items again, as that's handled
     * by the adapter itself when items are added/removed/quantity changed.
     */
    private void updateOverallCartSummary() {
        // Only update the total and button state here.
        BigDecimal total = CartManager.getInstance().getCartTotal();
        textViewCartTotal.setText(String.format(Locale.getDefault(), "Total: LKR %.2f", total));

        // Enable/disable checkout button based on cart items
        if (CartManager.getInstance().getCartItemCount() > 0) {
            buttonCheckout.setEnabled(true);
            buttonCheckout.setAlpha(1.0f); // Make it fully opaque
        } else {
            buttonCheckout.setEnabled(false);
            buttonCheckout.setAlpha(0.5f); // Make it translucent to indicate disabled
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // When activity resumes, ensure adapter has latest data and update overall summary
        // IMPORTANT: CartManager.getInstance().getCartItems() returns a *copy* of the list.
        // The adapter should refresh its internal list and then notifyDataSetChanged.
        cartAdapter.updateCartItems(); // This will pull fresh data from CartManager and notify itself
        updateOverallCartSummary(); // Then update the total/button
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Implementation of CartAdapter.OnCartUpdateListener
    // This callback is triggered by the CartAdapter when its internal data changes.
    @Override
    public void onCartUpdated() {
        // When the adapter notifies the activity that the cart has been updated (e.g., item added/removed/quantity changed),
        // we only need to refresh the overall summary (total price, checkout button state).
        // The adapter has already handled its own item list refresh.
        updateOverallCartSummary();
    }
}