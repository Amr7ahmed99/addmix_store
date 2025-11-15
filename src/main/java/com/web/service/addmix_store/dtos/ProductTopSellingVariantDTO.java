package com.web.service.addmix_store.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTopSellingVariantDTO {
    private Long Id;
    @JsonProperty("product_id")
    private Long productId;
    private String sku;
    @JsonProperty("attribute_value")
    private String attributeValue;
    @JsonProperty("attribute_name")
    private String attributeName;
}

