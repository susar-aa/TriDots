package com.example.tridots.models;

public class SubCategory {
    private int subCategoryId;
    private String subCategory;
    private String categoryDescription;
    private String categoryIcon;
    private int mainCategoryId;

    public SubCategory(int subCategoryId, String subCategory, String categoryDescription, String categoryIcon, int mainCategoryId) {
        this.subCategoryId = subCategoryId;
        this.subCategory = subCategory;
        this.categoryDescription = categoryDescription;
        this.categoryIcon = categoryIcon;
        this.mainCategoryId = mainCategoryId;
    }

    // Getter for subCategoryId
    public int getSubCategoryId() {
        return subCategoryId;
    }

    // Getters for other fields (if you don't have them already)
    public String getSubCategory() {
        return subCategory;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public int getMainCategoryId() {
        return mainCategoryId;
    }

    // Optional setters...
}