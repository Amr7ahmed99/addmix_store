package com.web.service.addmix_store.services;

import org.springframework.stereotype.Service;
import com.web.service.addmix_store.dtos.dashboard.BrandDTO;
import com.web.service.addmix_store.dtos.dashboard.CreateBrandRequest;
import com.web.service.addmix_store.dtos.dashboard.UpdateBrandRequest;
import com.web.service.addmix_store.models.Brand;
import com.web.service.addmix_store.repository.BrandRepository;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final S3Service s3Service;
    private ProductServiceImpl productService;

    public List<BrandDTO> getAll() {
        return brandRepository.findAll()
                .stream()
                .map(brand->{
                    BrandDTO dto = BrandDTO.builder()
                        .id(brand.getId())
                        .nameEn(brand.getNameEn())
                        .nameAr(brand.getNameAr())
                        .imageUrl(brand.getImageUrl())
                        // .isActive(brand.getIsActive())
                        .build();
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Optional<BrandDTO> getById(Long id) {
        return brandRepository.findById(id)
                .map(brand -> {
                    BrandDTO dto = BrandDTO.builder()
                        .id(brand.getId())
                        .nameEn(brand.getNameEn())
                        .nameAr(brand.getNameAr())
                        .imageUrl(brand.getImageUrl())
                        .build();
                    return dto;
                });
    }

    public void addBrand(CreateBrandRequest request) {
        // check if brand with same name exists
        Brand existsBrand = brandRepository
                .findByNameEnOrNameAr(request.getNameEn(), request.getNameAr()).orElse(null);
        if (existsBrand != null) {
            throw new IllegalArgumentException("لا يمكن اضافة ماركة بنفس الأسم.");
        }

        Brand brand = brandRepository.save(Brand.builder()
                .nameEn(request.getNameEn())
                .nameAr(request.getNameAr())
                .build());

        String imageUrl = s3Service.uploadBrandImage(brand.getId(), request.getImageFile(), "");

        brand.setImageUrl(imageUrl);

        brandRepository.save(brand);
    }

    public void updateBrand(Long id, UpdateBrandRequest request) {
       
        // fetch brand
        Brand brand = brandRepository.findById(id)
            .orElseThrow(()-> new IllegalArgumentException("ماركة غير موجودة"));

        // check if another brand with same name exists
        Brand existBrand= brandRepository
            .findByNameEnOrNameAr(request.getNameEn(), request.getNameAr()).orElse(null);
        if (existBrand != null && !existBrand.getId().equals(id)) {
            throw new IllegalArgumentException("لا يمكن تعديل أسم الماركة لأسم ماركة موجودة بالفعل");
        }

        // check if image file is updated
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            // delete old image from s3
            s3Service.deleteImage(brand.getImageUrl());
            // upload new image to s3
            String imageUrl = s3Service.uploadBrandImage(brand.getId(), request.getImageFile(), "");
            brand.setImageUrl(imageUrl);
        }

        brand.setNameEn(request.getNameEn());
        brand.setNameAr(request.getNameAr());

        brandRepository.save(brand);
    }

    public void deleteBrandById(Long id) {
        // fetch brand
        Brand brand = brandRepository.findById(id)
            .orElseThrow(()-> new IllegalArgumentException("ماركة غير موجودة"));

        // check if there are products associated with this category
        Long productCount = productService.countByCategoryId(id);
        if (productCount != null && productCount > 0) {
            throw new IllegalStateException("لا تستطيع حذف ماركة تحتوي على منتجات مرتبطة بها.");
        }

        brandRepository.deleteById(id);
        // delete category image from s3
        s3Service.deleteImage(brand.getImageUrl());
    }
}
