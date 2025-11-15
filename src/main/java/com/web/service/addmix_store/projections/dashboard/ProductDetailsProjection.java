package com.web.service.addmix_store.projections.dashboard;

import java.time.LocalDateTime;

public interface ProductDetailsProjection {
    // Product Basic Info
    Long getProductId();
    String getProductNameEn();
    String getProductNameAr();
    String getProductDescriptionEn();
    String getProductDescriptionAr();
    Double getProductWeight();
    String getProductTags();
    Boolean getProductIsActive();
    Boolean getProductIsTopSeller();
    Boolean getProductIsTrend();
    Boolean getProductIsNew();
    Boolean getProductIsDeleted();
    LocalDateTime getProductCreatedAt();
    LocalDateTime getProductUpdatedAt();
    
    // Collection Info
    Long getCollectionId();
    String getCollectionNameEn();
    String getCollectionNameAr();
    String getCollectionImageUrl();
    
    // Category Info
    Long getCategoryId();
    String getCategoryNameEn();
    String getCategoryNameAr();
    String getCategoryImageUrl();
    
    // SubCategory Info
    Long getSubCategoryId();
    String getSubCategoryNameEn();
    String getSubCategoryNameAr();
    
    // Brand Info
    Long getBrandId();
    String getBrandNameEn();
    String getBrandNameAr();
    String getBrandImageUrl();
    
    // Variant Info
    Long getVariantId();
    String getVariantSku();
    Boolean getVariantIsActive();
    
    // Color Info
    Long getColorId();
    String getColorNameEn();
    String getColorNameAr();
    String getColorHexCode();
    
    // Size Info
    Long getSizeId();
    String getSizeNameEn();
    String getSizeNameAr();
    String getSizeType();
    
    // Price Info
    Double getPrice();
    Double getDiscountPrice();
    LocalDateTime getPriceStartDate();
    LocalDateTime getPriceEndDate();
    
    // Inventory Info
    Integer getInventoryQuantity();
    Integer getInventoryReservedQuantity();
    Integer getInventoryAvailableQuantity();
    Integer getInventoryLowStockThreshold();
    
    // Image Info
    Long getImageId();
    String getImageUrl();
    Boolean getImageIsPrimary();
    Integer getImageDisplayOrder();
}
