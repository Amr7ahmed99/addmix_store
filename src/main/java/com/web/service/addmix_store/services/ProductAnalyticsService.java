package com.web.service.addmix_store.services;

import com.web.service.addmix_store.dtos.ProductTopSellingVariantDTO;
import com.web.service.addmix_store.dtos.TopSellerProductsDTO;
import com.web.service.addmix_store.repository.ProductRepository;
import com.web.service.addmix_store.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductAnalyticsService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    @Cacheable(value = "topSelling", key = "#limit")
    @Transactional(readOnly = true)
    public List<TopSellerProductsDTO> getTopSellerProducts(int limit, String lang) throws Exception{

        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        log.info("Fetching top sellers from DB for limit: {}", limit);
        
        // Fetch top seller products
        // PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("product_id").descending());
        List<TopSellerProductsDTO> topSellerProducts = productRepository.findTopSellerProducts(lang, PageRequest.of(0, limit))
            .stream()
            .map(top -> TopSellerProductsDTO.builder()
                    .productId(top.getProductId())
                    .productName(top.getProductName())
                    .productDescription(top.getProductDescription())
                    .categoryName(top.getCategoryName())
                    .subCategoryName(top.getSubCategoryName())
                    .collectionName(top.getCollectionName())
                    .brandName(top.getBrandName())
                    .sku(top.getSku())
                    .totalSoldQuantity(top.getTotalSoldQuantity())
                    .originalPrice(top.getOriginalPrice())
                    .discountPrice(top.getDiscountPrice())
                    .primaryImageUrl(top.getPrimaryImageUrl())
                    .build())
            .toList();

        List<Long> productIds = topSellerProducts.stream()
            .map(TopSellerProductsDTO::getProductId)
            .toList();

        // Fetch top seller products Variants
        List<ProductTopSellingVariantDTO> productVariants = productVariantRepository.findVariantsByProductIds(productIds)
            .stream()
            .map(variant -> ProductTopSellingVariantDTO.builder()
                    .Id(variant.getId())
                    .productId(variant.getProductId())
                    .attributeName(variant.getAttributeName())
                    .attributeValue(variant.getAttributeValue())
                    .sku(variant.getSku())
                    .build())
            .toList();

        // Map<ProductId, List<Variants>>
        Map<Long, List<ProductTopSellingVariantDTO>> variantsMap = productVariants.stream()
            .collect(Collectors.groupingBy(ProductTopSellingVariantDTO::getProductId));

        // Assign variants for each product
        topSellerProducts.forEach(product -> 
            product.setVariants(variantsMap.getOrDefault(product.getProductId(), List.of()))
        );

        return topSellerProducts;
    }

}