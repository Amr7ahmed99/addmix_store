package com.web.service.addmix_store.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsListFilterRequestDTO {
    private Integer collectionId;
    private List<Long> categoryIds;
    private List<Long> subCategoryIds;
    private List<Long> brandIds;
    private List<Long> colorIds;
    private List<Long> sizeIds;
    private Double minPrice;
    private Double maxPrice;
    private String search;
    private Boolean isNew;
    private Boolean isTrend;
    private Boolean isTopSeller;
    private String sortBy; // "price_asc", "price_desc", "newest", "popular"
    
    @Builder.Default
    private int page = 0;
    
    @Builder.Default
    private int limit = 20;
}