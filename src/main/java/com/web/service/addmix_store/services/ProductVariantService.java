package com.web.service.addmix_store.services;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.web.service.addmix_store.dtos.CreateVariantRequestDTO;
import com.web.service.addmix_store.dtos.response.VariantResponseDTO;
import com.web.service.addmix_store.models.Color;
import com.web.service.addmix_store.models.Inventory;
import com.web.service.addmix_store.models.Product;
import com.web.service.addmix_store.models.ProductPrice;
import com.web.service.addmix_store.models.ProductVariant;
import com.web.service.addmix_store.models.Size;
import com.web.service.addmix_store.repository.ColorRepository;
import com.web.service.addmix_store.repository.InventoryRepository;
import com.web.service.addmix_store.repository.ProductPriceRepository;
import com.web.service.addmix_store.repository.ProductRepository;
import com.web.service.addmix_store.repository.ProductVariantRepository;
import com.web.service.addmix_store.repository.SizeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductPriceRepository productPriceRepository;

    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;

    @Transactional
    public VariantResponseDTO createVariant(CreateVariantRequestDTO request) {
        try {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

            if (productVariantRepository.existsBySku(request.getSku())) {
                throw new RuntimeException("SKU already exists: " + request.getSku());
            }

            if(request.getPrice() <= 0) {
                throw new IllegalArgumentException("Price must be greater than 0");
            }

            if (productVariantRepository.existsByProductIdAndColorIdAndSizeId(
                    request.getProductId(), request.getColorId(), request.getSizeId())) {
                throw new RuntimeException("Variant with same color and size already exists for this product");
            }

            ProductVariant variant = ProductVariant.builder()
                    .product(product)
                    .color(request.getColorId() != null ? colorRepository.findById(request.getColorId())
                            .orElseThrow(() -> new RuntimeException("Color not found")) : null)
                    .size(request.getSizeId() != null ? sizeRepository.findById(request.getSizeId())
                            .orElseThrow(() -> new RuntimeException("Size not found")) : null)
                    .sku(request.getSku())
                    .isActive(true)
                    .build();

            ProductVariant savedVariant = productVariantRepository.save(variant);

            Inventory inventory = inventoryRepository.save(Inventory.builder()
                    .quantity(request.getQuantity())
                    .productVariant(savedVariant)
                    .build());

            ProductPrice productPrice = productPriceRepository.save(ProductPrice.builder()
                    .price(request.getPrice())
                    .startDate(LocalDateTime.now())
                    .productVariant(savedVariant)
                    .build());

            return buildVariantResponseDTO(savedVariant, inventory, productPrice);

        } catch (RuntimeException e) {
            log.error("Error creating variant for product id: {}", request.getProductId(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating variant for product id: {}", request.getProductId(), e);
            throw new RuntimeException("Failed to create variant", e);
        }
    }

    private VariantResponseDTO buildVariantResponseDTO(ProductVariant variant, Inventory inventory,
            ProductPrice price) {
        return VariantResponseDTO.builder()
                .id(variant.getId())
                .productId(variant.getProduct().getId())
                .colorId(variant.getColor() != null ? variant.getColor().getId() : null)
                .sizeId(variant.getSize() != null ? variant.getSize().getId() : null)
                .sku(variant.getSku())
                .price(price.getPrice())
                .quantity(inventory.getQuantity())
                .isActive(variant.getIsActive())
                .colorName(variant.getColor() != null ? variant.getColor().getNameEn() : "")
                .sizeName(variant.getSize() != null ? variant.getSize().getNameEn() : "")
                .build();
    }
}
