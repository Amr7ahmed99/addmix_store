package com.web.service.addmix_store.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsListDTO {
    private Long id;
    private String nameEn;
    private String nameAr;
    private Double price;
    private Double discountPrice;
    private String imageUrl;
    private Boolean isActive;
    private Integer quantity;
    private String categoryNameEn;
    private String subCategoryNameEn;
    private String collectionNameEn;

    public String getStockStatus() {
        if (quantity == null || quantity == 0) {
            return "Out of Stock";
        } else if (quantity <= 10) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }

    public String getStockBadge() {
        if (quantity == null || quantity == 0) {
            return "danger"; // red
        } else if (quantity <= 10) {
            return "warning"; // yellow
        } else {
            return "success"; // green
        }
    }
}