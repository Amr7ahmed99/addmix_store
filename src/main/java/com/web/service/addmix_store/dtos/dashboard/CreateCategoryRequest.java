package com.web.service.addmix_store.dtos.dashboard;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCategoryRequest {
    @NotBlank
    private String nameEn;

    @NotBlank
    private String nameAr;

    @NotNull
    private MultipartFile imageFile;

    @NotNull
    private Long collectionId;
}
