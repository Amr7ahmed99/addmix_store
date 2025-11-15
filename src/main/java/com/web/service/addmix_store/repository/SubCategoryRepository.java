package com.web.service.addmix_store.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.service.addmix_store.dtos.dashboard.CategoryDTO;
import com.web.service.addmix_store.dtos.dashboard.SubCategoryDTO;
import com.web.service.addmix_store.models.SubCategory;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    @Query(name= "SELECT * FROM sub_categories WHERE id= :id AND is_deleted= false", nativeQuery = true)
    Optional<SubCategory> findByIdAndIsDeletedFalse(@Param("id") Long id);

    @Query("SELECT COUNT(subCat) FROM SubCategory subCat WHERE subCat.category.id = :categoryId")
    Long countSubCategoriesByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT sc FROM SubCategory sc WHERE sc.category.id = :categoryId AND (sc.nameEn = :nameEn OR sc.nameAr = :nameAr)")
    Optional<SubCategory> findByNameEnOrNameAr(@Param("nameEn") String nameEn, @Param("nameAr") String nameAr, @Param("categoryId") Long categoryId);


    // find all subCats with category names
    @Query(value = """
        SELECT 
            sc.id AS id,
            sc.name_ar AS nameAr,
            sc.name_en AS nameEn,
            c.id AS categoryId,
            c.name_en AS categoryNameEn,
            c.name_ar AS categoryNameAr
        FROM sub_categories sc
        LEFT JOIN categories c ON sc.category_id = c.id
        WHERE sc.is_deleted = false
    """, nativeQuery = true)
    List<SubCategoryDTO> findAllWithCategoryNames();

}
