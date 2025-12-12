package com.web.service.addmix_store.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequestDTO {

    private List<MultipartFile> images;

    private Integer primaryImageIndex;

    private String nameEn;

    private String nameAr;

    private String descriptionEn;

    private String descriptionAr;

    private Long brandId;

    private Long collectionId;

    private Long categoryId;

    private Long subCategoryId;

    // validate that primaryImageIndex < images.size()
    public boolean isValidPrimaryIndex() {
        return images != null && primaryImageIndex != null && primaryImageIndex < images.size();
    }

    // @Data
    // @Builder
    // private static class ImagesData{
    //     @NotBlank(message = "Image source (src) is required")
    //     private String src;

    //     @NotBlank(message = "Image name is required")
    //     @Size(max = 255, message = "Image name must not exceed 255 characters")
    //     private String name;
    // }
}
