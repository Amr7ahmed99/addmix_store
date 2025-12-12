package com.web.service.addmix_store.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "product_color_images",
    indexes = {
        @Index(name = "idx_pci_product_color", columnList = "product_id, color_id"),
        @Index(name = "idx_pci_display_order", columnList = "display_order")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductColorImage extends BaseEntity {
    
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_color_image_seq")
    @SequenceGenerator(name = "product_color_image_seq", sequenceName = "product_color_images_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "image_type", nullable = false)
    private String imageType; // MAIN, THUMBNAIL, GALLERY

    @Column(name = "display_order")
    private Integer displayOrder;
}