package com.web.service.addmix_store.dtos.dashboard;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCategoryRequest {

    @NotNull
    private Long id;
    
    @NotNull
    private Long collectionId;

    private String nameEn;

    private String nameAr;

    private MultipartFile imageFile;
}
