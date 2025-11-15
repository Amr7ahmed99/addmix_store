package com.web.service.addmix_store.repository;

import com.web.service.addmix_store.models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductId(Long productId);
    
    Optional<ProductImage> findByProductIdAndIsPrimaryTrue(Long productId);
    
    Optional<ProductImage> findFirstByProductId(Long productId);
    
    long countByProductId(Long productId);
    
    boolean existsByProductIdAndIsPrimaryTrue(Long productId);
    
    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isPrimary = :isPrimary WHERE pi.product.id = :productId")
    void updateAllPrimaryStatus(@Param("productId") Long productId, @Param("isPrimary") Boolean isPrimary);
    
    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isPrimary = true WHERE pi.id = :imageId")
    void setAsPrimary(@Param("imageId") Long imageId);
    
    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isPrimary = false WHERE pi.product.id = :productId AND pi.id != :excludeImageId")
    void setOthersAsNonPrimary(@Param("productId") Long productId, @Param("excludeImageId") Long excludeImageId);
    
    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
    
    Optional<ProductImage> findByImageUrl(String imageUrl);
    
    List<ProductImage> findByProductIdInAndIsPrimaryTrue(List<Long> productIds);
}