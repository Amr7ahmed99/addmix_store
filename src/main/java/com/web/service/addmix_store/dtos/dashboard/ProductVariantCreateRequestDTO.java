package com.web.service.addmix_store.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantCreateRequestDTO {
    private Long colorId;
    private Long sizeId;
    private String sku;
    private Double price;
    private Double discountPrice;
    private Integer initialQuantity;
}
