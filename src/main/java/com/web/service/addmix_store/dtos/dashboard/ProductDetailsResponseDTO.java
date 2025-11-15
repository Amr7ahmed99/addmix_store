package com.web.service.addmix_store.dtos.dashboard;

import java.time.LocalDateTime;
import java.util.List;
import com.web.service.addmix_store.dtos.ProductImageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsResponseDTO {
    // Basic Info
    private Long id;
    private String nameEn;
    private String nameAr;
    private String descriptionEn;
    private String descriptionAr;
    private Boolean isActive;
    private Boolean isTopSeller;
    private Boolean isTrend;
    private Boolean isNew;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relations Info
    private CollectionDTO collection;
    private CategoryDTO category;
    private SubCategoryDTO subCategory;
    private BrandDTO brand;
    
    //Variants
    private List<ProductVariantDetailsDTO> variants;
    
    //Images
    private List<ProductImageDTO> images;
}

