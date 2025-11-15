package com.web.service.addmix_store.dtos.response;

import java.util.List;

import com.web.service.addmix_store.dtos.ProductsListForWithFiltersDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsListResponseDTO {
    private List<ProductsListForWithFiltersDTO> products;
    private int currentPage;
    private int pageSize;
    private long totalCount;
    private int totalPages;
}