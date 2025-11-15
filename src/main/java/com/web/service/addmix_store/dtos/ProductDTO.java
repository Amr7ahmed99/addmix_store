package com.web.service.addmix_store.dtos;

import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
private Long id;
    @JsonProperty("name_en")
    private String nameEn;
    @JsonProperty("name_ar")
    private String nameAr;
    private String description;
    @JsonProperty("category_id")
    private Long categoryId;
    @JsonProperty("sub_category_id")
    private Long subCategoryId;
    @JsonProperty("brand_id")
    private Long brandId;
    private List<ProductImageDTO> images;
    private List<ProductVariantDTO> variants;
}
