package com.web.service.addmix_store.dtos.dashboard;

import java.util.List;
import java.util.stream.Collectors;

import com.web.service.addmix_store.models.ProductImage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDTO {
    private Long id;
    private String imageUrl;
    private Boolean isPrimary;
    private Long productId;

    public static List<ProductImageDTO> mapToDTO(List<ProductImage> images) {
        return images.stream().map((im) -> ProductImageDTO.builder()
                .id(im.getId())
                .imageUrl(im.getImageUrl())
                .isPrimary(im.getIsPrimary())
                .productId(im.getProduct().getId())
                .build())
                .collect(Collectors.toList());
    }
}
