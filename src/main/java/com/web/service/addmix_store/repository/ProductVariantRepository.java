package com.web.service.addmix_store.repository;

import com.web.service.addmix_store.models.ProductVariant;
import com.web.service.addmix_store.projections.ProductVariantAttributesProjection;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    // ProductVariant findBySku(String sku);

    // Page<ProductVariant> findByIsDeletedFalse(Pageable pageable);

    // Page<ProductVariant> findByProductIdAndIsDeletedFalse(Long productId,
    // Pageable pageable);

    // @Query(name = """
    // SELECT * FROM product_variants WHERE product_id= :productId
    // """, nativeQuery = true)
    // List<ProductVariant> findByProductId(Integer productId);

    @Query(value = """
            SELECT
                pv.id,
                pv.product_id,
                pv.sku AS sku,
                av.value_en AS attribute_value,
                a.name_en AS attribute_name
            FROM product_variants pv
            JOIN product_attribute_values pav ON pav.variant_id = pv.id
            JOIN attribute_values av ON pav.attribute_value_id = av.id
            JOIN attributes a ON av.attribute_id = a.id
            WHERE pv.product_id IN(:productIds)
            ORDER BY pv.product_id DESC
            """, nativeQuery = true)
    List<ProductVariantAttributesProjection> findVariantsByProductIds(@Param("productIds") List<Long> productIds);

    @Query(value = """
                SELECT COUNT(id)
                FROM product_variants
                WHERE id IN (:variantIds) AND product_id= :productId
            """, nativeQuery = true)
    Long countProductVariants(@Param("variantIds") List<Long> variantIds, @Param("productId") Long productId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
                UPDATE
                    product_prices
                SET price = :price,
                    discount_price = :discountPrice,
                    start_date = :startDate,
                    end_date = :endDate
                WHERE variant_id IN (:variantIds)
            """, nativeQuery = true)
    int updateVariantPrices(
            @Param("price") Double price,
            @Param("discountPrice") Double discountPrice,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("variantIds") List<Long> variantIds);

    boolean existsBySku(String sku);
    
    boolean existsByProductIdAndColorIdAndSizeId(Long productId, Long colorId, Long sizeId);
}
