package com.web.service.addmix_store.dtos.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductUpdateRequestDTO {
    @NotBlank
    private String nameEn;
    @NotBlank
    private String nameAr;
    @NotBlank
    private String descriptionEn;
    @NotBlank
    private String descriptionAr;
    @Min(1)
    private Long categoryId;
    @Min(1)
    private Long subCategoryId;
    @Min(1)
    private Long brandId;
    // @Builder.Default
    // private Boolean isActive= true;
    // private Boolean isTopSeller;
}
