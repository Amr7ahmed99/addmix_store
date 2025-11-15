package com.web.service.addmix_store.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.web.service.addmix_store.models.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{

        @Query(value = """
            SELECT 
                sum(oi.quantity) AS quantity, pro.id AS product_id, 
                
                pro.name_en AS product_name_en, pro.name_ar AS product_name_ar,
                pro.description AS product_description, cat.name_en AS category_name_en,
                cat.name_ar AS category_name_ar, sub_cat.name_en AS sub_category_name_en,
                sub_cat.name_ar AS sub_category_name_ar, coll.name_en AS collection_name_en,
                coll.name_ar AS collection_name_ar, bra.name_en AS brand_name_en,
                bra.name_ar AS brand_name_ar,
                
                pv.sku, av.value_en AS attribute_value_name_en,
                av.value_ar AS attribute_value_name_ar, ats.name_en AS attribute_name_en,
                ats.name_ar AS attribute_name_ar, ppr.price AS original_price,
                ppr.discount_price
            FROM order_items oi
            JOIN product_variants pv ON oi.product_variant_id= pv.id
            JOIN products pro ON pv.product_id= pro.id
            JOIN brands bra ON pro.brand_id= bra.id
            JOIN categories cat ON pro.category_id= cat.id
            JOIN collections coll ON cat.collection_id= coll.id
            JOIN sub_categories sub_cat ON pro.sub_category_id= sub_cat.id

            JOIN product_attribute_values pav ON pav.variant_id= pv.id
            LEFT JOIN attribute_values av ON pav.attribute_value_id = av.id
            LEFT JOIN attributes ats ON av.attribute_id = ats.id
            LEFT JOIN product_prices ppr ON ppr.variant_id= pv.id
            GROUP BY 
                quantity, pro.id,
                pro.name_en, pro.name_ar,
                pro.description, cat.name_en,
                cat.name_ar, sub_cat.name_en,
                sub_cat.name_ar, coll.name_en,
                coll.name_ar, bra.name_en,
                bra.name_ar, pv.sku,
                av.value_en, av.value_ar, ats.name_en,
                ats.name_ar, ppr.price, ppr.discount_price
            ORDER BY oi.quantity DESC
            """, nativeQuery = true)

    List<Object[]> findTopSellerProducts(Pageable pageable);

    
}
