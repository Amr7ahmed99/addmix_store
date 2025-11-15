package com.web.service.addmix_store.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CollectionDTO {
    private Long id;
    private String name;
    @JsonProperty("name_url")
    private String nameUrl;
    @JsonProperty("image_url")
    private String imageUrl;
    
    @Builder.Default
    private List<CategoryDtoForCollection> categories= new ArrayList<>();
    
    @Builder.Default
    @JsonProperty("top_brands")
    private List<BrandDTOForCollection> topBrands = new ArrayList<>();
}

