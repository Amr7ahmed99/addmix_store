package com.web.service.addmix_store.projections;


public interface ProductsListVariantProjection {
    Long getId();
    Long getProductId();
    Double getPrice();
    Double getDiscountPrice();
    Integer getAvailableQuantity();
    Long getColorId();
    String getHexCode();
    Long getSizeId();
    String getSizeNameEn();
    String getSizeNameAr();
}