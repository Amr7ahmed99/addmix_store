package com.web.service.addmix_store.dtos.dashboard;

import lombok.Data;
import java.util.List;
import com.web.service.addmix_store.models.ProductImage;
import com.web.service.addmix_store.models.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String nameEn;
    private String nameAr;
    private String descriptionEn;
    private String descriptionAr;
    private CategoryDTO category;
    private SubCategoryDTO subCategory;
    private BrandDTO brand;
    private Boolean isActive;
    private Boolean isTopSeller;
    private Boolean isDeleted;
    private List<ProductImage> images;
    private List<ProductVariant> variants;
}
