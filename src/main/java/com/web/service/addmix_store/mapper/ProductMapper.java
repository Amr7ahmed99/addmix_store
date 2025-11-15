package com.web.service.addmix_store.mapper;

import com.web.service.addmix_store.dtos.ColorDTO;
import com.web.service.addmix_store.dtos.response.TopSellersProductResponseDTO;
import com.web.service.addmix_store.projections.ProductSliderProjection;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductMapper {

    /**
     * Map from Projection to DTO - Single Product
     */
    public TopSellersProductResponseDTO toSliderDTO(ProductSliderProjection projection) {
        return TopSellersProductResponseDTO.builder()
                .id(projection.getId())
                .nameEn(projection.getNameEn())
                .nameAr(projection.getNameAr())
                .brandName(projection.getBrandName())
                .imageUrl(projection.getImageUrl())
                .price(projection.getPrice())
                .discountPrice(projection.getDiscountPrice())
                .isNew(projection.getIsNew())
                .savings(calculateSavings(projection.getPrice(), projection.getDiscountPrice()))
                .build();
    }

    /**
     * Map list of projections to DTOs - Group by product and collect colors
     */
    public List<TopSellersProductResponseDTO> toSliderDTOs(List<ProductSliderProjection> projections) {
        // Group projections by product ID
        Map<Long, List<ProductSliderProjection>> groupedByProduct = projections.stream()
                .collect(Collectors.groupingBy(ProductSliderProjection::getId, LinkedHashMap::new, 
                         Collectors.toList()));

        // Convert grouped projections to DTOs
        List<TopSellersProductResponseDTO> result = new ArrayList<>();
        
        for (Map.Entry<Long, List<ProductSliderProjection>> entry : groupedByProduct.entrySet()) {
            List<ProductSliderProjection> productProjections = entry.getValue();
            
            if (!productProjections.isEmpty()) {
                ProductSliderProjection firstProjection = productProjections.get(0);
                
                // Extract all unique colors for this product
                List<ColorDTO> colors = productProjections.stream()
                        .filter(proj -> proj.getHexCode() != null && proj.getColorNameEn() != null)
                        .map(proj -> ColorDTO.builder()
                                .hexCode(proj.getHexCode())
                                .nameEn(proj.getColorNameEn())
                                .nameAr(proj.getColorNameAr() != null ? proj.getColorNameAr() : proj.getColorNameEn())
                                .build())
                        .distinct()
                        .collect(Collectors.toList());

                TopSellersProductResponseDTO dto = TopSellersProductResponseDTO.builder()
                        .id(firstProjection.getId())
                        .nameEn(firstProjection.getNameEn())
                        .nameAr(firstProjection.getNameAr())
                        .brandName(firstProjection.getBrandName())
                        .imageUrl(firstProjection.getImageUrl())
                        .price(firstProjection.getPrice())
                        .discountPrice(firstProjection.getDiscountPrice())
                        .isNew(firstProjection.getIsNew())
                        .savings(calculateSavings(firstProjection.getPrice(), firstProjection.getDiscountPrice()))
                        .colors(colors.isEmpty() ? getDefaultColors() : colors)
                        .build();

                result.add(dto);
            }
        }

        return result;
    }

    private Double calculateSavings(Double price, Double discountPrice) {
        if (discountPrice != null && price != null && price > discountPrice) {
            return price - discountPrice;
        }
        return 0.0;
    }

    private List<ColorDTO> getDefaultColors() {
        return List.of(
                ColorDTO.builder()
                        .hexCode("#000000")
                        .nameEn("Black")
                        .nameAr("أسود")
                        .build()
        );
    }
}
