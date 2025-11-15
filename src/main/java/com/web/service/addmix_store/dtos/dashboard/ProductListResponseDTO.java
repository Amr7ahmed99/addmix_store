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
public class ProductListResponseDTO {
    private List<ProductsListDTO> products;
    private int currentPage;
    private int pageSize;
    private long totalCount;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}