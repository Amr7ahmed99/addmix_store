package com.web.service.addmix_store.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "colors",
    indexes = {
        @Index(name = "idx_color_name_en", columnList = "nameEn"),
        @Index(name = "idx_color_name_ar", columnList = "nameAr"),
        @Index(name = "idx_color_hex", columnList = "hexCode")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Color extends BaseEntity {
    
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "color_seq")
    @SequenceGenerator(name = "color_seq", sequenceName = "colors_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String nameEn;

    @Column(nullable = false)
    private String nameAr;

    @Column(name = "hex_code", length = 12, nullable = false)
    private String hexCode;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "display_order")
    private Integer displayOrder;
}