package com.web.service.addmix_store.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class SubCategoryDtoForCollection {
    private Long id;
    private String name;
    @JsonProperty("name_url")
    private String nameUrl;
}
