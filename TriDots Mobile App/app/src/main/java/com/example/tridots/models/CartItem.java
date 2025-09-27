// Path: app/src/main/java/com/example/tridots/models/CartItem.java

package com.example.tridots.models;

import java.math.BigDecimal;
import java.io.Serializable;

public class CartItem implements Serializable {

    private Product product;
    private Product.Variant variant; // Can be null if it's a non-variant product
    private int quantity;

    public CartItem(Product product, Product.Variant variant, int quantity) {
        this.product = product;
        this.variant = variant;
        this.quantity = quantity;
    }

    // --- Getters ---
    public Product getProduct() {
        return product;
    }

    public Product.Variant getVariant() {
        return variant;
    }

    public int getQuantity() {
        return quantity;
    }

    // --- Setters ---
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // --- Helper Methods for Display and Calculation ---

    /**
     * Returns the display name of the cart item.
     * Prioritizes variant name if available, otherwise uses product name.
     */
    public String getDisplayName() {
        if (variant != null && variant.getVariantName() != null && !variant.getVariantName().isEmpty()) {
            return product.getProductName() + " (" + variant.getVariantName() + ")";
        }
        return product.getProductName();
    }

    /**
     * Returns the image URL to display for the cart item.
     * Prioritizes variant image, then product's first image, then null.
     */
    public String getDisplayImageUrl() {
        if (variant != null && variant.getVariantImageUrl() != null && !variant.getVariantImageUrl().isEmpty()) {
            return variant.getVariantImageUrl();
        }
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            return product.getImages().get(0).getImageUrl();
        }
        return null; // Or a default placeholder URL if you have one
    }

    /**
     * Returns the unit price of the item (variant price if available, otherwise product base price).
     */
    public BigDecimal getUnitPrice() {
        return (variant != null) ? variant.getVariantPrice() : product.getPrice();
    }

    /**
     * Calculates the total price for this specific cart line item (unit price * quantity).
     */
    public BigDecimal getTotalPrice() {
        return getUnitPrice().multiply(BigDecimal.valueOf(quantity));
    }
}