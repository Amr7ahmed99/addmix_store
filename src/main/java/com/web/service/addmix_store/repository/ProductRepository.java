package com.web.service.addmix_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.service.addmix_store.dtos.ColorDTO;
import com.web.service.addmix_store.dtos.ProductsListVariantDTO;
import com.web.service.addmix_store.dtos.SizeDTO;
import com.web.service.addmix_store.models.Product;
import com.web.service.addmix_store.models.ProductImage;
import com.web.service.addmix_store.models.ProductPrice;
import com.web.service.addmix_store.models.ProductVariant;
import com.web.service.addmix_store.projections.ProductBasicDataProjection;
import com.web.service.addmix_store.projections.ProductsListVariantProjection;
import com.web.service.addmix_store.projections.TopSellerProductsProjection;
import com.web.service.addmix_store.projections.TopSellerVariantProjection;
import com.web.service.addmix_store.projections.TrendingProductsProjection;
import com.web.service.addmix_store.projections.dashboard.BrandProjection;
import com.web.service.addmix_store.projections.dashboard.CategoryProjection;
import com.web.service.addmix_store.projections.dashboard.CollectionProjection;
import com.web.service.addmix_store.projections.dashboard.ProductDetailsProjection;
import com.web.service.addmix_store.projections.dashboard.ProductListProjection;
import com.web.service.addmix_store.projections.dashboard.SubCategoryProjection;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Category
    Page<Product> findByCategoryIdAndIsDeletedFalse(Long categoryId, Pageable pageable);

    // SubCategory
    Page<Product> findBySubCategoryIdAndIsDeletedFalse(Long subCategoryId, Pageable pageable);

    // Brand
    Page<Product> findByBrandIdAndIsDeletedFalse(Long brandId, Pageable pageable);

    // Search by English name
    Page<Product> findByNameEnContainingIgnoreCaseAndIsDeletedFalse(String nameEn, Pageable pageable);

    // Search by Arabic name
    Page<Product> findByNameArContainingIgnoreCaseAndIsDeletedFalse(String nameAr, Pageable pageable);

    // Get all active products
    Page<Product> findByIsDeletedFalse(Pageable pageable);

    @Query(value = """
            SELECT
                sum(oi.quantity) AS total_sold_quantity,
                pro.id AS product_id,
                CASE WHEN :lang = 'ar' THEN pro.name_ar ELSE pro.name_en END AS product_name,
                CASE WHEN :lang = 'ar' THEN pro.description_ar ELSE pro.description_en END AS product_description,
                CASE WHEN :lang = 'ar' THEN cat.name_ar ELSE cat.name_en END AS category_name,
                CASE WHEN :lang = 'ar' THEN sub_cat.name_ar ELSE sub_cat.name_en END AS sub_category_name,
                CASE WHEN :lang = 'ar' THEN coll.name_ar ELSE coll.name_en END AS collection_name,
                CASE WHEN :lang = 'ar' THEN bra.name_ar ELSE bra.name_en END AS brand_name,
                pv.sku AS sku,
                img.image_url AS primary_image_url,
                ppr.price AS original_price,
                ppr.discount_price
            FROM order_items oi
            JOIN product_variants pv ON oi.product_variant_id = pv.id
            JOIN products pro ON pv.product_id = pro.id
            JOIN brands bra ON pro.brand_id = bra.id
            JOIN categories cat ON pro.category_id = cat.id
            JOIN collections coll ON cat.collection_id = coll.id
            JOIN sub_categories sub_cat ON pro.sub_category_id = sub_cat.id
            JOIN product_prices ppr ON ppr.variant_id = pv.id
                AND (ppr.end_date IS NULL OR ppr.end_date > NOW())
            LEFT JOIN product_images img ON img.product_id = pro.id AND img.is_primary = true
            GROUP BY
                pro.id, pro.name_en, pro.name_ar, pro.description_en, pro.description_ar,
                cat.name_en, cat.name_ar,
                sub_cat.name_en, sub_cat.name_ar,
                coll.name_en, coll.name_ar,
                bra.name_en, bra.name_ar,
                pv.sku, img.image_url,
                ppr.price, ppr.discount_price
            ORDER BY product_id DESC
            """, nativeQuery = true)
    List<TopSellerProductsProjection> findTopSellerProducts(@Param("lang") String lang, Pageable pageable);

    // ============================== Dashboard Queries
    // ==============================
    @Query(value = """
            SELECT
                p.id AS product_id,
                p.name_en AS product_name_en,
                p.name_ar AS product_name_ar,
                p.description_en,
                p.description_ar,
                c.name_en AS category_name_en,
                c.name_ar AS category_name_ar,
                sc.name_en AS sub_category_name_en,
                sc.name_ar AS sub_category_name_ar,
                b.name_en AS brand_name_en,
                b.name_ar AS brand_name_ar,
                pi.image_url,
                pi.is_primary,
                v.id AS variant_id,
                v.sku,
                pr.price,
                pr.discount_price,
                pr.start_date,
                pr.end_date,
                a.name_en AS attribute_name_en,
                a.name_ar AS attribute_name_ar,
                av.value_en AS attribute_value_en,
                av.value_ar AS attribute_value_ar
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
            LEFT JOIN sub_categories sc ON p.sub_category_id = sc.id
            LEFT JOIN brands b ON p.brand_id = b.id
            LEFT JOIN product_images pi ON p.id = pi.product_id
            LEFT JOIN product_variants v ON p.id = v.product_id
            LEFT JOIN product_prices pr ON v.id = pr.variant_id
            LEFT JOIN product_attribute_values pav ON v.id = pav.variant_id
            LEFT JOIN attribute_values av ON pav.attribute_value_id = av.id
            LEFT JOIN attributes a ON av.attribute_id = a.id
            WHERE p.id = :product_id;
                """, nativeQuery = true)
    List<Map<String, Object>> findProductDetails(@Param("productId") Long productId);
    // ============================== Dashboard Queries
    // ==============================

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId AND p.isDeleted = false")
    Long countByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.brand.id = :brandId AND p.isDeleted = false")
    Long countByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.subCategory.id = :subCategoryId AND p.isDeleted = false")
    Long countBySubCategoryId(@Param("subCategoryId") Long subCategoryId);

    @Query(value = """
            SELECT
                p.id as id,
                p.name_en as nameEn,
                p.name_ar as nameAr,
                MIN(pp.price) as price,
                MIN(COALESCE(pp.discount_price, pp.price)) as discountPrice,
                COALESCE(pi.image_url, '') as imageUrl,
                p.is_active as isActive,
                COALESCE(
                    (
                        SELECT SUM(i2.quantity)
                        FROM product_variants pv2
                        LEFT JOIN inventory i2 ON pv2.id = i2.product_variant_id
                        WHERE pv2.product_id = p.id AND pv2.is_active = true
                    ), 0
                ) as quantity,
                p.category_id as categoryId,
                p.sub_category_id as subCategoryId,
                p.brand_id as brandId,
                cat.collection_id as collectionId,
                cat.name_en as categoryNameEn,
                sub_cat.name_en as subCategoryNameEn,
                col.name_en as collectionNameEn
            FROM products p
            LEFT JOIN product_images pi ON p.id = pi.product_id AND pi.is_primary = true
            LEFT JOIN product_variants pv ON p.id = pv.product_id AND pv.is_active = true
            LEFT JOIN product_prices pp ON pv.id = pp.variant_id
                AND (pp.end_date IS NULL OR pp.end_date > NOW())
            LEFT JOIN categories cat ON p.category_id = cat.id
            LEFT JOIN sub_categories sub_cat ON p.sub_category_id = sub_cat.id
            LEFT JOIN collections col ON cat.collection_id = col.id

            WHERE p.is_deleted = false
            --- removed to be displayed on admin dashboard  --- AND p.is_active= true
            AND (:categoryId = 0 OR p.category_id = :categoryId)
            AND (:subCategoryId = 0 OR p.sub_category_id = :subCategoryId)
            AND (:brandId = 0 OR p.brand_id = :brandId)
            AND (:collectionId = 0 OR cat.collection_id = :collectionId)
            AND (
                :search IS NULL
                OR :search = ''
                OR p.name_en ILIKE '%' || :search || '%'
                OR p.name_ar ILIKE '%' || :search || '%'
                OR p.description_en ILIKE '%' || :search || '%'
                OR p.description_ar ILIKE '%' || :search || '%'
            )
            GROUP BY p.id, p.name_en, p.name_ar, pi.image_url, p.is_active,
                    p.category_id, p.sub_category_id, p.brand_id, cat.collection_id,
                    cat.name_en, sub_cat.name_en, col.name_en
            ORDER BY p.created_at DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<ProductListProjection> findProductsListWithFilters(
            @Param("categoryId") int categoryId,
            @Param("subCategoryId") int subCategoryId,
            @Param("brandId") int brandId,
            @Param("collectionId") int collectionId,
            @Param("search") String search,
            @Param("limit") int limit,
            @Param("offset") int offset);

    @Query(value = """
            SELECT p.id, p.name_en AS product_name_en, p.name_ar AS product_name_ar,
                p.description_en AS product_description_en, p.description_ar AS product_description_ar,
                c.name_en AS category_name_en, c.name_ar AS category_name_ar,
                sc.name_en AS sub_category_name_en, sc.name_ar AS sub_category_name_ar,
                b.name_en AS brand_name_en, b.name_ar AS brand_name_ar
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
            LEFT JOIN sub_categories sc ON p.sub_category_id = sc.id
            LEFT JOIN brands b ON p.brand_id = b.id
            WHERE p.id = :productId
            """, nativeQuery = true)
    Object findProductBase(@Param("productId") Long productId);

    @Query(value = "SELECT * FROM product_images WHERE product_id = :productId", nativeQuery = true)
    List<ProductImage> findImagesByProductId(@Param("productId") Long productId);

    // Variants
    @Query(value = "SELECT * FROM product_variants WHERE product_id = :productId", nativeQuery = true)
    List<ProductVariant> findVariantsByProductId(@Param("productId") Long productId);

    // Prices
    @Query(value = "SELECT * FROM product_prices WHERE variant_id = :variantId", nativeQuery = true)
    List<ProductPrice> findPricesByVariantId(@Param("variantId") Long variantId);

    // Attributes per variant
    @Query(value = """
            SELECT a.name_en, a.name_ar, av.value_en, av.value_ar
            FROM product_attribute_values pav
            JOIN attribute_values av ON pav.attribute_value_id = av.id
            JOIN attributes a ON av.attribute_id = a.id
            WHERE pav.variant_id = :variantId
            """, nativeQuery = true)
    List<Object[]> findAttributesByVariantId(@Param("variantId") Long variantId);

    @Query(value = """
            SELECT
                u.first_name AS userName,
                r.rating AS rating,
                r.comment AS comment
            FROM reviews r
            JOIN users u ON r.user_id = u.id
            WHERE r.product_id = :productId
            ORDER BY r.created_at DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<Object[]> findReviewsByProductId(@Param("productId") Long productId, @Param("limit") int limit,
            @Param("offset") int offset);

    // Get top seller products
    @Query(value = """
                SELECT
                    p.id as id,
                    p.name_en as nameEn,
                    p.name_ar as nameAr,
                    p.description_en as descriptionEn,
                    p.description_ar as descriptionAr,
                    col.name_en as collectionNameUrl,
                    cat.name_en as categoryNameUrl,
                    sub_cat.name_en as subCategoryNameUrl,
                    b.name_en as brandName,
                    COALESCE(pi.image_url, '') as imageUrl,
                    MIN(pp.price) as price,
                    MIN(COALESCE(pp.discount_price, NULL)) as discountPrice,
                    p.is_new as isNew,
                    p.is_trend as isTrend
                FROM products p
                JOIN categories cat ON cat.id= p.category_id
                JOIN collections col ON cat.collection_id= col.id
                JOIN sub_categories sub_cat ON sub_cat.id= p.sub_category_id
                JOIN brands b ON p.brand_id = b.id
                JOIN product_images pi ON p.id = pi.product_id AND pi.is_primary = true
                JOIN product_variants pv ON p.id = pv.product_id AND pv.is_active = true
                JOIN product_prices pp ON pv.id = pp.variant_id
                    AND (pp.end_date IS NULL OR pp.end_date > NOW())
                WHERE p.is_top_seller = true
                AND p.is_deleted = false AND p.is_active= true
                GROUP BY p.id, p.name_en, p.name_ar, b.name_en, pi.image_url, p.created_at, col.name_en, cat.name_en, sub_cat.name_en
                ORDER BY p.created_at DESC
                LIMIT 10
            """, nativeQuery = true)
    List<ProductBasicProjection> findTopSellerProductsBasic();

    @Query(value = """
                SELECT
                    pv.product_id AS productId,
                    pv.id AS id,
                    pv.sku AS sku,
                    pv.is_active AS isActive,

                    c.hex_code AS colorHexCode,
                    c.name_en AS colorNameEn,
                    COALESCE(c.name_ar, c.name_en) AS colorNameAr,

                    s.name_en AS sizeNameEn,
                    COALESCE(s.name_ar, s.name_en) AS sizeNameAr,
                    s.size_type AS sizeType,

                    pp.price AS price,
                    pp.discount_price AS discountPrice,
                    pp.start_date AS priceStartDate,
                    pp.end_date AS priceEndDate,

                    i.quantity AS quantity,
                    i.reserved_quantity AS reservedQuantity,
                    i.available_quantity AS availableQuantity,
                    i.damaged_quantity AS damageQuantity,
                    img.image_url AS imageUrl
                FROM product_variants pv
                LEFT JOIN colors c ON pv.color_id = c.id
                LEFT JOIN sizes s ON pv.size_id = s.id
                JOIN product_prices pp ON pv.id = pp.variant_id
                    AND (pp.end_date IS NULL OR pp.end_date > NOW())
                LEFT JOIN inventory i ON pv.id = i.product_variant_id
                LEFT JOIN product_images img ON img.product_id = pv.product_id 
                    AND img.is_primary = true
                WHERE pv.product_id IN :productIds
                    AND pv.is_active = true
                ORDER BY pv.product_id, pv.id
            """, nativeQuery = true)
    List<TopSellerVariantProjection> findVariantsForTopSellers(@Param("productIds") List<Long> productIds);

    // Get trending products
    @Query(value = """
                SELECT
                    p.id as id,
                    p.name_en as nameEn,
                    p.name_ar as nameAr,
                    p.description_en as descriptionEn,
                    p.description_ar as descriptionAr,
                    b.name_en as brandNameEn,
                    b.name_ar as brandNameAr,
                    b.image_url as brandImageUrl,
                    c.name_en as categoryNameEn,
                    c.name_ar as categoryNameAr,
                    col.name_en as collectionNameEn,
                    col.name_ar as collectionNameAr,
                    sc.name_en as subCategoryNameEn,
                    sc.name_ar as subCategoryNameAr,
                    COALESCE(pi.image_url, '') as imageUrl,
                    MIN(pp.price) as price,
                    MIN(COALESCE(pp.discount_price, pp.price)) as discountPrice
                FROM products p
                JOIN categories c ON p.category_id = c.id
                JOIN collections col ON col.id = c.collection_id
                JOIN sub_categories sc ON p.sub_category_id = sc.id
                JOIN brands b ON p.brand_id = b.id
                JOIN product_images pi ON p.id = pi.product_id
                    AND pi.is_primary = true
                JOIN product_variants pv ON p.id = pv.product_id AND pv.is_active = true
                JOIN product_prices pp ON pv.id = pp.variant_id
                    AND (pp.end_date IS NULL OR pp.end_date > NOW())
                WHERE p.is_trend = true
                    AND p.is_active = true AND p.is_deleted = false
                GROUP BY p.id, p.name_en, p.name_ar, b.name_en, b.name_ar, b.image_url,
                    c.name_en, c.name_ar , col.name_en, col.name_ar, sc.name_en, sc.name_ar, pi.image_url, p.created_at
                ORDER BY p.created_at DESC
                LIMIT 5;
            """, nativeQuery = true)
    List<TrendingProductsProjection> getTrendingProducts();

    @Query(value = """
            SELECT
                pv.product_id as productId,
                c.hex_code as hexCode,
                c.name_en as nameEn,
                COALESCE(c.name_ar, c.name_en) as nameAr
            FROM product_variants pv
            LEFT JOIN colors c ON pv.color_id = c.id
            WHERE pv.product_id IN :productIds
                AND pv.is_active = true
            GROUP BY pv.product_id, c.hex_code, c.name_en, c.name_ar
            ORDER BY pv.product_id, c.name_en
            """, nativeQuery = true)
    List<ProductColorProjection> findColorsByProductIds(@Param("productIds") List<Long> productIds);

    @Query(value = """
            SELECT
                pv.product_id as productId,
                s.name_en as nameEn,
                COALESCE(s.name_ar, s.name_en) as nameAr,
                s.size_type as sizeType
            FROM product_variants pv
            LEFT JOIN sizes s ON pv.size_id = s.id
            WHERE pv.product_id IN :productIds
            AND pv.is_active = true
            AND s.is_active = true
            GROUP BY pv.product_id, s.name_en, s.name_ar, s.size_type
            ORDER BY pv.product_id
            """, nativeQuery = true)
    List<ProductSizeProjection> findSizesByProductIds(@Param("productIds") List<Long> productIds);

    public interface ProductBasicProjection {
        Long getId();

        String getNameEn();

        String getNameAr();

        String getDescriptionEn();

        String getCollectionNameUrl();

        String getCategoryNameUrl();

        String getSubCategoryNameUrl();

        String getDescriptionAr();

        String getBrandName();

        String getImageUrl();

        Double getPrice();

        Double getDiscountPrice();

        Boolean getIsNew();

        Boolean getIsTrend();

    }

    public interface ProductColorProjection {
        Long getProductId();

        String getHexCode();

        String getNameEn();

        String getNameAr();
    }

    public interface ProductSizeProjection {
        Long getProductId();

        String getNameEn();

        String getNameAr();

        String getSizeType();
    }

    @Query(value = """
            SELECT COUNT(DISTINCT p.id)
            FROM products p
            LEFT JOIN categories cat ON p.category_id = cat.id
            WHERE p.is_deleted = false AND p.is_active= true
            AND (:categoryId = 0 OR p.category_id = :categoryId)
            AND (:subCategoryId = 0 OR p.sub_category_id = :subCategoryId)
            AND (:brandId = 0 OR p.brand_id = :brandId)
            AND (:collectionId = 0 OR cat.collection_id = :collectionId)
            AND (
                :search IS NULL
                OR :search = ''
                OR p.name_en ILIKE '%' || :search || '%'
                OR p.name_ar ILIKE '%' || :search || '%'
                OR p.description_en ILIKE '%' || :search || '%'
                OR p.description_ar ILIKE '%' || :search || '%'
            )
            """, nativeQuery = true)
    long countProductsWithFilters(
            @Param("categoryId") int categoryId,
            @Param("subCategoryId") int subCategoryId,
            @Param("brandId") int brandId,
            @Param("collectionId") int collectionId,
            @Param("search") String search);

    @Query(value = """
            SELECT col.id, col.name_en, col.name_ar
            FROM collections col
            WHERE col.is_deleted = false
            """, nativeQuery = true)
    List<CollectionProjection> findAllActiveCollections();

    @Query(value = """
            SELECT cat.id, cat.name_en, cat.name_ar
            FROM categories cat
            WHERE cat.is_deleted = false
                AND (:collectionId = 0 OR cat.collection_id = :collectionId)
            """, nativeQuery = true)
    List<CategoryProjection> findAllActiveCategories(@Param("collectionId") Long collectionId);

    @Query(value = """
            SELECT sc.id, sc.name_en, sc.name_ar
            FROM sub_categories sc
            WHERE sc.is_deleted = false
                AND (:categoryId = 0 OR sc.category_id = :categoryId)
            """, nativeQuery = true)
    List<SubCategoryProjection> findAllActiveSubCategories(@Param("categoryId") Long categoryId);

    @Query(value = """
            SELECT b.id, b.name_en, b.name_ar
            FROM brands b
            WHERE b.is_active = true
            """, nativeQuery = true)
    List<BrandProjection> findAllActiveBrands();

    @Query(value = """
            SELECT
                p.id as product_id,
                p.name_en as product_name_en,
                p.name_ar as product_name_ar,
                p.description_en as product_description_en,
                p.description_ar as product_description_ar,
                p.is_active as product_is_active,
                p.is_top_seller as product_is_top_seller,
                p.is_trend as product_is_trend,
                p.is_new as product_is_new,
                p.is_deleted as product_is_deleted,
                p.created_at as product_created_at,
                p.updated_at as product_updated_at,

                -- Collection Data
                col.id as collection_id,
                col.name_en as collection_name_en,
                col.name_ar as collection_name_ar,
                col.image_url as collection_image_url,

                -- Category Data
                cat.id as category_id,
                cat.name_en as category_name_en,
                cat.name_ar as category_name_ar,
                cat.image_url as category_image_url,

                -- SubCategory Data
                sc.id as sub_category_id,
                sc.name_en as sub_category_name_en,
                sc.name_ar as sub_category_name_ar,

                -- Brand Data
                b.id as brand_id,
                b.name_en as brand_name_en,
                b.name_ar as brand_name_ar,
                b.image_url as brand_image_url,

                -- Variant Data
                pv.id as variant_id,
                pv.sku as variant_sku,
                pv.is_active as variant_is_active,

                -- Color Data
                c.id as color_id,
                c.name_en as color_name_en,
                c.name_ar as color_name_ar,
                c.hex_code as color_hex_code,

                -- Size Data
                s.id as size_id,
                s.name_en as size_name_en,
                s.name_ar as size_name_ar,
                s.size_type as size_type,

                -- Price Data
                pp.price as price,
                pp.discount_price as discount_price,
                pp.start_date as price_start_date,
                pp.end_date as price_end_date,

                -- Inventory Data
                i.quantity as inventory_quantity,
                i.reserved_quantity as inventory_reserved_quantity,
                i.available_quantity as inventory_available_quantity,
                i.low_stock_threshold as inventory_low_stock_threshold,

                -- Image Data
                pi.id as image_id,
                pi.image_url as image_url,
                pi.is_primary as image_is_primary

            FROM products p
            JOIN categories cat ON p.category_id = cat.id
            JOIN collections col ON cat.collection_id = col.id
            JOIN sub_categories sc ON p.sub_category_id = sc.id
            JOIN brands b ON p.brand_id = b.id
            JOIN product_variants pv ON p.id = pv.product_id
            LEFT JOIN colors c ON pv.color_id = c.id
            LEFT JOIN sizes s ON pv.size_id = s.id
            JOIN product_prices pp ON pv.id = pp.variant_id
                AND (pp.end_date IS NULL OR pp.end_date > NOW())
            JOIN inventory i ON pv.id = i.product_variant_id
            JOIN product_images pi ON p.id = pi.product_id
            WHERE p.id = :productId
              AND p.is_deleted = false
            --- removed to be displayed on admin dashboard  --- AND p.is_active= true
            ORDER BY pi.is_primary DESC, pv.id ASC
            """, nativeQuery = true)
    List<ProductDetailsProjection> findProductDetailsById(@Param("productId") Long productId);

    @Query(value = """
            SELECT
                p.id as product_id,
                p.name_en as product_name_en,
                p.name_ar as product_name_ar,
                p.description_en as product_description_en,
                p.description_ar as product_description_ar,
                p.is_active as product_is_active,
                p.is_top_seller as product_is_top_seller,
                p.is_trend as product_is_trend,
                p.is_new as product_is_new,
                p.is_deleted as product_is_deleted,
                p.created_at as product_created_at,
                p.updated_at as product_updated_at,

                -- Collection Data
                col.id as collection_id,
                col.name_en as collection_name_en,
                col.name_ar as collection_name_ar,
                col.image_url as collection_image_url,

                -- Category Data
                cat.id as category_id,
                cat.name_en as category_name_en,
                cat.name_ar as category_name_ar,
                cat.image_url as category_image_url,

                -- SubCategory Data
                sc.id as sub_category_id,
                sc.name_en as sub_category_name_en,
                sc.name_ar as sub_category_name_ar,

                -- Brand Data
                b.id as brand_id,
                b.name_en as brand_name_en,
                b.name_ar as brand_name_ar,
                b.image_url as brand_image_url,

                -- Variant Data
                pv.id as variant_id,
                pv.sku as variant_sku,
                pv.is_active as variant_is_active,

                -- Color Data
                c.id as color_id,
                c.name_en as color_name_en,
                c.name_ar as color_name_ar,
                c.hex_code as color_hex_code,

                -- Size Data
                s.id as size_id,
                s.name_en as size_name_en,
                s.name_ar as size_name_ar,
                s.size_type as size_type,

                -- Price Data
                pp.price as price,
                pp.discount_price as discount_price,
                pp.start_date as price_start_date,
                pp.end_date as price_end_date,

                -- Inventory Data
                i.quantity as inventory_quantity,
                i.reserved_quantity as inventory_reserved_quantity,
                i.available_quantity as inventory_available_quantity,
                i.low_stock_threshold as inventory_low_stock_threshold,

                -- Image Data
                pi.id as image_id,
                pi.image_url as image_url,
                pi.is_primary as image_is_primary

            FROM products p
            JOIN categories cat ON p.category_id = cat.id
            JOIN collections col ON cat.collection_id = col.id
            JOIN sub_categories sc ON p.sub_category_id = sc.id
            JOIN brands b ON p.brand_id = b.id
            LEFT JOIN product_variants pv ON p.id = pv.product_id
            LEFT JOIN colors c ON pv.color_id = c.id
            LEFT JOIN sizes s ON pv.size_id = s.id
            LEFT JOIN product_prices pp ON pv.id = pp.variant_id
                AND (pp.end_date IS NULL OR pp.end_date > NOW())
            LEFT JOIN inventory i ON pv.id = i.product_variant_id
            LEFT JOIN product_images pi ON p.id = pi.product_id
            WHERE p.id = :productId
                AND p.is_deleted = false
            --- removed to be displayed on admin dashboard  --- AND p.is_active= true
            ORDER BY pi.is_primary DESC, pv.id ASC
            """, nativeQuery = true)
    List<ProductDetailsProjection> findProductDetailsByIdForDashboard(@Param("productId") Long productId);

    String productsListWithFiltersQuery = """
            FROM products p
                JOIN categories cat ON p.category_id = cat.id
                JOIN collections col ON cat.collection_id = col.id
                JOIN sub_categories sc ON p.sub_category_id = sc.id
                JOIN brands b ON p.brand_id = b.id
                JOIN product_variants pv ON p.id = pv.product_id
                    AND pv.is_active = true
                LEFT JOIN colors c ON pv.color_id = c.id
                JOIN product_prices pp ON pv.id = pp.variant_id
                    AND (pp.end_date IS NULL OR pp.end_date > NOW())
                LEFT JOIN sizes s ON pv.size_id = s.id
            WHERE p.is_deleted = false
                AND p.is_active = true
                AND (COALESCE(:collectionId) IS NULL OR col.id= :collectionId)
                AND (COALESCE(:categoryIds) IS NULL OR cat.id IN (:categoryIds))
                AND (COALESCE(:subCategoryIds) IS NULL OR sc.id IN (:subCategoryIds))
                AND (COALESCE(:brandIds) IS NULL OR b.id IN (:brandIds))
                AND (COALESCE(:colorIds) IS NULL OR c.id IN (:colorIds))
                AND (COALESCE(:sizeIds) IS NULL OR s.id IN (:sizeIds))
                AND (COALESCE(:minPrice) IS NULL OR pp.price >= :minPrice)
                AND (COALESCE(:maxPrice) IS NULL OR pp.price <= :maxPrice)
                AND (COALESCE(:search) IS NULL OR p.name_en ILIKE '%' || :search || '%' OR
                    p.name_ar ILIKE '%' || :search || '%')
                AND (COALESCE(:isNew) IS NULL OR p.is_new = :isNew)
                AND (COALESCE(:isTrend) IS NULL OR p.is_trend = :isTrend)
                AND (COALESCE(:isTopSeller) IS NULL OR p.is_top_seller = :isTopSeller)
            """;

    @Query(value = "SELECT COUNT(DISTINCT p.id) " + productsListWithFiltersQuery, nativeQuery = true)
    Long countProductsListWithFilters(
            @Param("collectionId") Integer collectionId,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("subCategoryIds") List<Long> subCategoryIds,
            @Param("brandIds") List<Long> brandIds,
            @Param("colorIds") List<Long> colorIds,
            @Param("sizeIds") List<Long> sizeIds,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("search") String search,
            @Param("isNew") Boolean isNew,
            @Param("isTrend") Boolean isTrend,
            @Param("isTopSeller") Boolean isTopSeller);

    @Query(value = """
            SELECT
                p.id,
                p.name_en AS product_name_en,
                p.name_ar AS product_name_ar,
                p.description_en AS product_description_en,
                p.description_ar AS product_description_ar,
                c.id AS category_id,
                c.name_en AS category_name_en,
                c.name_ar AS category_name_ar,
                sc.id AS sub_category_id,
                sc.name_en AS sub_category_name_en,
                sc.name_ar AS sub_category_name_ar,
                b.id AS brand_id,
                b.name_en AS brand_name_en,
                b.name_ar AS brand_name_ar,
                b.image_url AS brand_image_url,
                p.is_new,
                p.is_trend,
                p.is_top_seller,
                col.id AS collection_id,
                col.name_en AS collection_name_en,
                col.name_ar AS collection_name_ar,
                pro_im.image_url AS primary_image
            FROM products p
            JOIN categories c ON p.category_id = c.id
            JOIN collections col ON c.collection_id = col.id
            JOIN sub_categories sc ON p.sub_category_id = sc.id
            JOIN brands b ON p.brand_id = b.id
            JOIN product_images pro_im ON p.id= pro_im.product_id
                AND  pro_im.is_primary= true
            WHERE p.is_deleted = false
                AND p.is_active = true
                AND (COALESCE(:productId) IS NULL OR p.id = :productId)
                AND (COALESCE(:collectionId) IS NULL OR col.id = :collectionId)
                AND (COALESCE(:categoryIds) IS NULL OR c.id IN (:categoryIds))
                AND (COALESCE(:subCategoryIds) IS NULL OR sc.id IN (:subCategoryIds))
                AND (COALESCE(:brandIds) IS NULL OR b.id IN (:brandIds))
                AND (COALESCE(:search) IS NULL
                     OR p.name_en ILIKE '%' || :search || '%'
                     OR p.name_ar ILIKE '%' || :search || '%')
                AND (COALESCE(:isNew) IS NULL OR p.is_new = :isNew)
                AND (COALESCE(:isTrend) IS NULL OR p.is_trend = :isTrend)
                AND (COALESCE(:isTopSeller) IS NULL OR p.is_top_seller = :isTopSeller)
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<ProductBasicDataProjection> fetchBasicData(
            @Param("productId") Long productId,
            @Param("collectionId") Integer collectionId,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("subCategoryIds") List<Long> subCategoryIds,
            @Param("brandIds") List<Long> brandIds,
            @Param("search") String search,
            @Param("isNew") Boolean isNew,
            @Param("isTrend") Boolean isTrend,
            @Param("isTopSeller") Boolean isTopSeller,
            @Param("limit") int limit,
            @Param("offset") int offset);

    @Query(value = """
            SELECT
                pv.id, pv.product_id, pp.price, pp.discount_price,
                inv.available_quantity, c.id AS color_id,
                c.hex_code, s.name_en AS size_name_en,
                s.name_ar AS size_name_ar, s.id AS size_id
            FROM product_variants pv
                LEFT JOIN colors c ON pv.color_id = c.id
                JOIN product_prices pp ON pv.id = pp.variant_id
                    AND (pp.end_date IS NULL OR pp.end_date > NOW())
                LEFT JOIN sizes s ON pv.size_id = s.id
                JOIN inventory inv ON pv.id = inv.product_variant_id
            WHERE pv.product_id IN(:productIds)
                AND (COALESCE(:colorIds) IS NULL OR c.id IN (:colorIds))
                AND (COALESCE(:sizeIds) IS NULL OR s.id IN (:sizeIds))
                AND (COALESCE(:minPrice) IS NULL OR pp.price >= :minPrice)
                AND (COALESCE(:maxPrice) IS NULL OR pp.price <= :maxPrice)
            """, nativeQuery = true)
    public List<ProductsListVariantProjection> fetchVariantsByProductsIds(
            @Param("productIds") List<Long> productIds,
            @Param("colorIds") List<Long> colorIds,
            @Param("sizeIds") List<Long> sizeIds,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice);
}
