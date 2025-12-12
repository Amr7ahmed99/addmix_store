package com.web.service.addmix_store.projections.dashboard;


public interface ProductListProjection {

    Long getId();

    String getNameEn();

    String getNameAr();

    Double getPrice();

    Double getDiscountPrice();

    String getImageUrl();

    Boolean getIsActive();

    Integer getQuantity();

    Long getCategoryId();

    Long getSubCategoryId();

    Long getBrandId();

    Long getCollectionId();

    String getCategoryNameEn();
    String getSubCategoryNameEn();
    String getCollectionNameEn();
}

