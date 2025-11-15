package com.web.service.addmix_store.projections;

public interface ProductSliderProjection {
    Long getId();
    String getNameEn();
    String getNameAr();
    String getBrandName();
    String getImageUrl();
    Double getPrice();
    Double getDiscountPrice();
    String getHexCode();          
    String getColorNameEn();      
    String getColorNameAr();      
    Boolean getIsNew();
}
