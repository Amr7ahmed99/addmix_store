package com.web.service.addmix_store.dtos.dashboard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSubCategoryRequest {
    @NotBlank
    private String nameEn;

    @NotBlank
    private String nameAr;

    @NotNull
    private Long categoryId;
}
