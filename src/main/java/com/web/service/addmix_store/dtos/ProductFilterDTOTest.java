package com.web.service.addmix_store.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class ProductFilterDTOTest {
    private Long categoryId;
    private Long subCategoryId;
    private Long brandId;
    private Long collectionId;
    private Double minPrice;
    private Double maxPrice;
    private String searchTerm;
    private Boolean inStockOnly;
    private String sortBy;
}