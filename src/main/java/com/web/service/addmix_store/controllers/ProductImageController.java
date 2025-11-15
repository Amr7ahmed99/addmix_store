package com.web.service.addmix_store.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.web.service.addmix_store.Exceptions.EntityNotFoundException;
import com.web.service.addmix_store.dtos.dashboard.ProductImageDTO;
import com.web.service.addmix_store.models.Product;
import com.web.service.addmix_store.models.ProductImage;
import com.web.service.addmix_store.repository.ProductRepository;
import com.web.service.addmix_store.services.ProductImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;


@RestController
@AllArgsConstructor
@RequestMapping("/api/products/images")
// @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@CrossOrigin(
    origins = {
        "http://localhost:3000",
        "http://addmix-dashboard.s3-website-us-east-1.amazonaws.com",
        "http://addmix-wep-app.s3-website-us-east-1.amazonaws.com"
    },
    allowCredentials = "true"
)
@Tag(name = "Products Images", description = "APIs for products images management")
public class ProductImageController {

    private final ProductImageService productImageService;
    private final ProductRepository productRepository;


    @PostMapping("/{productId}")
    public ResponseEntity<List<ProductImageDTO>> uploadImages(
            @PathVariable Long productId,
            @RequestParam("files") List<MultipartFile> files) {
        
        Product product= this.productRepository.findById(productId)
            .orElseThrow(()-> new EntityNotFoundException("product not found"));
        List<ProductImage> uploadedImages = productImageService.uploadImages(product, files);
        
        return ResponseEntity.ok(ProductImageDTO.mapToDTO(uploadedImages));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        productImageService.deleteImage(imageId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{imageId}/primary")
    public ResponseEntity<Void> setAsPrimary(@PathVariable Long imageId) {
        productImageService.setAsPrimary(imageId);
        return ResponseEntity.ok().build();
    }
}
