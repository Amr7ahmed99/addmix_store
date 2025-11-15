package com.web.service.addmix_store.dtos.dashboard;

import java.time.LocalDateTime;
import com.web.service.addmix_store.dtos.ColorDTO;
import com.web.service.addmix_store.dtos.SizeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDetailsDTO {
    private Long id;
    private ColorDTO color;
    private SizeDTO size;
    private String sku;
    private Boolean isActive;
    
    // prices
    private Double price;
    private Double discountPrice;
    private LocalDateTime priceStartDate;
    private LocalDateTime priceEndDate;
    
    // stock
    private Integer quantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private Integer damageQuantity;
    private String stockStatus;
}
