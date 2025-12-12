package com.web.service.addmix_store.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "inventory",
    indexes = {
        @Index(name = "idx_inventory_variant", columnList = "product_variant_id", unique = true),
        @Index(name = "idx_inventory_available", columnList = "available_quantity")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseEntity {
    
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventory_seq")
    @SequenceGenerator(name = "inventory_seq", sequenceName = "inventory_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false, unique = true)
    private ProductVariant productVariant;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    @Builder.Default
    private Integer quantity = 0;

    @Column(name = "reserved_quantity", nullable = false, columnDefinition = "bigint default 0")
    @Builder.Default
    private Integer reservedQuantity = 0;

    @Column(name = "damaged_quantity", nullable = false, columnDefinition = "bigint default 0")
    @Builder.Default
    private Integer damagedQuantity = 0;

    @Column(name = "available_quantity", nullable = false)
    @Builder.Default
    private Integer availableQuantity = 0;

    @Column(name = "low_stock_threshold", columnDefinition = "int default 2")
    @Builder.Default
    private Integer lowStockThreshold = 2;

    @PreUpdate
    @PrePersist
    public void calculateAvailableQuantity() {
        this.availableQuantity = this.quantity - this.damagedQuantity -this.reservedQuantity;
    }
}