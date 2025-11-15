package com.web.service.addmix_store.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsListVariantsDTO {
    private List<ColorDTO> colors;
    private List<SizeDTO> sizes;
}