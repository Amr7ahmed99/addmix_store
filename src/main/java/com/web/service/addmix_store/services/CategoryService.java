package com.web.service.addmix_store.services;

import org.springframework.stereotype.Service;
import com.web.service.addmix_store.dtos.dashboard.CategoryDTO;
import com.web.service.addmix_store.dtos.dashboard.CreateCategoryRequest;
import com.web.service.addmix_store.dtos.dashboard.UpdateCategoryRequest;
import com.web.service.addmix_store.models.Category;
import com.web.service.addmix_store.models.Collection;
import com.web.service.addmix_store.repository.CategoryRepository;
import com.web.service.addmix_store.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final S3Service s3Service;
    private final CollectionService collectionService;
    private final ProductServiceImpl productService;
    private final SubCategoryRepository subCategoryRepository;

    // public List<CategoryDTO> getAll() {
    //     return categoryRepository.findAllWithCollectionNames()
    //             .stream()
    //             .map(cat -> modelMapper.map(cat, CategoryDTO.class))
    //             .collect(Collectors.toList());
    // }

    public List<CategoryDTO> getAll() {
        return categoryRepository.findAllWithCollectionNames()
            .stream()
            .map(cat -> {
                CategoryDTO dto = new CategoryDTO();
                dto.setId(cat.getId());
                dto.setNameEn(cat.getNameEn());
                dto.setNameAr(cat.getNameAr());
                dto.setImageUrl(cat.getImageUrl());
                dto.setCollectionId(cat.getCollectionId());
                dto.setCollectionNameEn(cat.getCollectionNameEn());
                dto.setCollectionNameAr(cat.getCollectionNameAr()); 
                return dto;
            })
            .collect(Collectors.toList());
    }

    public Optional<CategoryDTO> getById(Long id) {
        return categoryRepository.findById(id)
                .map(cat -> {
                    CategoryDTO dto = new CategoryDTO();
                    dto.setId(cat.getId());
                    dto.setNameEn(cat.getNameEn());
                    dto.setNameAr(cat.getNameAr());
                    dto.setImageUrl(cat.getImageUrl());
                    dto.setCollectionId(cat.getCollection().getId());
                    dto.setCollectionNameEn(cat.getCollection().getNameEn());
                    dto.setCollectionNameAr(cat.getCollection().getNameAr()); 
                    return dto;
                });
    }
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("الفئة غير موجودة."));
    }

    public void deleteCategoryById(Long id) {
        // if there are any subCategories on this category, throw an exception
        Category cat = this.findById(id);

        Long subCategoryCount = subCategoryRepository.countSubCategoriesByCategoryId(id);
        if (subCategoryCount != null && subCategoryCount > 0) {
            // throw new IllegalStateException("Cannot delete category with associated
            // subCategories.");
            throw new IllegalStateException("لا تستطيع حذف فئة تحتوي على فئات فرعية مرتبطة بها.");
        }

        // check if there are products associated with this category
        Long productCount = productService.countByCategoryId(id);
        if (productCount != null && productCount > 0) {
            throw new IllegalStateException("لا تستطيع حذف فئة تحتوي على منتجات مرتبطة بها.");
        }

        // delete category image from s3
        categoryRepository.deleteById(id);

        s3Service.deleteImage(cat.getImageUrl());
    }

    public Category addCategory(CreateCategoryRequest request) {

        // check collection exists
        Collection collection = collectionService.getCollectionById(request.getCollectionId());

        // check if category with same name exists in the same collection
        Category existCategory = categoryRepository
                .findByNameEnOrNameAr(request.getNameEn(), request.getNameAr(), request.getCollectionId()).orElse(null);
        if (existCategory != null) {
            throw new IllegalArgumentException("لا يمكن اضافة فئة بنفس الاسم في نفس المجموعة.");
        }

        // save collection image to s3 then save retured url to db
        Category category = categoryRepository.save(Category.builder()
                .nameEn(request.getNameEn())
                .nameAr(request.getNameAr())
                .collection(collection)
                .build());

        String imageUrl = s3Service.uploadCategoryImage(category.getId(), request.getImageFile(), "");

        category.setImageUrl(imageUrl);

        Category savedCategory = categoryRepository.save(category);

        return savedCategory;
    }

    public void updateCategory(Long id, UpdateCategoryRequest request) {
        // fetch collection
        Collection collection = collectionService.getCollectionById(request.getCollectionId());

        // fetch category
        Category category = this.findById(id);

        // check if another category with same name exists in the same collection
        Category existCategory = categoryRepository
                .findByNameEnOrNameAr(request.getNameEn(), request.getNameAr(), request.getCollectionId())
                .orElse(null);
        if (existCategory != null && !existCategory.getId().equals(id)
                && existCategory.getCollection().getId().equals(request.getCollectionId())) {
            throw new IllegalArgumentException("لا يمكن تعديل الفئة الى اسم فئة موجودة في نفس المجموعة.");
        }

        // check if image file is updated
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            // delete old image from s3
            s3Service.deleteImage(category.getImageUrl());
            // upload new image to s3
            String imageUrl = s3Service.uploadCategoryImage(category.getId(), request.getImageFile(), "");
            category.setImageUrl(imageUrl);
        }

        category.setNameEn(request.getNameEn());
        category.setNameAr(request.getNameAr());
        category.setCollection(collection);

        categoryRepository.save(category);
    }

}
