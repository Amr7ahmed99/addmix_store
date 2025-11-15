package com.web.service.addmix_store.controllers;

import com.web.service.addmix_store.dtos.ProductCreateRequestDTO;
import com.web.service.addmix_store.dtos.ProductsListFilterRequestDTO;
import com.web.service.addmix_store.dtos.ProductsListVariantsDTO;
import com.web.service.addmix_store.dtos.StatusRequestDTO;
import com.web.service.addmix_store.dtos.dashboard.CategoryDTO;
import com.web.service.addmix_store.dtos.dashboard.ProductDetailsResponseDTO;
import com.web.service.addmix_store.dtos.dashboard.ProductFilterDataResponseDTO;
import com.web.service.addmix_store.dtos.dashboard.ProductListResponseDTO;
import com.web.service.addmix_store.dtos.dashboard.ProductUpdateRequestDTO;
import com.web.service.addmix_store.dtos.dashboard.SubCategoryDTO;
import com.web.service.addmix_store.dtos.dashboard.UpdateVariantPriceRequestDTO;
import com.web.service.addmix_store.dtos.response.ProductsListResponseDTO;
import com.web.service.addmix_store.dtos.response.TopSellersProductResponseDTO;
import com.web.service.addmix_store.dtos.response.TrendingProductsResponseDTO;
import com.web.service.addmix_store.models.Color;
import com.web.service.addmix_store.models.Size;
import com.web.service.addmix_store.repository.ColorRepository;
import com.web.service.addmix_store.repository.SizeRepository;
import com.web.service.addmix_store.services.ProductServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
// @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@CrossOrigin(
    origins = {
        "http://localhost:3000",
        "http://addmix-dashboard.s3-website-us-east-1.amazonaws.com",
        "http://addmix-wep-app.s3-website-us-east-1.amazonaws.com"
    },
    allowCredentials = "true"
)
@Slf4j
@Tag(name = "Products", description = "APIs for product management")
public class ProductController {

    private final ProductServiceImpl productService;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;

    // @GetMapping
    // public List<ProductDTOTest> getProducts(
    //         @RequestParam(required = false) Long categoryId,
    //         @RequestParam(required = false) List<Long> subCategoryIds,
    //         @RequestParam(required = false) Long brandId,
    //         @RequestParam(required = false) Long collectionId,
    //         @RequestParam(required = false) Double minPrice,
    //         @RequestParam(required = false) Double maxPrice,
    //         @RequestParam(required = false) String searchTerm,
    //         @RequestParam(defaultValue = "false") Boolean inStockOnly,
    //         @RequestParam(defaultValue = "created_date") String sortBy,
    //         @RequestParam(defaultValue = "0") Long page,
    //         @RequestParam(defaultValue = "20") Integer perPage,
    //         Locale locale
    // ) {
    //     return productService.getProducts(
    //             categoryId, subCategoryIds, brandId, collectionId, minPrice, maxPrice,
    //             searchTerm, inStockOnly, sortBy, page, perPage, locale.getLanguage()
    //     );
    // }

    
    @GetMapping("/list")
    @Operation(summary = "Get products for web application with filters and pagination")
    public ResponseEntity<ProductsListResponseDTO> getWebProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = true) Integer collectionId,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> subCategoryIds,
            @RequestParam(required = false) List<Long> brandIds,
            @RequestParam(required = false) List<Long> colorIds,
            @RequestParam(required = false) List<Long> sizeIds,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isNew,
            @RequestParam(required = false) Boolean isTrend,
            @RequestParam(required = false) Boolean isTopSeller,
            @RequestParam(defaultValue = "newest") String sortBy) {
        
        ProductsListFilterRequestDTO filterRequest = ProductsListFilterRequestDTO.builder()
                .page(page)
                .limit(limit)
                .collectionId(collectionId)
                .categoryIds(categoryIds)
                .subCategoryIds(subCategoryIds)
                .brandIds(brandIds)
                .colorIds(colorIds)
                .sizeIds(sizeIds)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .search(search)
                .isNew(isNew)
                .isTrend(isTrend)
                .isTopSeller(isTopSeller)
                .sortBy(sortBy)
                .build();
        
        ProductsListResponseDTO response = productService.getProductsListWithFilter(filterRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/attributes")
    @Operation(summary = "Get products attributes like colors and sizes", 
               description = "Fetches products attributes")
    public ResponseEntity<ProductsListVariantsDTO> getProductsAttributes(){
        ProductsListVariantsDTO productsListVariantsDTO= this.productService.getProductsVariants();
        return ResponseEntity.ok(productsListVariantsDTO);
    }

    // @GetMapping("/{productId}")
    // public ResponseEntity<ProductProfileResponseDTO> getProductDetails(@PathVariable Long productId, 
    //                                                                    @RequestParam(defaultValue = "5") int limitReviews,
    //                                                                    @RequestParam(defaultValue = "0") int offsetReviews) {
    //     ProductProfileResponseDTO response = productService.getProductDetails(productId, limitReviews, offsetReviews);
    //     return ResponseEntity.ok(response);
    // }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailsResponseDTO> getProductDetails(@PathVariable Long productId,
        @RequestParam(defaultValue = "5") int limitReviews, @RequestParam(defaultValue = "0") int offsetReviews) {
        ProductDetailsResponseDTO productDetails = productService.getProductDashboardDetails(productId);
        return ResponseEntity.ok(productDetails);
    }


    @GetMapping("/top-sellers")
    @Operation(summary = "Get top seller products", 
               description = "Fetches products marked as top sellers")
    public ResponseEntity<List<TopSellersProductResponseDTO>> getTopSellersProducts(){
        List<TopSellersProductResponseDTO> topSellers= this.productService.getTopSellerProducts();
        return ResponseEntity.ok(topSellers);
    }

    @GetMapping("/trends")
    @Operation(summary = "Get trending products", 
               description = "Fetches products marked as is trend")
    public ResponseEntity<List<TrendingProductsResponseDTO>> getTrendingProducts(){
        List<TrendingProductsResponseDTO> trending= this.productService.getTrendingProducts();
        return ResponseEntity.ok(trending);
    }

    

    @PostMapping("/{productId}/mark-top-seller")
    public ResponseEntity<Void> markAsTopSeller(@PathVariable Long productId) {
        productService.markProductAsTopSeller(productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}/unmark-top-seller")
    public ResponseEntity<Void> unmarkAsTopSeller(@PathVariable Long productId) {
        productService.unmarkProductAsTopSeller(productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/list")
    // @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get basic products list for dashboard")
    public ResponseEntity<ProductListResponseDTO> getBasicProducts(
            @RequestParam(required = false, defaultValue = "0") int categoryId,
            @RequestParam(required = false, defaultValue = "0") int subCategoryId,
            @RequestParam(required = false, defaultValue = "0") int brandId,
            @RequestParam(required = false, defaultValue = "0") int collectionId,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        
        ProductListResponseDTO response = productService.getProductsList(page, size, categoryId, subCategoryId, brandId, collectionId, search);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/admin/filters")
    // @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all filter data for products")
    public ResponseEntity<ProductFilterDataResponseDTO> getProductFilters(
        @RequestParam(required = false) Long collectionId,
        @RequestParam(required = false) Long categoryId) {
        ProductFilterDataResponseDTO filterData = productService.getProductFilterData(collectionId, categoryId);
        return ResponseEntity.ok(filterData);
    }

    @GetMapping("/admin/{productId}/details")
    // @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get complete product details by ID")
    public ResponseEntity<ProductDetailsResponseDTO> getProductProfileDetails(@PathVariable Long productId) {
        ProductDetailsResponseDTO productDetails = productService.getProductDashboardDetailsForDashboard(productId);
        return ResponseEntity.ok(productDetails);
    }

    @PutMapping("/admin/{productId}/status")
    // @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle product status")
    public ResponseEntity<Void> toggleActivation(@PathVariable Long productId, @Valid @RequestBody StatusRequestDTO request) {
        productService.toggleStatus(productId, request.getStatusName());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/admin/{id}/basic-info")
    @Operation(summary = "Update product basic information")
    public ResponseEntity<Void> updateProductBasicInfo(
            @PathVariable Long id, 
            @Valid @RequestBody ProductUpdateRequestDTO request) {
        productService.updateProductBasicInfo(id, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/admin/{id}/prices")
    @Operation(summary = "Update product prices information")
    public ResponseEntity<Void> updateProductPrices(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateVariantPriceRequestDTO request) {
        // ProductDTO product = productService.updateProductBasicInfo(id, request);
        productService.updateProductPrices(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/sub-categories")
    // @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get complete subCategories by category ID")
    public ResponseEntity<List<SubCategoryDTO>> getSubCategoriesOfCategory(
        @RequestParam(required = false, defaultValue = "0") Long categoryId
        ) {
        List<SubCategoryDTO> subCategories = productService.getAllActiveSubCategories(categoryId);
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/admin/categories")
    // @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get complete categories by collection ID")
    public ResponseEntity<List<CategoryDTO>> getCategoriesOfCollection(
        @RequestParam(required = false, defaultValue = "0") Long collectionId
        ) {
        List<CategoryDTO> subCategories = productService.getAllActiveCategories(collectionId);
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/admin/colors")
    // @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all active colors")
    public ResponseEntity<List<Color>> getActiveColors() {
        return ResponseEntity.ok(colorRepository.findAll());
    }

    @GetMapping("/admin/sizes")
    // @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all active sizes")
    public ResponseEntity<List<Size>> getActiveSizes() {
        return ResponseEntity.ok(sizeRepository.findAll());
    }

    @PostMapping(value = "/admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@ModelAttribute @Valid ProductCreateRequestDTO productCreateRequst){
        try{
            if (!productCreateRequst.isValidPrimaryIndex()) {
                throw new BadRequestException("Invalid primary image index");
            }
            Long productId= this.productService.create(productCreateRequst);
            return ResponseEntity.ok().body(productId);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("can not create new product");
        }
    }





    // @PostMapping(value = "/admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // public ResponseEntity<?> create(
    //     @RequestPart("images") List<MultipartFile> images,
    //     @RequestParam("primaryImageIndex") Integer primaryImageIndex,
    //     @RequestParam("nameEn") String nameEn,
    //     @RequestParam("nameAr") String nameAr,
    //     @RequestParam("descriptionEn") String descriptionEn,
    //     @RequestParam("descriptionAr") String descriptionAr,
    //     @RequestParam("brandId") Long brandId,
    //     @RequestParam("collectionId") Long collectionId,
    //     @RequestParam("categoryId") Long categoryId,
    //     @RequestParam("subCategoryId") Long subCategoryId) {
        
    //     try {
    //         ProductCreateRequestDTO requestDTO = ProductCreateRequestDTO.builder()
    //             .images(images)
    //             .primaryImageIndex(primaryImageIndex)
    //             .nameEn(nameEn)
    //             .nameAr(nameAr)
    //             .descriptionEn(descriptionEn)
    //             .descriptionAr(descriptionAr)
    //             .brandId(brandId)
    //             .collectionId(collectionId)
    //             .categoryId(categoryId)
    //             .subCategoryId(subCategoryId)
    //             .build();

    //         if (!requestDTO.isValidPrimaryIndex()) {
    //             return ResponseEntity.badRequest().body("Invalid primary image index");
    //         }

    //         Long productId = this.productService.create(requestDTO);
    //         return ResponseEntity.ok(productId);
    //     } catch(Exception e) {
    //         e.printStackTrace(); // للتصحيح
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //             .body("Cannot create new product: " + e.getMessage());
    //     }
    // }
}
