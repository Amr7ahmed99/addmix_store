package com.web.service.addmix_store.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCollectionDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name_en")
    private String nameEn;

    @JsonProperty("name_ar")
    private String nameAr;

    @JsonProperty("description")
    private String description;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("brand_name")
    private String brandName;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("discount_price")
    private Double discountPrice;

    @JsonProperty("colors")
    private List<String> colors;

    @JsonProperty("sizes")
    private List<String> sizes;
}

