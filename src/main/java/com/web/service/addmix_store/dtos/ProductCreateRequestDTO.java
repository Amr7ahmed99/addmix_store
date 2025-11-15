package com.web.service.addmix_store.dtos;

import jakarta.validation.constraints.*;
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

    @NotEmpty(message = "Images list cannot be empty")
    @Size(min = 1, message = "At least one image is required")
    private List<MultipartFile> images;

    @NotNull(message = "Primary image index is required")
    @Min(value = 0, message = "Primary image index must be 0 or greater")
    private Integer primaryImageIndex;

    @NotBlank(message = "English name is required")
    @Size(min = 2, max = 100, message = "English name must be between 2 and 100 characters")
    private String nameEn;

    @NotBlank(message = "Arabic name is required")
    @Size(min = 2, max = 100, message = "Arabic name must be between 2 and 100 characters")
    private String nameAr;

    @NotBlank(message = "English description is required")
    @Size(min = 5, max = 1000, message = "English description must be between 5 and 1000 characters")
    private String descriptionEn;

    @NotBlank(message = "Arabic description is required")
    @Size(min = 5, max = 1000, message = "Arabic description must be between 5 and 1000 characters")
    private String descriptionAr;

    @NotNull(message = "Brand ID is required")
    private Long brandId;

    @NotNull(message = "Collection ID is required")
    private Long collectionId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Subcategory ID is required")
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
