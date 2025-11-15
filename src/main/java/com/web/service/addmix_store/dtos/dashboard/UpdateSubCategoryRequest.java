package com.web.service.addmix_store.dtos.dashboard;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateSubCategoryRequest {
    
    @NotNull
    private Long categoryId;

    private String nameEn;

    private String nameAr;
}
