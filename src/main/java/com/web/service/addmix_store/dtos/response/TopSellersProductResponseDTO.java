package com.web.service.addmix_store.dtos.response;

import java.util.List;
import com.web.service.addmix_store.dtos.dashboard.ProductVariantDetailsDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopSellersProductResponseDTO {
    private Long id;
    private String nameEn;
    private String nameAr;
    private String brandName;
    private String descriptionEn;
    private String descriptionAr;
    private String imageUrl;

    private Double price;
    private Double discountPrice;

    private String collectionNameUrl;
    private String categoryNameUrl;
    private String subCategoryNameUrl;

    private Boolean isNew;
    private Boolean isTrend;
    private Double savings;

    private List<ProductVariantDetailsDTO> variants;
}
