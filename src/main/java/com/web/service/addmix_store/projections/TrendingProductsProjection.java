package com.web.service.addmix_store.projections;

public interface TrendingProductsProjection {
    Long getId();
    String getNameEn();
    String getNameAr();
    String getDescriptionEn();
    String getDescriptionAr();
    String getSubCategoryNameEn();
    String getSubCategoryNameAr();
    String getCategoryNameEn();
    String getCategoryNameAr();
    String getCollectionNameEn();
    String getCollectionNameAr();
    String getBrandNameEn();
    String getBrandNameAr();
    String getBrandImageUrl();
    String getImageUrl();
    Double getPrice();
    Double getDiscountPrice();
    Double getSavings();
}


