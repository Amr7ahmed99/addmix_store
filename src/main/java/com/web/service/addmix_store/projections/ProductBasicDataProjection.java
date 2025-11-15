package com.web.service.addmix_store.projections;

public interface ProductBasicDataProjection {

    Long getId();

    String getProductNameEn();

    String getProductNameAr();

    String getProductDescriptionEn();

    String getProductDescriptionAr();

    Long getCategoryId();

    String getCategoryNameEn();

    String getCategoryNameAr();

    Long getSubCategoryId();

    String getSubCategoryNameEn();

    String getSubCategoryNameAr();

    Long getBrandId();

    String getBrandNameEn();

    String getBrandNameAr();

    String getBrandImageUrl();

    Boolean getIsNew();

    Boolean getIsTrend();
    
    Boolean getIsTopSeller();

    Long getCollectionId();

    String getCollectionNameEn();

    String getCollectionNameAr();

    String getPrimaryImage();
}
