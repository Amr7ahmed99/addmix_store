package com.web.service.addmix_store.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "product_available_sizes",
    indexes = {
        @Index(name = "idx_pas_product", columnList = "product_id"),
        @Index(name = "idx_pas_product_size", columnList = "product_id, size_id", unique = true)
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAvailableSize extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    @Column(name = "display_order")
    private Integer displayOrder;
}