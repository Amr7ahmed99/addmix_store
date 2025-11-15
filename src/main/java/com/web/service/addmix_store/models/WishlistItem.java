package com.web.service.addmix_store.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(
    name = "wishlist_items",
    indexes = {
        @Index(name = "idx_wishlist_item_wishlist", columnList = "wishlist_id"),
        @Index(name = "idx_wishlist_item_variant", columnList = "product_variant_id"),
        @Index(name = "idx_wishlist_item_unique", columnList = "wishlist_id,product_variant_id", unique = true)
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class WishlistItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // الربط بالـ wishlist
    @ManyToOne
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    // المنتج/الفاريانت
    @ManyToOne
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;
}
