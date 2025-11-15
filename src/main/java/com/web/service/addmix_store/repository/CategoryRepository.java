package com.web.service.addmix_store.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.service.addmix_store.dtos.dashboard.CategoryDTO;
import com.web.service.addmix_store.models.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(name= "SELECT * FROM categories WHERE id= :id AND is_deleted= false", nativeQuery = true)
    Optional<Category> findByIdAndIsDeletedFalse(@Param("id") Long id);

    // find all cates with collection names
    @Query(value = """
        SELECT 
            cat.id,
            cat.name_en,
            cat.name_ar,
            cat.image_url,
            col.id AS collection_id,
            col.name_en AS collection_name_en,
            col.name_ar AS collection_name_ar
        FROM categories cat
        LEFT JOIN collections col ON cat.collection_id = col.id
        WHERE cat.is_deleted = false
        """, nativeQuery = true)
    List<CategoryDTO> findAllWithCollectionNames();

    @Query("SELECT c FROM Category c WHERE c.collection.id = :collectionId AND (c.nameEn = :nameEn OR c.nameAr = :nameAr)")
    Optional<Category> findByNameEnOrNameAr(@Param("nameEn") String nameEn, @Param("nameAr") String nameAr, @Param("collectionId") Long collectionId);
}