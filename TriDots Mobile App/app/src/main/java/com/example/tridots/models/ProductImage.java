package com.example.tridots.models;

import java.io.Serializable;

public class ProductImage implements Serializable {
    private int image_id;
    private int product_id;
    private String image_url;
    private boolean is_thumbnail;
    private int display_order;

    public ProductImage() {} // Default constructor

    // Getters and Setters
    public int getImage_id() { return image_id; }
    public void setImage_id(int image_id) { this.image_id = image_id; }
    public int getProduct_id() { return product_id; }
    public void setProduct_id(int product_id) { this.product_id = product_id; }
    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }
    public boolean isIs_thumbnail() { return is_thumbnail; }
    public void setIs_thumbnail(boolean is_thumbnail) { this.is_thumbnail = is_thumbnail; }
    public int getDisplay_order() { return display_order; }
    public void setDisplay_order(int display_order) { this.display_order = display_order; }
}