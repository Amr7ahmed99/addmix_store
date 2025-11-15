package com.web.service.addmix_store.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantDTO {
    private Long id;
    private String sku;
    private List<AttributeValueDTO> attributes;
    @JsonProperty("current_price")
    private Double currentPrice;
    @JsonProperty("available_stock")
    private Integer availableStock;

}
