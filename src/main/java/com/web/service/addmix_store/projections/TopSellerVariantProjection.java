package com.web.service.addmix_store.projections;

import java.time.LocalDateTime;

public interface TopSellerVariantProjection {
    Long getProductId();
    Long getId(); // variant id
    String getSku();
    Boolean getIsActive();

    // color
    String getColorHexCode();
    String getColorNameEn();
    String getColorNameAr();

    // size
    String getSizeNameEn();
    String getSizeNameAr();
    String getSizeType();

    // prices
    Double getPrice();
    Double getDiscountPrice();
    LocalDateTime getPriceStartDate();
    LocalDateTime getPriceEndDate();

    // stock
    Integer getQuantity();
    Integer getReservedQuantity();
    Integer getAvailableQuantity();
    Integer getDamageQuantity();
    
    // image
    String getImageUrl();
}


