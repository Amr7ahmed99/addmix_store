package com.web.service.addmix_store.dtos.response;

import java.time.LocalDateTime;
import java.util.List;
import com.web.service.addmix_store.dtos.ProductImageDTO;
import com.web.service.addmix_store.dtos.dashboard.BrandDTO;
import com.web.service.addmix_store.dtos.dashboard.CategoryDTO;
import com.web.service.addmix_store.dtos.dashboard.CollectionDTO;
import com.web.service.addmix_store.dtos.dashboard.ProductVariantDetailsDTO;
import com.web.service.addmix_store.dtos.dashboard.SubCategoryDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductProfileResponseDTO {
// Basic Info
    private Long id;
    private String nameEn;
    private String nameAr;
    private String descriptionEn;
    private String descriptionAr;
    private Boolean isActive;
    private Boolean isTopSeller;
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
    private List<ReviewResponse> reviews;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class ReviewResponse {
        private String userName;
        private int rating;
        private String comment;
    }

}

