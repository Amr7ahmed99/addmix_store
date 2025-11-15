package com.web.service.addmix_store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data @NoArgsConstructor @AllArgsConstructor
public class CategoryDtoForCollection {
    private Long id;
    private String name;
    @JsonProperty("name_url")
    private String nameUrl;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("sub_categories")
    private List<SubCategoryDtoForCollection> subCategories;
}
