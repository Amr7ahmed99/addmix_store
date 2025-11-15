package com.web.service.addmix_store.services;

import org.springframework.stereotype.Service;
import com.web.service.addmix_store.dtos.dashboard.CreateSubCategoryRequest;
import com.web.service.addmix_store.dtos.dashboard.SubCategoryDTO;
import com.web.service.addmix_store.dtos.dashboard.UpdateCategoryRequest;
import com.web.service.addmix_store.dtos.dashboard.UpdateSubCategoryRequest;
import com.web.service.addmix_store.models.Category;
import com.web.service.addmix_store.models.SubCategory;
import com.web.service.addmix_store.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.modelmapper.ModelMapper;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryService categoryService;
    private final ProductServiceImpl productService;

    public List<SubCategoryDTO> getAll() {
        return subCategoryRepository.findAllWithCategoryNames()
                .stream()
                .map(subCat -> {
                    SubCategoryDTO dto = new SubCategoryDTO();
                    dto.setId(subCat.getId());
                    dto.setNameEn(subCat.getNameEn());
                    dto.setNameAr(subCat.getNameAr());
                    dto.setCategoryId(subCat.getCategoryId());
                    dto.setCategoryNameEn(subCat.getCategoryNameEn());
                    dto.setCategoryNameAr(subCat.getCategoryNameAr());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void deleteSubCategoryById(Long id) {
        // if there are any subCategories on this category, throw an exception
        subCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("الفئة الفرعية غير موجودة."));

        // check if there are products associated with this category
        Long productCount = productService.countBySubCategoryId(id);
        if (productCount != null && productCount > 0) {
            throw new IllegalArgumentException("لا يمكن حذف الفئة الفرعية لأنها مرتبطة بمنتجات حالية.");
        }

        subCategoryRepository.deleteById(id);
    }

    public void addSubCategory(CreateSubCategoryRequest request) {

        // check collection exists
        Category category = categoryService.findById(request.getCategoryId());

        // check if subCategory with same name exists in the same category
        SubCategory existSubCategory = subCategoryRepository
                .findByNameEnOrNameAr(request.getNameEn(), request.getNameAr(), request.getCategoryId()).orElse(null);
        if (existSubCategory != null) {
            throw new IllegalArgumentException("لا يمكن اضافة فئة فرعية بنفس الاسم في نفس الفئة.");
        }

        SubCategory subCategory = subCategoryRepository.save(SubCategory.builder()
                .nameEn(request.getNameEn())
                .nameAr(request.getNameAr())
                .category(category)
                .build());

        subCategoryRepository.save(subCategory);
    }

    public void updateSubCategory(Long id, UpdateSubCategoryRequest request) {

        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("الفئة الفرعية غير موجودة."));

        // check if another subCategory with same name exists in the same category
        SubCategory existSubCategory = subCategoryRepository
                .findByNameEnOrNameAr(request.getNameEn(), request.getNameAr(), request.getCategoryId()).orElse(null);
        if (existSubCategory != null && !existSubCategory.getId().equals(id)
                && existSubCategory.getCategory().getId().equals(request.getCategoryId())) {
            throw new IllegalArgumentException("لا يمكن تحديث الفئة الفرعية باسم موجود مسبقاً في نفس الفئة.");
        }

        subCategory.setNameEn(request.getNameEn());
        subCategory.setNameAr(request.getNameAr());
        subCategory.setCategory(categoryService.findById(request.getCategoryId()));

        subCategoryRepository.save(subCategory);
    }

}
