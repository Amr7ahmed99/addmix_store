package com.web.service.addmix_store.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    private Long id;
    @JsonProperty("name_en")
    private String nameEn;
    @JsonProperty("name_ar")
    private String nameAr;
    @JsonProperty("image_url")
    private String imageUrl;
}
