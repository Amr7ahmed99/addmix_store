package com.web.service.addmix_store.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantUpdateRequestDTO {
    private Long variantId;
    private Long colorId;
    private Long sizeId;
    private String sku;
    private Boolean isActive;
    private Double price;
    private Double discountPrice;
}
