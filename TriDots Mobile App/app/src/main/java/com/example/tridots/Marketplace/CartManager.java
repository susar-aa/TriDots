// Path: app/src/main/java/com/example/tridots/Marketplace/CartManager.java
package com.example.tridots.Marketplace;

import com.example.tridots.adapters.CartAdapter;
import com.example.tridots.models.Product;
import com.example.tridots.models.CartItem; // Import the new CartItem model

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects; // For Object.equals and Objects.hash

/**
 * Singleton class to manage the shopping cart.
 */
public class CartManager {

    private static CartManager instance;
    private List<CartItem> cartItems; // Store CartItem objects
    private List<CartAdapter.OnCartUpdateListener> listeners; // Listeners for cart changes

    private CartManager() {
        cartItems = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    /**
     * Finds a CartItem in the cart based on Product and Variant.
     * @param product The product.
     * @param variant The variant (can be null for non-variant products).
     * @return The CartItem if found, otherwise null.
     */
    private CartItem findCartItem(Product product, Product.Variant variant) {
        for (CartItem item : cartItems) {
            // Check if the product IDs match
            if (item.getProduct().getProductId() == product.getProductId()) {
                // If both items have variants, compare them using Objects.equals for null safety
                if (Objects.equals(item.getVariant(), variant)) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Adds a product to the cart or increments its quantity if already present.
     * This method supports products with or without variants.
     * @param product The product to add.
     * @param variant The specific variant of the product (can be null for non-variant products).
     * @param quantityToAdd The quantity to add.
     */
    public void addProduct(Product product, Product.Variant variant, int quantityToAdd) {
        if (quantityToAdd <= 0) {
            return; // Nothing to add
        }

        // Basic stock check (optional, but recommended)
        if (product.getStockQuantity() > 0 && quantityToAdd > product.getStockQuantity()) {
            // You might want to show a toast or log an error here
            // Toast.makeText(context, "Cannot add more than available stock.", Toast.LENGTH_SHORT).show();
            // Or adjust quantityToAdd to product.getStockQuantity();
            quantityToAdd = product.getStockQuantity(); // Adjust to max available stock
            if (quantityToAdd == 0) return; // If stock is 0 after adjustment, don't add.
        }

        CartItem existingItem = findCartItem(product, variant);

        if (existingItem != null) {
            // If item already in cart, increment quantity
            int newQuantity = existingItem.getQuantity() + quantityToAdd;
            // Prevent adding more than available stock (if applicable)
            if (newQuantity > product.getStockQuantity()) {
                newQuantity = product.getStockQuantity();
                // Optionally inform the user they hit the stock limit
            }
            existingItem.setQuantity(newQuantity);
        } else {
            // If item not in cart, add as a new CartItem
            cartItems.add(new CartItem(product, variant, quantityToAdd));
        }
        notifyCartUpdated();
    }

    /**
     * Updates the quantity of a specific product (and variant) in the cart.
     * @param product The product whose quantity needs to be updated.
     * @param variant The specific variant of the product (can be null).
     * @param newQuantity The new quantity. If 0 or less, the item is removed.
     */
    public void updateProductQuantity(Product product, Product.Variant variant, int newQuantity) {
        Iterator<CartItem> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            // Check if this cart item matches the product and variant being updated
            if (item.getProduct().getProductId() == product.getProductId() &&
                    Objects.equals(item.getVariant(), variant)) { // Use Objects.equals for null-safe comparison of variants
                if (newQuantity > 0) {
                    // Prevent setting quantity more than available stock
                    if (newQuantity > product.getStockQuantity()) {
                        newQuantity = product.getStockQuantity();
                    }
                    item.setQuantity(newQuantity);
                } else {
                    iterator.remove(); // Remove if quantity is 0 or less
                }
                notifyCartUpdated();
                return; // Found and updated/removed
            }
        }
        // If the item wasn't found in the loop but newQuantity is positive,
        // it means we're trying to set a quantity for an item not yet in the cart.
        // This is effectively an "add" operation. However, updateProductQuantity is typically
        // used for items *already* in the cart. If you want to allow adding, uncomment below:
        /*
        if (newQuantity > 0) {
            cartItems.add(new CartItem(product, variant, newQuantity));
            notifyCartUpdated();
        }
        */
    }


    /**
     * Removes a product (and variant) completely from the cart.
     * @param product The product to remove.
     * @param variant The specific variant of the product (can be null).
     */
    public void removeProduct(Product product, Product.Variant variant) {
        Iterator<CartItem> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            // Check if this cart item matches the product and variant being removed
            if (item.getProduct().getProductId() == product.getProductId() &&
                    Objects.equals(item.getVariant(), variant)) {
                iterator.remove();
                notifyCartUpdated();
                return; // Found and removed
            }
        }
    }

    /**
     * Gets the current list of items in the cart.
     * @return List of CartItem objects.
     */
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems); // Return a copy to prevent external modification
    }

    /**
     * Gets the total number of unique items in the cart (not total quantity).
     * @return The count of unique product-variant combinations in the cart.
     */
    public int getCartItemCount() {
        return cartItems.size();
    }

    /**
     * Calculates the total sum of prices of all items in the cart.
     * @return BigDecimal representing the total cart value.
     */
    public BigDecimal getCartTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            total = total.add(item.getTotalPrice());
        }
        return total;
    }

    /**
     * Clears all items from the cart.
     */
    public void clearCart() {
        cartItems.clear();
        notifyCartUpdated();
    }

    /**
     * Registers a listener to be notified of cart updates.
     * @param listener The listener to register.
     */
    public void addCartUpdateListener(CartAdapter.OnCartUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Unregisters a listener from cart update notifications.
     * @param listener The listener to unregister.
     */
    public void removeCartUpdateListener(CartAdapter.OnCartUpdateListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all registered listeners that the cart has been updated.
     */
    private void notifyCartUpdated() {
        for (CartAdapter.OnCartUpdateListener listener : listeners) {
            listener.onCartUpdated();
        }
    }

    /**
     * Gets the quantity of a specific product (and variant) in the cart.
     * @param product The product.
     * @param variant The variant (can be null).
     * @return The quantity of the product in the cart, or 0 if not found.
     */
    public int getProductQuantityInCart(Product product, Product.Variant variant) {
        CartItem item = findCartItem(product, variant);
        return item != null ? item.getQuantity() : 0;
    }
}