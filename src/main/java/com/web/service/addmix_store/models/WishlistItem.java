package com.web.service.addmix_store.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Getter
@Setter
public class WishlistItem extends BaseEntity {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wishlist_item_seq")
    @jakarta.persistence.SequenceGenerator(name = "wishlist_item_seq", sequenceName = "wishlist_items_id_seq", allocationSize = 1)
    @jakarta.persistence.Column(name = "id", updatable = false, nullable = false)
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
