package com.example.tridots.models;

public class Category {
    private int mainCategoryId;
    private String mainCategory;
    private String categoryDescription;
    private String categoryIcon;

    public Category(int mainCategoryId, String mainCategory, String categoryDescription, String categoryIcon) {
        this.mainCategoryId = mainCategoryId;
        this.mainCategory = mainCategory;
        this.categoryDescription = categoryDescription;
        this.categoryIcon = categoryIcon;
    }

    public int getMainCategoryId() {
        return mainCategoryId;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }
}