package com.web.service.addmix_store.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("is_primary")
    private Boolean isPrimary;
}
