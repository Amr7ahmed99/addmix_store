package com.web.service.addmix_store.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "sizes",
    indexes = {
        @Index(name = "idx_size_name_en", columnList = "nameEn"),
        @Index(name = "idx_size_name_ar", columnList = "nameAr"),
        @Index(name = "idx_size_type", columnList = "sizeType")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Size extends BaseEntity {
    
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "size_seq")
    @SequenceGenerator(name = "size_seq", sequenceName = "sizes_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String nameEn;

    @Column(nullable = false)
    private String nameAr;

    @Column(name = "size_type", nullable = false)
    private String sizeType; // CLOTHING, SHOES, ACCESSORIES

    @Column(name = "size_order")
    private Integer sizeOrder;

    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean isActive = true;
}