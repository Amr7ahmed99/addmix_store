package com.web.service.addmix_store.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageUpdateRequestDTO {
    private Long imageId;
    private String imageUrl;
    private Boolean isPrimary;
    private Integer displayOrder;
}
