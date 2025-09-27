package com.example.tridots.models;

public class VehicleCategory {

    private int vehicleCategoryId;
    private String vehicleCategoryName;
    private String description;
    private String categoryIcon;

    public VehicleCategory(int vehicleCategoryId, String vehicleCategoryName, String description, String categoryIcon) {
        this.vehicleCategoryId = vehicleCategoryId;
        this.vehicleCategoryName = vehicleCategoryName;
        this.description = description;
        this.categoryIcon = categoryIcon;
    }


    public int getVehicleCategoryId() {
        return vehicleCategoryId;
    }

    public String getVehicleCategoryName() {
        return vehicleCategoryName;
    }

    public String getDescription() {
        return description;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }
}