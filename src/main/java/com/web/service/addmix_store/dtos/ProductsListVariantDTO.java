package com.web.service.addmix_store.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true )
public class ProductsListVariantDTO {
    private Long id;
    private Long productId;
    private ColorDTO color;
    private SizeDTO size;
    private Double price;
    private Double discountPrice;
    private Integer availableQuantity;
    
    // Calculated fields
    public Double getSavings() {
        if (discountPrice != null && price != null && price > discountPrice) {
            return price - discountPrice;
        }
        return 0.0;
    }
}