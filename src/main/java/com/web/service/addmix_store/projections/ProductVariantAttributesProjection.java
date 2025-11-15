package com.web.service.addmix_store.projections;

public interface ProductVariantAttributesProjection {
    Long getId();
    Long getProductId();
    String getSku();
    String getAttributeValue();
    String getAttributeName();
}
