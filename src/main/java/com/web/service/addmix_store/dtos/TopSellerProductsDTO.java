package com.web.service.addmix_store.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopSellerProductsDTO {

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_description")
    private String productDescription;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("sub_category_name")
    private String subCategoryName;

    @JsonProperty("collection_name")
    private String collectionName;

    @JsonProperty("brand_name")
    private String brandName;

    @JsonProperty("sku")
    private String sku;

    @JsonProperty("total_sold_quantity")
    private Long totalSoldQuantity;

    @JsonProperty("primary_image_url")
    private String primaryImageUrl;

    @JsonProperty("original_price")
    private Double originalPrice;

    @JsonProperty("discount_price")
    private Double discountPrice;

    @JsonProperty("variants")
    private List<ProductTopSellingVariantDTO> variants;
}

