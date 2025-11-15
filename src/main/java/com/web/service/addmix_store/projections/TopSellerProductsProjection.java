package com.web.service.addmix_store.projections;

public interface TopSellerProductsProjection {
    Long getProductId();
    String getProductName();
    String getProductDescription();
    String getCategoryName();
    String getSubCategoryName();
    String getCollectionName();
    String getBrandName();
    String getSku();
    String getPrimaryImageUrl();
    Double getOriginalPrice();
    Double getDiscountPrice();
    Long getTotalSoldQuantity();
}

