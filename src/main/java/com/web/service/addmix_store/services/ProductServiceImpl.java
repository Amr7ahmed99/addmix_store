package com.web.service.addmix_store.services;

import com.web.service.addmix_store.Exceptions.EntityNotFoundException;
import com.web.service.addmix_store.dtos.ColorDTO;
import com.web.service.addmix_store.dtos.ProductCreateRequestDTO;
import com.web.service.addmix_store.dtos.ProductImageDTO;
import com.web.service.addmix_store.dtos.ProductsListFilterRequestDTO;
import com.web.service.addmix_store.dtos.ProductsListForWithFiltersDTO;
import com.web.service.addmix_store.dtos.SizeDTO;
import com.web.service.addmix_store.dtos.dashboard.ProductVariantDetailsDTO;
import com.web.service.addmix_store.dtos.ProductsListVariantDTO;
import com.web.service.addmix_store.dtos.ProductsListVariantsDTO;
import com.web.service.addmix_store.dtos.dashboard.BrandDTO;
import com.web.service.addmix_store.dtos.dashboard.CategoryDTO;
import com.web.service.addmix_store.dtos.dashboard.CollectionDTO;
import com.web.service.addmix_store.dtos.dashboard.ProductDTO;
import com.web.service.addmix_store.dtos.dashboard.ProductDetailsResponseDTO;
import com.web.service.addmix_store.dtos.dashboard.ProductFilterDataResponseDTO;
import com.web.service.addmix_store.dtos.dashboard.ProductListResponseDTO;
import com.web.service.addmix_store.dtos.dashboard.ProductUpdateRequestDTO;
import com.web.service.addmix_store.dtos.dashboard.ProductsListDTO;
import com.web.service.addmix_store.dtos.dashboard.SubCategoryDTO;
import com.web.service.addmix_store.dtos.dashboard.UpdateVariantPriceRequestDTO;
import com.web.service.addmix_store.dtos.response.ProductProfileResponseDTO;
import com.web.service.addmix_store.dtos.response.TopSellersProductResponseDTO;
import com.web.service.addmix_store.dtos.response.TrendingProductsResponseDTO;
import com.web.service.addmix_store.dtos.response.ProductsListResponseDTO;
import com.web.service.addmix_store.models.Brand;
import com.web.service.addmix_store.models.Category;
import com.web.service.addmix_store.models.Color;
import com.web.service.addmix_store.models.Product;
import com.web.service.addmix_store.models.ProductImage;
import com.web.service.addmix_store.models.ProductPrice;
import com.web.service.addmix_store.models.ProductVariant;
import com.web.service.addmix_store.models.Size;
import com.web.service.addmix_store.models.SubCategory;
import com.web.service.addmix_store.projections.ProductBasicDataProjection;
import com.web.service.addmix_store.projections.ProductColorsProjection;
import com.web.service.addmix_store.projections.ProductsListVariantProjection;
import com.web.service.addmix_store.projections.TrendingProductsProjection;
import com.web.service.addmix_store.projections.dashboard.BrandProjection;
import com.web.service.addmix_store.projections.dashboard.CategoryProjection;
import com.web.service.addmix_store.projections.dashboard.CollectionProjection;
import com.web.service.addmix_store.projections.dashboard.ProductDetailsProjection;
import com.web.service.addmix_store.projections.dashboard.ProductListProjection;
import com.web.service.addmix_store.projections.dashboard.SubCategoryProjection;
import com.web.service.addmix_store.repository.BrandRepository;
import com.web.service.addmix_store.repository.CategoryRepository;
import com.web.service.addmix_store.repository.ColorRepository;
import com.web.service.addmix_store.repository.ProductRepository;
import com.web.service.addmix_store.repository.ProductVariantRepository;
import com.web.service.addmix_store.repository.SizeRepository;
import com.web.service.addmix_store.repository.SubCategoryRepository;
import com.web.service.addmix_store.repository.ProductRepository.ProductBasicProjection;
import com.web.service.addmix_store.repository.ProductRepository.ProductColorProjection;
import com.web.service.addmix_store.repository.ProductRepository.ProductSizeProjection;
import com.web.service.addmix_store.services.contracts.ProductService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

        private final ProductRepository productRepository;
        private final ProductVariantRepository productVariantRepository;
        private final CategoryRepository categoryRepository;
        private final SubCategoryRepository subCategoryRepository;
        private final BrandRepository brandRepository;
        private final ColorRepository colorRepository;
        private final SizeRepository sizeRepository;
        private final ProductImageService productImageService;

        // Soft Delete
        public void deleteProduct(Long id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Product not found"));
                product.setIsDeleted(true);
                productRepository.save(product);
        }

        public ProductProfileResponseDTO getProductDetails(Long productId, int limitReviews, int offsetReviews) {
                return new ProductProfileResponseDTO();
        }

        @Override
        @Transactional
        public Long create(ProductCreateRequestDTO request) {
                Category category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new EntityNotFoundException("Invalid category"));
                Brand brand = brandRepository.findById(request.getBrandId())
                        .orElseThrow(() -> new EntityNotFoundException("Invalid brand"));
                SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                        .orElseThrow(() -> new EntityNotFoundException("Invalid subcategory"));

                Product product = Product.builder()
                        .category(category)
                        .subCategory(subCategory)
                        .brand(brand)
                        .nameEn(request.getNameEn())
                        .nameAr(request.getNameAr())
                        .descriptionEn(request.getDescriptionEn())
                        .descriptionAr(request.getDescriptionAr())
                        .build();

                productRepository.save(product);

                log.info("Received {} images for product {}", request.getImages().size(), product.getId());

                // handle image upload + DB save
                List<ProductImage> uploaded = productImageService.uploadImages(product, request.getImages());
                productImageService.setPrimaryImage(product, request.getPrimaryImageIndex(), uploaded);

                return product.getId();
        }

        @Override
        @Transactional(readOnly = true)
        public ProductsListVariantsDTO getProductsVariants() {
                List<Color> colors = this.colorRepository.findAll();
                List<Size> sizes = this.sizeRepository.findAll();

                List<ColorDTO> colorDTO = colors.stream()
                                .map(color -> ColorDTO.builder().id(color.getId()).hexCode(color.getHexCode()).build())
                                .collect(Collectors.toList());

                List<SizeDTO> sizeDTOs = sizes.stream()
                                .map(size -> SizeDTO.builder().id(size.getId()).nameAr(size.getNameAr())
                                                .nameEn(size.getNameEn()).build())
                                .collect(Collectors.toList());

                return new ProductsListVariantsDTO(colorDTO, sizeDTOs);
        }

        @Override
        @Transactional(readOnly = true)
        // fetching products to display them in user UI with filters
        public ProductsListResponseDTO getProductsListWithFilter(ProductsListFilterRequestDTO filterRequest) {
                try {
                        int offset = filterRequest.getPage() * filterRequest.getLimit();

                        // Get products with filters
                        List<ProductBasicDataProjection> projections = productRepository.fetchBasicData(
                                        null,
                                        filterRequest.getCollectionId(),
                                        filterRequest.getCategoryIds(),
                                        filterRequest.getSubCategoryIds(),
                                        filterRequest.getBrandIds(),
                                        filterRequest.getSearch(),
                                        filterRequest.getIsNew(),
                                        filterRequest.getIsTrend(),
                                        filterRequest.getIsTopSeller(),
                                        filterRequest.getLimit(),
                                        offset);

                        // Get total count
                        long totalCount = productRepository.countProductsListWithFilters(
                                        filterRequest.getCollectionId(),
                                        filterRequest.getCategoryIds(),
                                        filterRequest.getSubCategoryIds(),
                                        filterRequest.getBrandIds(),
                                        filterRequest.getColorIds(),
                                        filterRequest.getSizeIds(),
                                        filterRequest.getMinPrice(),
                                        filterRequest.getMaxPrice(),
                                        filterRequest.getSearch(),
                                        filterRequest.getIsNew(),
                                        filterRequest.getIsTrend(),
                                        filterRequest.getIsTopSeller());

                        // Convert projections to DTOs
                        List<ProductsListForWithFiltersDTO> products = mapProductBasicDataFromProjectionsToDTOs(
                                        projections);

                        // fetch products variants
                        List<ProductsListVariantProjection> productsVariants = productRepository
                                        .fetchVariantsByProductsIds(
                                                        products.stream().map(pro -> pro.id)
                                                                        .collect(Collectors.toList()),
                                                        filterRequest.getColorIds(), filterRequest.getSizeIds(),
                                                        filterRequest.getMinPrice(), filterRequest.getMaxPrice());

                        mapProductVariantDataFromProjectionsToDTOs(productsVariants, products);

                        // Build response
                        return buildWebProductListResponse(products, filterRequest.getPage(), filterRequest.getLimit(),
                                        totalCount);

                } catch (Exception e) {
                        log.error("Error fetching web products with filters: {}", filterRequest, e);
                        throw new RuntimeException("Failed to fetch products", e);
                }
        }

        private void mapProductVariantDataFromProjectionsToDTOs(
                        List<ProductsListVariantProjection> projections,
                        List<ProductsListForWithFiltersDTO> dtos) {

                Map<Long, ProductsListForWithFiltersDTO> dtoMap = dtos.stream()
                                .collect(Collectors.toMap(dto -> dto.id, Function.identity()));

                projections.forEach(proj -> {
                        ProductsListForWithFiltersDTO dto = dtoMap.get(proj.getProductId());
                        if (dto != null) {
                                ProductsListVariantDTO variant = createVariantDTO(proj);
                                if (dto.getVariants() == null) {
                                        dto.setVariants(new ArrayList<>());
                                }
                                dto.getVariants().add(variant);
                        }
                });
        }

        private ProductsListVariantDTO createVariantDTO(ProductsListVariantProjection proj) {
                return ProductsListVariantDTO.builder()
                                .id(proj.getProductId())
                                .productId(proj.getProductId())
                                .price(proj.getPrice())
                                .discountPrice(proj.getDiscountPrice())
                                .availableQuantity(proj.getAvailableQuantity())
                                .color(ColorDTO.builder()
                                                .id(proj.getColorId())
                                                .hexCode(proj.getHexCode())
                                                .build())
                                .size(SizeDTO.builder()
                                                .id(proj.getSizeId())
                                                .nameAr(proj.getSizeNameAr())
                                                .nameEn(proj.getSizeNameEn())
                                                .build())
                                .build();
        }

        private List<ProductsListForWithFiltersDTO> mapProductBasicDataFromProjectionsToDTOs(
                        List<ProductBasicDataProjection> projections) {

                List<ProductsListForWithFiltersDTO> result = new ArrayList<>();

                for (ProductBasicDataProjection proj : projections) {
                        result.add(ProductsListForWithFiltersDTO
                                        .builder().id(proj.getId())
                                        .nameEn(proj.getProductNameEn())
                                        .nameAr(proj.getProductNameAr())
                                        .descriptionEn(proj.getProductDescriptionEn())
                                        .descriptionAr(proj.getProductDescriptionAr())
                                        .isNew(proj.getIsNew())
                                        .isTrend(proj.getIsTrend())
                                        .isTopSeller(proj.getIsTopSeller())
                                        .collection(CollectionDTO.builder().id(proj.getCollectionId())
                                                        .nameAr(proj.getCollectionNameAr())
                                                        .nameEn(proj.getCollectionNameEn()).build())
                                        .category(CategoryDTO.builder().id(proj.getCategoryId())
                                                        .nameAr(proj.getCategoryNameAr())
                                                        .nameEn(proj.getCategoryNameEn()).build())
                                        .subCategory(SubCategoryDTO.builder().id(proj.getSubCategoryId())
                                                        .nameAr(proj.getSubCategoryNameAr())
                                                        .nameEn(proj.getSubCategoryNameEn()).build())
                                        .brand(BrandDTO.builder().id(proj.getBrandId())
                                                        .nameAr(proj.getBrandNameAr())
                                                        .nameEn(proj.getBrandNameEn())
                                                        .imageUrl(proj.getBrandImageUrl()).build())
                                        .primaryImageUrl(proj.getPrimaryImage())
                                        .build());
                }

                return result;
        }

        @Cacheable(value = "topSelling", key = "'top_sellers'", condition = "#result != null && #result.size() > 0", unless = "#result == null || #result.empty")
        @Override
        @Transactional(readOnly = true)
        public List<TopSellersProductResponseDTO> getTopSellerProducts() {

                try {
                        List<ProductBasicProjection> basicProducts = productRepository.findTopSellerProductsBasic();

                        if (basicProducts.isEmpty()) {
                                return Collections.emptyList();
                        }

                        List<Long> productIds = basicProducts.stream()
                                        .map(ProductBasicProjection::getId)
                                        .collect(Collectors.toList());

                        CompletableFuture<List<ProductColorProjection>> colorsFuture = findColorsByProductIdsAsync(
                                        productIds);
                        // CompletableFuture<List<ProductSizeProjection>> sizesFuture =
                        // findSizesByProductIdsAsync(
                        // productIds);
                        // CompletableFuture.allOf(colorsFuture, sizesFuture).join();

                        List<ProductColorProjection> colors = colorsFuture.get();
                        // List<ProductSizeProjection> sizes = sizesFuture.get();

                        // List<TopSellersProductResponseDTO> result = mergeProductData(basicProducts,
                        // colors, sizes);
                        List<TopSellersProductResponseDTO> result = mergeProductData(basicProducts, colors, null);

                        log.info("Fetched {} top seller products with {} colors",
                                        result.size(), colors.size());
                        return result;
                } catch (Exception e) {
                        log.error("Error fetching top seller products", e);
                        throw new RuntimeException("Failed to fetch top seller products", e);
                }
        }

        @Cacheable(value = "trending", key = "'trending'", condition = "#trendingDto != null", unless = "#trendingDto == null || #trendingDto.empty")
        @Override
        @Transactional(readOnly = true)
        public List<TrendingProductsResponseDTO> getTrendingProducts() {

                try {
                        List<TrendingProductsProjection> trending = productRepository.getTrendingProducts();
                        if (trending.isEmpty()) {
                                return Collections.emptyList();
                        }

                        List<Long> productIds = trending.stream()
                                        .map(TrendingProductsProjection::getId)
                                        .collect(Collectors.toList());

                        Map<Long, List<ColorDTO>> colorsMap = colorRepository
                                        .findColorsByProductIds(productIds)
                                        .stream()
                                        .collect(Collectors.groupingBy(
                                                        ProductColorsProjection::getProductId,
                                                        Collectors.mapping(color -> ColorDTO.builder()
                                                                        .hexCode(color.getHexCode())
                                                                        .nameEn(color.getNameEn())
                                                                        .nameAr(color.getNameAr())
                                                                        .build(),
                                                                        Collectors.toList())));

                        List<TrendingProductsResponseDTO> trendingDto = trending.stream()
                                        .map(proj -> TrendingProductsResponseDTO.builder()
                                                        .id(proj.getId())
                                                        .nameEn(proj.getNameEn())
                                                        .nameAr(proj.getNameAr())
                                                        .descriptionEn(proj.getDescriptionEn())
                                                        .descriptionAr(proj.getDescriptionAr())
                                                        .imageUrl(proj.getImageUrl())
                                                        .price(proj.getPrice())
                                                        .discountPrice(proj.getDiscountPrice())
                                                        .brandNameEn(proj.getBrandNameEn())
                                                        .brandNameAr(proj.getBrandNameAr())
                                                        .brandImageUrl(proj.getBrandImageUrl())
                                                        .categoryNameEn(proj.getCategoryNameEn())
                                                        .categoryNameAr(proj.getCategoryNameAr())
                                                        .subCategoryNameEn(proj.getSubCategoryNameEn())
                                                        .subCategoryNameAr(proj.getSubCategoryNameAr())
                                                        .collectionNameEn(proj.getCollectionNameEn())
                                                        .collectionNameAr(proj.getCollectionNameAr())
                                                        .colors(colorsMap.getOrDefault(proj.getId(),
                                                                        Collections.emptyList()))
                                                        .build())
                                        .collect(Collectors.toList());

                        log.info("Fetched {} trending products",
                                        trending.size());
                        return trendingDto;
                } catch (Exception e) {
                        log.error("Error fetching trending products", e);
                        throw new RuntimeException("Failed to fetch trending products", e);
                }
        }

        private List<TopSellersProductResponseDTO> mergeProductData(
                        List<ProductBasicProjection> basicProducts,
                        List<ProductColorProjection> colors,
                        List<ProductSizeProjection> sizes) {

                Map<Long, List<ProductColorProjection>> colorsByProductId = colors.stream()
                                .collect(Collectors.groupingBy(ProductColorProjection::getProductId));

                // Map<Long, List<ProductSizeProjection>> sizesByProductId = sizes.stream()
                // .collect(Collectors.groupingBy(ProductSizeProjection::getProductId));

                return basicProducts.stream()
                                .map(basic -> {
                                        Long productId = basic.getId();

                                        List<ColorDTO> colorDTOs = colorsByProductId
                                                        .getOrDefault(productId, Collections.emptyList())
                                                        .stream()
                                                        .map(color -> ColorDTO.builder()
                                                                        .hexCode(color.getHexCode())
                                                                        .nameEn(color.getNameEn())
                                                                        .nameAr(color.getNameAr())
                                                                        .build())
                                                        .distinct()
                                                        .collect(Collectors.toList());

                                        // List<SizeDTO> sizeDTOs = sizesByProductId
                                        // .getOrDefault(productId, Collections.emptyList())
                                        // .stream()
                                        // .map(size -> SizeDTO.builder()
                                        // .nameEn(size.getNameEn())
                                        // .nameAr(size.getNameAr())
                                        // .sizeType(size.getSizeType())
                                        // .build())
                                        // .distinct()
                                        // .collect(Collectors.toList());

                                        return TopSellersProductResponseDTO.builder()
                                                        .id(productId)
                                                        .nameEn(basic.getNameEn())
                                                        .nameAr(basic.getNameAr())
                                                        .brandName(basic.getBrandName())
                                                        .descriptionEn(basic.getDescriptionEn())
                                                        .descriptionAr(basic.getDescriptionAr())
                                                        .imageUrl(basic.getImageUrl())
                                                        .price(basic.getPrice())
                                                        .discountPrice(basic.getDiscountPrice())
                                                        .collectionNameUrl(basic.getCollectionNameUrl())
                                                        .categoryNameUrl(basic.getCategoryNameUrl())
                                                        .subCategoryNameUrl(basic.getSubCategoryNameUrl())
                                                        .isNew(basic.getIsNew())
                                                        .isTrend(basic.getIsTrend())
                                                        .savings(calculateSavings(basic.getPrice(),
                                                                        basic.getDiscountPrice()))
                                                        .colors(colorDTOs.isEmpty() ? getDefaultColors() : colorDTOs)
                                                        // .sizes(sizeDTOs) // Optional
                                                        .build();
                                })
                                .collect(Collectors.toList());
        }

        @Async
        public CompletableFuture<List<ProductColorProjection>> findColorsByProductIdsAsync(List<Long> productIds) {
                return CompletableFuture.completedFuture(productRepository.findColorsByProductIds(productIds));
        }

        @Async
        public CompletableFuture<List<ProductSizeProjection>> findSizesByProductIdsAsync(List<Long> productIds) {
                return CompletableFuture.completedFuture(productRepository.findSizesByProductIds(productIds));
        }

        private List<ColorDTO> getDefaultColors() {
                return List.of(
                                ColorDTO.builder()
                                                .hexCode("#000000")
                                                .nameEn("Black")
                                                .nameAr("أسود")
                                                .build());
        }

        @Override
        @CacheEvict(value = "topSelling", allEntries = true)
        public void markProductAsTopSeller(Long productId) {
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new RuntimeException("Product not found"));

                product.setIsTopSeller(true);
                productRepository.save(product);

                log.info("Product {} marked as top seller, cache evicted", productId);
        }

        @Override
        @CacheEvict(value = "topSelling", allEntries = true)
        public void unmarkProductAsTopSeller(Long productId) {
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new RuntimeException("Product not found"));

                product.setIsTopSeller(false);
                productRepository.save(product);

                log.info("Product {} unmarked as top seller, cache evicted", productId);
        }

        @Override
        @Transactional
        public void toggleStatus(Long productId, String statusName) {
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new RuntimeException("Product not found"));

                Boolean newStatus= false;
                switch (statusName) {
                        case "isActive":
                                newStatus= !product.getIsActive();
                                product.setIsActive(newStatus);
                                break;
                        case "isTopSeller":
                                newStatus= !product.getIsTopSeller();
                                product.setIsTopSeller(newStatus);
                                break;
                        case "isTrend":
                                newStatus= !product.getIsTrend();
                                product.setIsTrend(newStatus);
                                break;
                        case "isNew":
                                newStatus= !product.getIsNew();
                                product.setIsNew(newStatus);
                                break;
                        default:
                                return;
                }

                productRepository.save(product);

                

                log.info("Product {}, {} status changed to {}", productId, statusName, product.getIsActive());
        }

        @Override
        @Transactional(readOnly = true)
        public ProductListResponseDTO getProductsList(int page, int size, int categoryId, int subCategoryId,
                        int brandId, int collectionId, String searchTxt) {
                try {
                        int offset = page * size;

                        List<ProductListProjection> projections = productRepository.findProductsListWithFilters(
                                        categoryId, subCategoryId, brandId, collectionId, searchTxt, size, offset);

                        long totalCount = productRepository.countProductsWithFilters(
                                        categoryId, subCategoryId, brandId, collectionId, searchTxt);

                        List<ProductsListDTO> products = projections.stream()
                                        .map(this::convertToBasicDTO)
                                        .collect(Collectors.toList());

                        return buildProductListResponse(products, page, size, totalCount);
                } catch (Exception e) {
                        log.error("Error fetching basic products", e);
                        throw new RuntimeException("Failed to fetch products", e);
                }
        }

        private ProductsListDTO convertToBasicDTO(ProductListProjection projection) {
                return ProductsListDTO.builder()
                                .id(projection.getId())
                                .nameEn(projection.getNameEn())
                                .nameAr(projection.getNameAr())
                                .price(projection.getPrice())
                                .discountPrice(projection.getDiscountPrice())
                                .imageUrl(projection.getImageUrl())
                                .isActive(projection.getIsActive())
                                .quantity(projection.getQuantity())
                                .build();
        }

        private ProductListResponseDTO buildProductListResponse(List<ProductsListDTO> products,
                        int currentPage, int pageSize,
                        long totalCount) {

                int totalPages = (int) Math.ceil((double) totalCount / pageSize);
                boolean hasNext = (currentPage + 1) < totalPages;
                boolean hasPrevious = currentPage > 0;

                return ProductListResponseDTO.builder()
                                .products(products)
                                .currentPage(currentPage)
                                .pageSize(pageSize)
                                .totalCount(totalCount)
                                .totalPages(totalPages)
                                .hasNext(hasNext)
                                .hasPrevious(hasPrevious)
                                .build();
        }

        @Override
        public List<CollectionDTO> getAllActiveCollections() {
                List<CollectionProjection> collections = productRepository.findAllActiveCollections();
                return convertCollectionsToDTO(collections);
        }

        @Override
        public List<CategoryDTO> getAllActiveCategories(Long collectionId) {
                List<CategoryProjection> categories = productRepository.findAllActiveCategories(collectionId);
                return convertCategoriesToDTO(categories);
        }

        @Override
        public List<SubCategoryDTO> getAllActiveSubCategories(Long categoryId) {
                List<SubCategoryProjection> subCategories = productRepository.findAllActiveSubCategories(categoryId);
                return convertSubCategoriesToDTO(subCategories);
        }

        @Override
        public List<BrandDTO> getAllActiveBrands() {
                List<BrandProjection> brands = productRepository.findAllActiveBrands();
                return convertBrandsToDTO(brands);
        }

        @Override
        @Transactional(readOnly = true)
        public ProductFilterDataResponseDTO getProductFilterData(Long collectionId, Long categoryId) {
                try {
                        // Convert projections to DTOs
                        return ProductFilterDataResponseDTO.builder()
                                        .collections(this.getAllActiveCollections())
                                        .categories(this.getAllActiveCategories(collectionId))
                                        .subCategories(this.getAllActiveSubCategories(categoryId))
                                        .brands(this.getAllActiveBrands())
                                        .build();

                } catch (Exception e) {
                        log.error("Error fetching product filter data", e);
                        throw new RuntimeException("Failed to fetch filter data", e);
                }
        }

        private List<CollectionDTO> convertCollectionsToDTO(List<CollectionProjection> projections) {
                return projections.stream()
                                .map(proj -> CollectionDTO.builder()
                                                .id(proj.getId())
                                                .nameEn(proj.getNameEn())
                                                .nameAr(proj.getNameAr())
                                                .build())
                                .collect(Collectors.toList());
        }

        private List<CategoryDTO> convertCategoriesToDTO(List<CategoryProjection> projections) {
                return projections.stream()
                                .map(proj -> CategoryDTO.builder()
                                                .id(proj.getId())
                                                .nameEn(proj.getNameEn())
                                                .nameAr(proj.getNameAr())
                                                .build())
                                .collect(Collectors.toList());
        }

        private List<SubCategoryDTO> convertSubCategoriesToDTO(List<SubCategoryProjection> projections) {
                return projections.stream()
                                .map(proj -> SubCategoryDTO.builder()
                                                .id(proj.getId())
                                                .nameEn(proj.getNameEn())
                                                .nameAr(proj.getNameAr())
                                                .build())
                                .collect(Collectors.toList());
        }

        private List<BrandDTO> convertBrandsToDTO(List<BrandProjection> projections) {
                return projections.stream()
                                .map(proj -> BrandDTO.builder()
                                                .id(proj.getId())
                                                .nameEn(proj.getNameEn())
                                                .nameAr(proj.getNameAr())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public ProductDetailsResponseDTO getProductDashboardDetails(Long productId) {
                try {
                        List<ProductDetailsProjection> projections = productRepository
                                        .findProductDetailsById(productId);

                        if (projections.isEmpty()) {
                                throw new RuntimeException("Product not found with id: " + productId);
                        }

                        return buildProductDetailsDTO(projections);

                } catch (Exception e) {
                        log.error("Error fetching product details for id: {}", productId, e);
                        throw new RuntimeException("Failed to fetch product details", e);
                }
        }

        @Override
        @Transactional(readOnly = true)
        public ProductDetailsResponseDTO getProductDashboardDetailsForDashboard(Long productId) {
                try {
                        List<ProductDetailsProjection> projections = productRepository
                                        .findProductDetailsByIdForDashboard(productId);

                        if (projections.isEmpty()) {
                                throw new RuntimeException("Product not found with id: " + productId);
                        }

                        return buildProductDetailsDTO(projections);

                } catch (Exception e) {
                        log.error("Error fetching product details for id: {}", productId, e);
                        throw new RuntimeException("Failed to fetch product details", e);
                }
        }

        private ProductDetailsResponseDTO buildProductDetailsDTO(List<ProductDetailsProjection> projections) {
                // fetching first projection to extract basic info of the product
                ProductDetailsProjection firstProjection = projections.get(0);

                ProductDetailsResponseDTO productDTO = extractProductBasicInfo(firstProjection);

                // extract variants
                Map<Long, ProductVariantDetailsDTO> variantsMap = new LinkedHashMap<>();
                Set<Long> processedImageIds = new HashSet<>();

                for (ProductDetailsProjection projection : projections) {
                        Long variantId = projection.getVariantId();
                        if (variantId != null) {
                                processVariantProjection(projection, variantsMap);
                        }

                        Long imageId = projection.getImageId();
                        if (imageId != null && !processedImageIds.contains(imageId)) {
                                processImageProjection(projection, productDTO);
                                processedImageIds.add(imageId);
                        }
                }

                productDTO.setVariants(new ArrayList<>(variantsMap.values()));

                return productDTO;
        }

        private ProductDetailsResponseDTO extractProductBasicInfo(ProductDetailsProjection projection) {
                return ProductDetailsResponseDTO.builder()
                                .id(projection.getProductId())
                                .nameEn(projection.getProductNameEn())
                                .nameAr(projection.getProductNameAr())
                                .descriptionEn(projection.getProductDescriptionEn())
                                .descriptionAr(projection.getProductDescriptionAr())
                                .isActive(projection.getProductIsActive())
                                .isTopSeller(projection.getProductIsTopSeller())
                                .isTrend(projection.getProductIsTrend())
                                .isNew(projection.getProductIsNew())
                                .isDeleted(projection.getProductIsDeleted())
                                .createdAt(projection.getProductCreatedAt())
                                .updatedAt(projection.getProductUpdatedAt())
                                .collection(extractCollectionInfo(projection))
                                .category(extractCategoryInfo(projection))
                                .subCategory(extractSubCategoryInfo(projection))
                                .brand(extractBrandInfo(projection))
                                .images(new ArrayList<>())
                                .build();
        }

        private void processVariantProjection(ProductDetailsProjection projection,
                        Map<Long, ProductVariantDetailsDTO> variantsMap) {
                Long variantId = projection.getVariantId();

                if (!variantsMap.containsKey(variantId)) {
                        ProductVariantDetailsDTO variant = ProductVariantDetailsDTO.builder()
                                        .id(variantId)
                                        .sku(projection.getVariantSku())
                                        .isActive(projection.getVariantIsActive())
                                        .color(extractColorInfo(projection))
                                        .size(extractSizeInfo(projection))
                                        .price(projection.getPrice())
                                        .discountPrice(projection.getDiscountPrice())
                                        .priceStartDate(projection.getPriceStartDate())
                                        .priceEndDate(projection.getPriceEndDate())
                                        .quantity(projection.getInventoryQuantity() != null
                                                        ? projection.getInventoryQuantity()
                                                        : 0)
                                        .reservedQuantity(projection.getInventoryReservedQuantity() != null
                                                        ? projection.getInventoryReservedQuantity()
                                                        : 0)
                                        .availableQuantity(projection.getInventoryAvailableQuantity() != null
                                                        ? projection.getInventoryAvailableQuantity()
                                                        : 0)
                                        .stockStatus(calculateStockStatus(
                                                        projection.getInventoryAvailableQuantity() != null
                                                                        ? projection.getInventoryAvailableQuantity()
                                                                        : 0,
                                                        projection.getInventoryLowStockThreshold() != null
                                                                        ? projection.getInventoryLowStockThreshold()
                                                                        : 0))
                                        .build();

                        variantsMap.put(variantId, variant);
                }
        }

        private void processImageProjection(ProductDetailsProjection projection, ProductDetailsResponseDTO productDTO) {
                ProductImageDTO image = ProductImageDTO.builder()
                                .id(projection.getImageId())
                                .imageUrl(projection.getImageUrl())
                                .isPrimary(projection.getImageIsPrimary())
                                .build();

                productDTO.getImages().add(image);
        }

        private CollectionDTO extractCollectionInfo(ProductDetailsProjection projection) {
                if (projection.getCollectionId() == null)
                        return null;

                return CollectionDTO.builder()
                                .id(projection.getCollectionId())
                                .nameEn(projection.getCollectionNameEn())
                                .nameAr(projection.getCollectionNameAr())
                                .build();
        }

        private CategoryDTO extractCategoryInfo(ProductDetailsProjection projection) {
                if (projection.getCategoryId() == null)
                        return null;

                return CategoryDTO.builder()
                                .id(projection.getCategoryId())
                                .nameEn(projection.getCategoryNameEn())
                                .nameAr(projection.getCategoryNameAr())
                                .build();
        }

        private SubCategoryDTO extractSubCategoryInfo(ProductDetailsProjection projection) {
                if (projection.getSubCategoryId() == null)
                        return null;

                return SubCategoryDTO.builder()
                                .id(projection.getSubCategoryId())
                                .nameEn(projection.getSubCategoryNameEn())
                                .nameAr(projection.getSubCategoryNameAr())
                                .build();
        }

        private BrandDTO extractBrandInfo(ProductDetailsProjection projection) {
                if (projection.getBrandId() == null)
                        return null;

                return BrandDTO.builder()
                                .id(projection.getBrandId())
                                .nameEn(projection.getBrandNameEn())
                                .nameAr(projection.getBrandNameAr())
                                .imageUrl(projection.getBrandImageUrl())
                                .build();
        }

        private ColorDTO extractColorInfo(ProductDetailsProjection projection) {
                if (projection.getColorId() == null)
                        return null;

                return ColorDTO.builder()
                                .id(projection.getColorId())
                                .nameEn(projection.getColorNameEn())
                                .nameAr(projection.getColorNameAr())
                                .hexCode(projection.getColorHexCode())
                                .build();
        }

        private SizeDTO extractSizeInfo(ProductDetailsProjection projection) {
                if (projection.getSizeId() == null)
                        return null;

                return SizeDTO.builder()
                                .id(projection.getSizeId())
                                .nameEn(projection.getSizeNameEn())
                                .nameAr(projection.getSizeNameAr())
                                .sizeType(projection.getSizeType())
                                .build();
        }

        private ProductsListResponseDTO buildWebProductListResponse(
                        List<ProductsListForWithFiltersDTO> products,
                        int currentPage, int pageSize,
                        long totalCount) {

                int totalPages = (int) Math.ceil((double) totalCount / pageSize);

                return ProductsListResponseDTO.builder()
                                .products(products.stream()
                                                .filter(pro -> pro.getVariants() != null
                                                                && !pro.getVariants().isEmpty())
                                                .toList())
                                .currentPage(currentPage)
                                .pageSize(pageSize)
                                .totalCount(totalCount)
                                .totalPages(totalPages)
                                .build();
        }

        private String calculateStockStatus(int availableQuantity, int lowStockThreshold) {
                if (availableQuantity == 0) {
                        return "Out of Stock";
                } else if (availableQuantity <= lowStockThreshold) {
                        return "Low Stock";
                } else {
                        return "In Stock";
                }
        }

        @Override
        @Transactional
        public void updateProductPrices(Long productId, UpdateVariantPriceRequestDTO request) {

                try {
                        Long variantsCount = this.productVariantRepository.countProductVariants(request.getVariantIds(),
                                        productId);
                        if (variantsCount != request.getVariantIds().size()) {
                                throw new RuntimeException("Some variants not found or don't belong to product");
                        }

                        LocalDate startDate = request.getPriceStartDate() != null ? request.getPriceStartDate()
                                        : LocalDate.now();

                        int updatedCount = this.productVariantRepository.updateVariantPrices(
                                        request.getPrice(),
                                        request.getDiscountPrice(),
                                        startDate,
                                        request.getPriceEndDate(),
                                        request.getVariantIds());
                        log.info("Updated {} variants prices for product id {}", updatedCount, productId);
                } catch (Exception e) {
                        log.error("Error updating product variants price for id: {}", productId, e);
                        throw new RuntimeException("Failed to update product variants price", e);
                }

        }

        @Override
        @Transactional
        public void updateProductBasicInfo(Long productId, ProductUpdateRequestDTO request) {
                try {
                        Product product = productRepository.findById(productId)
                                        .orElseThrow(() -> new RuntimeException("Product not found"));

                        // Update basic fields
                        if (request.getNameEn() != null)
                                product.setNameEn(request.getNameEn());
                        if (request.getNameAr() != null)
                                product.setNameAr(request.getNameAr());
                        if (request.getDescriptionEn() != null)
                                product.setDescriptionEn(request.getDescriptionEn());
                        if (request.getDescriptionAr() != null)
                                product.setDescriptionAr(request.getDescriptionAr());
                        // if (request.getIsActive() != null)
                        // product.setIsActive(request.getIsActive());
                        // if (request.getIsTopSeller() != null)
                        // product.setIsTopSeller(request.getIsTopSeller());

                        // Update relationships if provided
                        if (request.getCategoryId() != null) {
                                Category category = categoryRepository
                                                .findByIdAndIsDeletedFalse(request.getCategoryId())
                                                .orElseThrow(() -> new RuntimeException("Category not found"));
                                product.setCategory(category);
                        }

                        if (request.getSubCategoryId() != null) {
                                SubCategory subCategory = subCategoryRepository
                                                .findByIdAndIsDeletedFalse(request.getSubCategoryId())
                                                .orElseThrow(() -> new RuntimeException("SubCategory not found"));
                                product.setSubCategory(subCategory);
                        }

                        if (request.getBrandId() != null) {
                                Brand brand = brandRepository.findById(request.getBrandId())
                                                .orElseThrow(() -> new RuntimeException("Brand not found"));
                                product.setBrand(brand);
                        }

                        productRepository.save(product);
                        log.info("Product {} basic info updated", productId);

                        // Manual mapping to DTO
                        // return mapProductToDTO(updatedProduct);
                } catch (Exception e) {
                        log.error("Error updating product basic info for id: {}", productId, e);
                        throw new RuntimeException("Failed to update product", e);
                }
        }

        // Helper methods for mapping
        private ProductDTO mapProductToDTO(Product product) {
                return ProductDTO.builder()
                                .id(product.getId())
                                .nameEn(product.getNameEn())
                                .nameAr(product.getNameAr())
                                .descriptionEn(product.getDescriptionEn())
                                .descriptionAr(product.getDescriptionAr())
                                .category(mapCategoryToDTO(product.getCategory()))
                                .subCategory(mapSubCategoryToDTO(product.getSubCategory()))
                                .brand(mapBrandToDTO(product.getBrand()))
                                .build();
        }

        private ProductVariantDetailsDTO mapProductVariantToDTO(ProductVariant variant) {
                return ProductVariantDetailsDTO.builder()
                                .id(variant.getId())
                                .color(mapColorToDTO(variant.getColor()))
                                .size(mapSizeToDTO(variant.getSize()))
                                .sku(variant.getSku())
                                .isActive(variant.getIsActive())
                                .price(getCurrentPrice(variant))
                                .discountPrice(getCurrentDiscountPrice(variant))
                                .quantity(getInventoryQuantity(variant))
                                .availableQuantity(getAvailableQuantity(variant))
                                .damageQuantity(getDamagedQuantity(variant))
                                .stockStatus(getStockStatus(variant))
                                .build();
        }

        private CategoryDTO mapCategoryToDTO(Category category) {
                if (category == null)
                        return null;

                return CategoryDTO.builder()
                                .id(category.getId())
                                .nameEn(category.getNameEn())
                                .nameAr(category.getNameAr())
                                .build();
        }

        private SubCategoryDTO mapSubCategoryToDTO(SubCategory subCategory) {
                if (subCategory == null)
                        return null;

                return SubCategoryDTO.builder()
                                .id(subCategory.getId())
                                .nameEn(subCategory.getNameEn())
                                .nameAr(subCategory.getNameAr())
                                .build();
        }

        private BrandDTO mapBrandToDTO(Brand brand) {
                if (brand == null)
                        return null;

                return BrandDTO.builder()
                                .id(brand.getId())
                                .nameEn(brand.getNameEn())
                                .nameAr(brand.getNameAr())
                                .imageUrl(brand.getImageUrl())
                                .build();
        }

        private List<ProductVariant> mapVariantsToDTO(List<ProductVariant> variants) {
                if (variants == null || variants.isEmpty()) {
                        return Collections.emptyList();
                }

                return variants;
        }

        private ColorDTO mapColorToDTO(Color color) {
                if (color == null)
                        return null;

                return ColorDTO.builder()
                                .id(color.getId())
                                .nameEn(color.getNameEn())
                                .nameAr(color.getNameAr())
                                .hexCode(color.getHexCode())
                                .build();
        }

        private SizeDTO mapSizeToDTO(Size size) {
                if (size == null)
                        return null;

                return SizeDTO.builder()
                                .id(size.getId())
                                .nameEn(size.getNameEn())
                                .nameAr(size.getNameAr())
                                .sizeType(size.getSizeType())
                                .build();
        }

        // Helper methods for prices and inventory
        private Double getCurrentPrice(ProductVariant variant) {
                if (variant.getPrices() == null || variant.getPrices().isEmpty()) {
                        return null;
                }

                return variant.getPrices().stream()
                                .filter(ProductPrice::getIsActive)
                                .map(ProductPrice::getPrice)
                                .findFirst()
                                .orElse(null);
        }

        private Double getCurrentDiscountPrice(ProductVariant variant) {
                if (variant.getPrices() == null || variant.getPrices().isEmpty()) {
                        return null;
                }

                return variant.getPrices().stream()
                                .filter(ProductPrice::getIsActive)
                                .map(ProductPrice::getDiscountPrice)
                                .findFirst()
                                .orElse(null);
        }

        private Integer getInventoryQuantity(ProductVariant variant) {
                if (variant.getInventory() == null) {
                        return 0;
                }

                return variant.getInventory().getQuantity();
        }

        private Integer getAvailableQuantity(ProductVariant variant) {
                if (variant.getInventory() == null) {
                        return 0;
                }

                return variant.getInventory().getAvailableQuantity();
        }

        private Integer getDamagedQuantity(ProductVariant variant) {
                if (variant.getInventory() == null) {
                        return 0;
                }

                return variant.getInventory().getDamagedQuantity();
        }

        private String getStockStatus(ProductVariant variant) {
                if (variant.getInventory() == null) {
                        return "Out of Stock";
                }

                int availableQuantity = variant.getInventory().getAvailableQuantity();
                int lowStockThreshold = variant.getInventory().getLowStockThreshold();

                if (availableQuantity == 0) {
                        return "Out of Stock";
                } else if (availableQuantity <= lowStockThreshold) {
                        return "Low Stock";
                } else {
                        return "In Stock";
                }
        }

        public Long countByCategoryId(Long categoryId) {
                return productRepository.countByCategoryId(categoryId);
        }

        public Long countBySubCategoryId(Long subCategoryId) {
                return productRepository.countBySubCategoryId(subCategoryId);
        }

        public Long countByBrandId(Long brandId) {
                return productRepository.countByBrandId(brandId);
        }

        
}
