package com.web.service.addmix_store.services;

import com.web.service.addmix_store.dtos.BrandDTOForCollection;
import com.web.service.addmix_store.dtos.CategoryDtoForCollection;
import com.web.service.addmix_store.dtos.CollectionDTO;
import com.web.service.addmix_store.dtos.SubCategoryDtoForCollection;
import com.web.service.addmix_store.dtos.dashboard.CreateCollectionRequest;
import com.web.service.addmix_store.dtos.dashboard.UpdateCollectionRequest;
import com.web.service.addmix_store.models.Collection;
import com.web.service.addmix_store.repository.CollectionRepository;
import com.web.service.addmix_store.repository.CollectionRepository.CollectionBrandProjection;
import com.web.service.addmix_store.repository.CollectionRepository.CollectionDataProjection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final S3Service s3Service;


    @Cacheable("allSystemCollectionsData") // Cash the response
    @Transactional(readOnly = true)
    public List<CollectionDTO> getCollectionsWithCategoriesAndBrands(String lang) {

        log.info("Running DB query for system collections...");

        List<CollectionDataProjection> flatData = collectionRepository.findAllWithCategoriesAndSubCategories(lang);
        List<CollectionBrandProjection> brandData = collectionRepository.findTop10BrandsPerCollection(lang);

        // 1. Build collections with categories/subcategories
        Map<Long, CollectionDTO> collectionMap = new LinkedHashMap<>();
        for (CollectionDataProjection row : flatData) {
            CollectionDTO collection = collectionMap.computeIfAbsent(
                row.getCollectionId(),
                id -> new CollectionDTO(id, row.getCollectionName(), row.getCollectionNameUrl(), row.getCollectionImageUrl(), new ArrayList<>(), new ArrayList<>())
            );

            CategoryDtoForCollection category = collection.getCategories().stream()
                .filter(cat -> cat.getId().equals(row.getCategoryId()))
                .findFirst()
                .orElseGet(() -> {
                    if (row.getCategoryId() == null) {
                        return null; // skip creating category
                    }
                    CategoryDtoForCollection newCategory = new CategoryDtoForCollection(
                        row.getCategoryId(),
                        row.getCategoryName(),
                        row.getCategoryNameUrl(),
                        row.getCategoryImageUrl(),
                        new ArrayList<>()
                    );
                    collection.getCategories().add(newCategory);
                    return newCategory;
                });

            if (category != null && row.getSubCategoryId() != null) {
                category.getSubCategories().add(
                    new SubCategoryDtoForCollection(
                        row.getSubCategoryId(),
                        row.getSubCategoryName(),
                        row.getSubCategoryNameUrl()
                    )
                );
            }
        }

        // 2. Merge top brands into the same collections
        Map<String, List<BrandDTOForCollection>> brandsGrouped = brandData.stream()
            .collect(Collectors.groupingBy(
                CollectionBrandProjection::getCollectionName,
                Collectors.mapping(b -> new BrandDTOForCollection(b.getBrandId(), b.getBrandName(), b.getBrandNameUrl(), b.getBrandImageUrl()), Collectors.toList())
            ));

        collectionMap.values().forEach(c -> {
            List<BrandDTOForCollection> brands = brandsGrouped.getOrDefault(c.getName(), List.of());
            c.setTopBrands(brands);
        });

        return new ArrayList<>(collectionMap.values());
    }


    public List<com.web.service.addmix_store.dtos.dashboard.CollectionDTO> getCollections() {
        List<Collection> collections= collectionRepository.fetchAllCollections();
        return collections.stream().map(c ->
            com.web.service.addmix_store.dtos.dashboard.CollectionDTO.builder()
                .id(c.getId())
                .nameAr(c.getNameAr())
                .nameEn(c.getNameEn())
                .imageUrl(c.getImageUrl())
                .build()
        ).collect(Collectors.toList());
    }

    public Collection getCollectionById(Long id) {
        return collectionRepository.findById(id)
            .orElseThrow(()-> new IllegalArgumentException("المجموعة غير موجودة."));
    }

    @CacheEvict(value = "allSystemCollectionsData", allEntries = true)
    public ResponseEntity<?> deleteCollectionById(Long id){
        // if there are any categories on this collection, throw an exception
        try{
            Collection col= collectionRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("المجموعة غير موجودة."));
            Long categoryCount= collectionRepository.countCategoriesByCollectionId(id);
            if (categoryCount != null && categoryCount > 0) {
                // throw new IllegalStateException("Cannot delete collection with associated categories.");
                throw new IllegalStateException("لا تستطيع حذف مجموعة تحتوي على فئات مرتبطة بها.");
            }

            // delete collection image from s3
            collectionRepository.deleteById(id);

            s3Service.deleteImage(col.getImageUrl());
            return ResponseEntity.noContent().build();
        }catch(Exception e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @CacheEvict(value = "allSystemCollectionsData", allEntries = true)
    public Collection addCollection(CreateCollectionRequest request) {

        // check if collection with same name exists
        Collection existCollection= collectionRepository.findByNameEnOrNameAr(request.getNameEn(), request.getNameAr()).orElse(null);
        if (existCollection != null) {
            throw new IllegalArgumentException("لا يمكن اضافة مجموعة بنفس الاسم.");
        }

        // save collection image to s3 then save retured url to db
        Collection collection= collectionRepository.save(Collection.builder()
            .nameEn(request.getNameEn())
            .nameAr(request.getNameAr())
            .build());

        String imageUrl = s3Service.uploadCollectionImage(collection.getId(), request.getImageFile(), "");

        collection.setImageUrl(imageUrl);

        
        return collectionRepository.save(collection);
    }

    @CacheEvict(value = "allSystemCollectionsData", allEntries = true)
    public void updateCollection(Long collectionId, UpdateCollectionRequest request) {
        // fetch collection
        Collection collection= collectionRepository.findById(collectionId)
            .orElseThrow(()-> new IllegalArgumentException("المجموعة غير موجودة."));
        
        //check if another collection with same name exists
        Collection existCollection= collectionRepository.findByNameEnOrNameAr(request.getNameEn(), request.getNameAr()).orElse(null);
        if (existCollection != null && !existCollection.getId().equals(collectionId)) {
            throw new IllegalArgumentException("لا يمكن تحديث المجموعة باسم موجود مسبقاً.");
        }

        // check if image file is updated
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            // delete old image from s3
            s3Service.deleteImage(collection.getImageUrl());
            // upload new image to s3
            String imageUrl = s3Service.uploadCollectionImage(collection.getId(), request.getImageFile(), "");
            collection.setImageUrl(imageUrl);
        }


        collection.setNameEn(request.getNameEn());
        collection.setNameAr(request.getNameAr());

        collectionRepository.save(collection);

    }
}

