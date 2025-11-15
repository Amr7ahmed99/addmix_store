package com.web.service.addmix_store.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.web.service.addmix_store.dtos.dashboard.ProductImageDTO;
import com.web.service.addmix_store.models.Product;
import com.web.service.addmix_store.models.ProductImage;
import com.web.service.addmix_store.repository.ProductImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageService {

    private final S3Service s3Service;
    private final ProductImageRepository productImageRepository;

    public List<ProductImage> uploadImages(Product product, List<MultipartFile> images) {
        List<ProductImage> saved = new ArrayList<>();

        for (MultipartFile img : images) {
            try {
                String imageUrl = s3Service.uploadProductImage(product.getId(), img, "gallery");
                ProductImage image = ProductImage.builder()
                        .product(product)
                        .imageUrl(imageUrl)
                        .isPrimary(false)
                        .build();
                saved.add(image);
            } catch (Exception e) {
                log.error("Failed to upload image {} for product {}", img.getName(), product.getId(), e);
                throw new RuntimeException("Failed to upload image " + img.getName());
            }
        }

        
        return productImageRepository.saveAll(saved);
    }

    public void setPrimaryImage(Product product, int primaryIndex, List<ProductImage> images) {
        if (images.isEmpty() || primaryIndex < 0 || primaryIndex >= images.size()) return;

        productImageRepository.updateAllPrimaryStatus(product.getId(), false);

        ProductImage primary = images.get(primaryIndex);
        primary.setIsPrimary(true);
        productImageRepository.save(primary);
    }

    @Transactional
    public void deleteImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        
        Long productId = image.getProduct().getId();
        boolean wasPrimary = image.getIsPrimary();
        
        // remove from S3
        s3Service.deleteImage(image.getImageUrl());
        
        // remove image from Database
        productImageRepository.delete(image);

        // if wasPrimary, select another image to be isPrimary
        if (wasPrimary) {
            productImageRepository.findFirstByProductId(productId)
                    .ifPresent(newPrimary -> toggleAllAndSetOneAsPrimary(newPrimary.getId()));
        }
    }

    private void toggleAllAndSetOneAsPrimary(Long imageId){
        ProductImage newPrimary = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        
        // set all isPrimary images by false 
        productImageRepository.updateAllPrimaryStatus(newPrimary.getProduct().getId(), false);
        
        // set the selected image as primary
        newPrimary.setIsPrimary(true);
        productImageRepository.save(newPrimary);
    }

    @Transactional
    public void setAsPrimary(Long imageId) {
        this.toggleAllAndSetOneAsPrimary(imageId);
    }

    public List<ProductImageDTO> getProductImages(Long productId) {
        return productImageRepository.findByProductId(productId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductImageDTO> getPrimaryImage(Long productId) {
        return productImageRepository.findByProductIdAndIsPrimaryTrue(productId)
                .map(this::mapToDTO);
    }

    private ProductImageDTO mapToDTO(ProductImage image) {
        return ProductImageDTO.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .isPrimary(image.getIsPrimary())
                .productId(image.getProduct().getId())
                .build();
    }

    // public void saveAllImages(List<ProductImage> images){
    //     this.productImageRepository.saveAll(images);
    // }
}
