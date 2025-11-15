package com.web.service.addmix_store.dtos.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.web.service.addmix_store.dtos.ColorDTO;
import com.web.service.addmix_store.projections.ProductColorsProjection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrendingProductsResponseDTO {
    private Long id;
    private String nameEn;
    private String nameAr;
    private String descriptionEn;
    private String descriptionAr;
    private String categoryNameEn;
    private String categoryNameAr;
    private String subCategoryNameEn;
    private String subCategoryNameAr;
    private String collectionNameEn;
    private String collectionNameAr;
    private String brandNameEn;
    private String brandNameAr;
    private String brandImageUrl;
    private String imageUrl;
    private Double price;
    private Double discountPrice;
    private List<ColorDTO> colors;
    private Double savings;

    public static List<ColorDTO> mapColorsToProduct(Long productId, 
                                                   List<ProductColorsProjection> proColors) {
        return proColors.stream()
                .filter(color -> productId.equals(color.getProductId()))
                .map(color -> ColorDTO.builder()
                        .hexCode(color.getHexCode())
                        .nameEn(color.getNameEn())
                        .nameAr(color.getNameAr())
                        .build())
                .collect(Collectors.toList());
    }
}