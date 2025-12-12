package com.web.service.addmix_store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.service.addmix_store.models.Color;
import com.web.service.addmix_store.projections.ProductColorsProjection;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {

        @Query(value = """
        SELECT 
            pv.product_id,
            co.hex_code,
            co.name_en,
            co.name_ar
        FROM product_variants pv
        JOIN colors co ON co.id= pv.color_id
        WHERE pv.is_active= true
            AND pv.product_id IN(:productIds)
            AND pv.color_id IS NOT NULL
        GROUP BY pv.product_id, co.hex_code, co.name_en, co.name_ar
        ORDER BY pv.product_id DESC
        """, nativeQuery = true)
    List<ProductColorsProjection> findColorsByProductIds(@Param("productIds") List<Long> productIds);
}
