package com.example.tridots.models;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProductVariant implements Serializable {
    private int variant_id;
    private int product_id;
    private String variant_name;
    private BigDecimal variant_price;
    private int variant_stock_quantity;
    private String variant_image_url;

    public ProductVariant() {
        // Default constructor
    }

    // Getters and Setters

    public int getVariant_id() {
        return variant_id;
    }

    public void setVariant_id(int variant_id) {
        this.variant_id = variant_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getVariant_name() {
        return variant_name;
    }

    public void setVariant_name(String variant_name) {
        this.variant_name = variant_name;
    }

    public BigDecimal getVariant_price() {
        return variant_price;
    }

    public void setVariant_price(BigDecimal variant_price) {
        this.variant_price = variant_price;
    }

    public int getVariant_stock_quantity() {
        return variant_stock_quantity;
    }

    public void setVariant_stock_quantity(int variant_stock_quantity) {
        this.variant_stock_quantity = variant_stock_quantity;
    }

    public String getVariant_image_url() {
        return variant_image_url;
    }

    public void setVariant_image_url(String variant_image_url) {
        this.variant_image_url = variant_image_url;
    }

    @Override
    public String toString() {
        return variant_name; // This is helpful for displaying in Spinners or logs
    }
}