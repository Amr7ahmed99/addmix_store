package com.web.service.addmix_store.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.web.service.addmix_store.dtos.dashboard.BrandDTO;
import com.web.service.addmix_store.dtos.dashboard.CategoryDTO;
import com.web.service.addmix_store.dtos.dashboard.CollectionDTO;
import com.web.service.addmix_store.dtos.dashboard.SubCategoryDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true )
public class ProductsListForWithFiltersDTO {
    public Long id;
    private String nameEn;
    private String nameAr;
    private String descriptionEn;
    private String descriptionAr;
    private Boolean isNew;
    private Boolean isTrend;
    private Boolean isTopSeller;
    private CollectionDTO collection;
    private CategoryDTO category;
    private SubCategoryDTO subCategory;
    private BrandDTO brand;
    private List<ProductsListVariantDTO> variants;
    private String primaryImageUrl;
}

