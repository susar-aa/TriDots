package com.example.tridots.models;

public class LaborerCategory {
    private int serviceCategoryId;
    private String serviceCategoryName;
    private String description;
    private String categoryIcon;

    public LaborerCategory(int serviceCategoryId, String serviceCategoryName, String description, String categoryIcon) {
        this.serviceCategoryId = serviceCategoryId;
        this.serviceCategoryName = serviceCategoryName;
        this.description = description;
        this.categoryIcon = categoryIcon;
    }

    public int getServiceCategoryId() {
        return serviceCategoryId;
    }

    public String getServiceCategoryName() {
        return serviceCategoryName;
    }

    public String getDescription() {
        return description;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }
}