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
public class AttributeValueDTO {
    private Long id;
    @JsonProperty("value_en")
    private String valueEn;
    @JsonProperty("value_ar")
    private String valueAr;
    @JsonProperty("attribute_name")
    private String attributeName; // ex: "Color", "Size"
}
