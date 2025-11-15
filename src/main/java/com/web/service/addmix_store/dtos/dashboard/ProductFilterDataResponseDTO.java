package com.web.service.addmix_store.dtos.dashboard;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterDataResponseDTO {
    private List<CollectionDTO> collections;
    private List<CategoryDTO> categories;
    private List<SubCategoryDTO> subCategories;
    private List<BrandDTO> brands;
}