package com.web.service.addmix_store.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SubCategoryDTO {
    private Long id;
    private String nameAr;
    private String nameEn;
    private Long categoryId;
    private String categoryNameEn;
    private String categoryNameAr;
}

