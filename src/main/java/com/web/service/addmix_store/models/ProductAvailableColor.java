package com.web.service.addmix_store.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "product_available_colors",
    indexes = {
        @Index(name = "idx_pac_product", columnList = "product_id"),
        @Index(name = "idx_pac_product_color", columnList = "product_id, color_id", unique = true)
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAvailableColor extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @Column(name = "display_order")
    private Integer displayOrder;
}