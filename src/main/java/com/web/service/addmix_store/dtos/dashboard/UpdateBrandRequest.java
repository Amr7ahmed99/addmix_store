package com.web.service.addmix_store.dtos.dashboard;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class UpdateBrandRequest {

    private String nameEn;

    private String nameAr;

    private MultipartFile imageFile;
}
