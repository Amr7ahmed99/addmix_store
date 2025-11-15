package com.web.service.addmix_store.repository;

import com.web.service.addmix_store.models.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

    @Query(value = """
        SELECT 
            c.id AS collection_id,
            c.image_url AS collection_image_url,
            CASE WHEN :lang = 'ar' THEN c.name_ar ELSE c.name_en END AS collection_name,
            c.name_en AS collection_name_url,
            
            cat.id AS categoryId,
            CASE WHEN :lang = 'ar' THEN cat.name_ar ELSE cat.name_en END AS category_name,
            cat.image_url AS category_image_url,
            cat.name_en AS category_name_url,

            
            s_cat.id AS subCategoryId,
            CASE WHEN :lang = 'ar' THEN s_cat.name_ar ELSE s_cat.name_en END AS sub_category_name,
            s_cat.name_en AS sub_category_name_url

        FROM collections c
        LEFT JOIN categories cat ON cat.collection_id = c.id
        LEFT JOIN sub_categories s_cat ON s_cat.category_id = cat.id
        """, nativeQuery = true)
    List<CollectionDataProjection> findAllWithCategoriesAndSubCategories(@Param("lang") String lang);



    @Query(value = """
        SELECT 
            collection_name,brand_name, 
            brand_image_url,brand_id, brand_name_url
        FROM (
            SELECT 
                CASE WHEN :lang = 'ar' THEN c.name_ar ELSE c.name_en END AS collection_name,
                b.id AS brand_id,
                b.name_en AS brand_name,
                b.image_url AS brand_image_url,
                b.name_en AS brand_name_url,
                
                ROW_NUMBER() OVER (PARTITION BY c.id ORDER BY COUNT(p.id) DESC) AS rn
            FROM collections c
            LEFT JOIN categories cat ON cat.collection_id = c.id
            LEFT JOIN products p ON p.category_id = cat.id
            JOIN brands b ON p.brand_id = b.id
            GROUP BY c.id, c.name_en, c.name_ar, b.id, b.name_en, b.name_ar, b.image_url
        ) ranked
        WHERE rn <= 10
        """, nativeQuery = true)
    List<CollectionBrandProjection> findTop10BrandsPerCollection(@Param("lang") String lang);

    @Query("SELECT c FROM Collection c")
    List<Collection> fetchAllCollections();

    @Query("SELECT COUNT(cat) FROM Category cat WHERE cat.collection.id = :collectionId")
    Long countCategoriesByCollectionId(@Param("collectionId") Long collectionId);

  
    @Query(value = "SELECT * FROM collections WHERE name_en = :nameEn OR name_ar = :nameAr LIMIT 1", nativeQuery = true)
    Optional<Collection> findByNameEnOrNameAr(@Param("nameEn") String nameEn, @Param("nameAr") String nameAr);

    // interfaces
    public interface CollectionBrandProjection {
        String getCollectionName();
        Integer getBrandId();
        String getBrandName();
        String getBrandNameUrl();
        String getBrandImageUrl();
    }

    

    public interface CollectionDataProjection {
        Long getCollectionId();
        String getCollectionName();
        String getCollectionNameUrl();
        String getCollectionImageUrl();

        Long getCategoryId();
        String getCategoryName();
        String getCategoryNameUrl();
        String getCategoryImageUrl();

        Long getSubCategoryId();
        String getSubCategoryName();
        String getSubCategoryNameUrl();
    }



    
}


