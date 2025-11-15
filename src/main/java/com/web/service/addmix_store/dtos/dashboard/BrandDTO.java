package com.web.service.addmix_store.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandDTO {
    private Long id;
    private String nameEn;
    private String nameAr;
    private String imageUrl;
    // private Boolean isActive;
}