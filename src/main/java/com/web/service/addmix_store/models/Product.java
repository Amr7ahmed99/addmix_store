package com.web.service.addmix_store.models;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_product_name_en", columnList = "nameEn"),
        @Index(name = "idx_product_name_ar", columnList = "nameAr"),
        @Index(name = "idx_product_category", columnList = "category_id"),
        @Index(name = "idx_product_subcategory", columnList = "sub_category_id"),
        @Index(name = "idx_product_brand", columnList = "brand_id"),
        @Index(name = "idx_product_category_subcategory", columnList = "category_id, sub_category_id")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product extends BaseEntity {

    @Id 
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "products_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name= "name_en")
    private String nameEn;

    @Column(name= "name_ar")
    private String nameAr;

    @Column(name= "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name= "description_ar", columnDefinition = "TEXT")
    private String descriptionAr;
    
    @Column(name= "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;  // Soft delete

    @Column(name = "is_top_seller", nullable = false)
    @Builder.Default
    private Boolean isTopSeller = false;

    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_new", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isNew = false;

    @Column(name = "is_trend", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isTrend = false;

    @Column
    private Long createdBy;
    @Column
    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id", nullable = false)
    private SubCategory subCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductVariant> variants;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductImage> images;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductColorImage> colorImages;
}