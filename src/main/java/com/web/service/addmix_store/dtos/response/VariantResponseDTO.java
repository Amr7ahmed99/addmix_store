package com.web.service.addmix_store.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantResponseDTO {
    private Long id;
    private Long productId;
    private Long colorId;
    private Long sizeId;
    private String sku;
    private Double price;
    private Integer quantity;
    private Boolean isActive;
    private String colorName;
    private String sizeName;
}
