// Path: app/src/main/java/com/example/tridots/models/Product.java

package com.example.tridots.models;

import java.io.Serializable;
import java.math.BigDecimal; // Ensure BigDecimal is imported
import java.util.List;
import java.util.Objects;

public class Product implements Serializable {

    // Fields standardized to camelCase for Java convention
    private int productId;
    private int sellerId;
    private int categoryId;
    private String productName;
    private String productDescription;
    private BigDecimal price;
    private int stockQuantity;
    private String sku;
    private String productStatus;
    private String productCondition;
    private String postedDate;
    private String lastUpdatedDate;
    private String approvedStatus;
    private String availabilityStatus;
    private String keywords;
    private String brand;
    private String model;
    private String compatibility;
    private String warrantyPolicy;
    private int weightG;
    private String dimensionsCm;
    private String material;
    private String color;
    private String manufacturingDate;
    private String expirationDate;
    private String storeAddressLine1;
    private String storeAddressLine2;
    private String storeCity;
    private String storeDistrict;
    private String storePostalCode;
    private String storeCountry;
    private BigDecimal deliveryFee;
    private String thumbnailImageUrl; // Standardized for consistency

    private List<Image> images;
    private List<Variant> variants;

    public Product() {
        this.images = new java.util.ArrayList<>();
        this.variants = new java.util.ArrayList<>();
        this.price = BigDecimal.ZERO;
        this.deliveryFee = BigDecimal.ZERO;
        this.productName = null;
        this.productDescription = null;
        this.sku = null;
        this.productStatus = null;
        this.productCondition = null;
        this.postedDate = null;
        this.lastUpdatedDate = null;
        this.approvedStatus = null;
        this.availabilityStatus = null;
        this.keywords = null;
        this.brand = null;
        this.model = null;
        this.compatibility = null;
        this.warrantyPolicy = null;
        this.dimensionsCm = null;
        this.material = null;
        this.color = null;
        this.manufacturingDate = null;
        this.expirationDate = null;
        this.storeAddressLine1 = null;
        this.storeAddressLine2 = null;
        this.storeCity = null;
        this.storeDistrict = null;
        this.storePostalCode = null;
        this.storeCountry = null;
        this.thumbnailImageUrl = null;
    }

    // Full Constructor (matches all camelCase fields)
    public Product(int productId, int sellerId, int categoryId, String productName, String productDescription, BigDecimal price, int stockQuantity,
                   String sku, String productStatus, String productCondition, String postedDate, String lastUpdatedDate,
                   String approvedStatus, String availabilityStatus, String keywords, String brand, String model,
                   String compatibility, String warrantyPolicy, int weightG, String dimensionsCm, String material, String color,
                   String manufacturingDate, String expirationDate, String storeAddressLine1, String storeAddressLine2,
                   String storeCity, String storeDistrict, String storePostalCode, String storeCountry, BigDecimal deliveryFee,
                   String thumbnailImageUrl, List<Image> images, List<Variant> variants) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.sku = sku;
        this.productStatus = productStatus;
        this.productCondition = productCondition;
        this.postedDate = postedDate;
        this.lastUpdatedDate = lastUpdatedDate;
        this.approvedStatus = approvedStatus;
        this.availabilityStatus = availabilityStatus;
        this.keywords = keywords;
        this.brand = brand;
        this.model = model;
        this.compatibility = compatibility;
        this.warrantyPolicy = warrantyPolicy;
        this.weightG = weightG;
        this.dimensionsCm = dimensionsCm;
        this.material = material;
        this.color = color;
        this.manufacturingDate = manufacturingDate;
        this.expirationDate = expirationDate;
        this.storeAddressLine1 = storeAddressLine1;
        this.storeAddressLine2 = storeAddressLine2;
        this.storeCity = storeCity;
        this.storeDistrict = storeDistrict;
        this.storePostalCode = storePostalCode;
        this.storeCountry = storeCountry;
        this.deliveryFee = deliveryFee;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.images = images;
        this.variants = variants;
    }


    // --- Getters (Using standard Java camelCase for all) ---
    public int getProductId() {
        return productId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public String getSku() {
        return sku;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public String getProductCondition() {
        return productCondition;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public String getApprovedStatus() {
        return approvedStatus;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getCompatibility() {
        return compatibility;
    }

    public String getWarrantyPolicy() {
        return warrantyPolicy;
    }

    public int getWeightG() {
        return weightG;
    }

    public String getDimensionsCm() {
        return dimensionsCm;
    }

    public String getMaterial() {
        return material;
    }

    public String getColor() {
        return color;
    }

    public String getManufacturingDate() {
        return manufacturingDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getStoreAddressLine1() {
        return storeAddressLine1;
    }

    public String getStoreAddressLine2() {
        return storeAddressLine2;
    }

    public String getStoreCity() {
        return storeCity;
    }

    public String getStoreDistrict() {
        return storeDistrict;
    }

    public String getStorePostalCode() {
        return storePostalCode;
    }

    public String getStoreCountry() {
        return storeCountry;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public List<Image> getImages() {
        return images;
    }

    public List<Variant> getVariants() {
        return variants;
    }


    // --- Setters (Using standard Java camelCase for all) ---
    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public void setProductCondition(String productCondition) {
        this.productCondition = productCondition;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public void setApprovedStatus(String approvedStatus) {
        this.approvedStatus = approvedStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setCompatibility(String compatibility) {
        this.compatibility = compatibility;
    }

    public void setWarrantyPolicy(String warrantyPolicy) {
        this.warrantyPolicy = warrantyPolicy;
    }

    public void setWeightG(int weightG) {
        this.weightG = weightG;
    }

    public void setDimensionsCm(String dimensionsCm) {
        this.dimensionsCm = dimensionsCm;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setStoreAddressLine1(String storeAddressLine1) {
        this.storeAddressLine1 = storeAddressLine1;
    }

    public void setStoreAddressLine2(String storeAddressLine2) {
        this.storeAddressLine2 = storeAddressLine2;
    }

    public void setStoreCity(String storeCity) {
        this.storeCity = storeCity;
    }

    public void setStoreDistrict(String storeDistrict) {
        this.storeDistrict = storeDistrict;
    }

    public void setStorePostalCode(String storePostalCode) {
        this.storePostalCode = storePostalCode;
    }

    public void setStoreCountry(String storeCountry) {
        this.storeCountry = storeCountry;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productId == product.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }


    // --- Nested Image Class ---
    public static class Image implements Serializable {
        private int imageId;
        private int productId;
        private String imageUrl;
        private boolean isThumbnail;
        private int displayOrder;

        public Image() {
        } // No-argument constructor for parsing

        public Image(int imageId, int productId, String imageUrl, boolean isThumbnail, int displayOrder) {
            this.imageId = imageId;
            this.productId = productId;
            this.imageUrl = imageUrl;
            this.isThumbnail = isThumbnail;
            this.displayOrder = displayOrder;
        }

        public int getImageId() {
            return imageId;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public boolean isThumbnail() {
            return isThumbnail;
        } // Corrected: standard boolean getter prefix

        public int getDisplayOrder() {
            return displayOrder;
        }

        public void setImageId(int imageId) {
            this.imageId = imageId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public void setThumbnail(boolean thumbnail) {
            isThumbnail = thumbnail;
        } // Corrected setter name

        public void setDisplayOrder(int displayOrder) {
            this.displayOrder = displayOrder;
        }
    }

    // --- Nested Variant Class ---
    public static class Variant implements Serializable {
        private int variantId;
        private int productId;
        private String variantName;
        private BigDecimal variantPrice;
        private int variantStockQuantity;
        private String variantImageUrl;

        public Variant() {
        } // No-argument constructor for parsing

        public Variant(int variantId, int productId, String variantName, BigDecimal variantPrice, int variantStockQuantity, String variantImageUrl) {
            this.variantId = variantId;
            this.productId = productId;
            this.variantName = variantName;
            this.variantPrice = variantPrice;
            this.variantStockQuantity = variantStockQuantity;
            this.variantImageUrl = variantImageUrl;
        }

        public int getVariantId() {
            return variantId;
        }

        public String getVariantName() {
            return variantName;
        }

        public BigDecimal getVariantPrice() {
            return variantPrice;
        }

        public int getVariantStockQuantity() {
            return variantStockQuantity;
        }

        public String getVariantImageUrl() {
            return variantImageUrl;
        }

        public void setVariantId(int variantId) {
            this.variantId = variantId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public void setVariantName(String variantName) {
            this.variantName = variantName;
        }

        public void setVariantPrice(BigDecimal variantPrice) {
            this.variantPrice = variantPrice;
        }

        public void setVariantStockQuantity(int variantStockQuantity) {
            this.variantStockQuantity = variantStockQuantity;
        }

        public void setVariantImageUrl(String variantImageUrl) {
            this.variantImageUrl = variantImageUrl;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Variant variant = (Variant) o;
            return variantId == variant.variantId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(variantId);
        }
    }
}