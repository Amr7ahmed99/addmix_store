package com.web.service.addmix_store.services.contracts;

import java.util.List;
import com.web.service.addmix_store.dtos.dashboard.BrandDTO;
import com.web.service.addmix_store.dtos.dashboard.CategoryDTO;
import com.web.service.addmix_store.dtos.dashboard.CollectionDTO;
import com.web.service.addmix_store.dtos.ProductCreateRequestDTO;
import com.web.service.addmix_store.dtos.ProductsListFilterRequestDTO;
import com.web.service.addmix_store.dtos.ProductsListVariantsDTO;
import com.web.service.addmix_store.dtos.dashboard.ProductDetailsResponseDTO;
import com.web.service.addmix_store.dtos.dashboard.ProductFilterDataResponseDTO;
import com.web.service.addmix_store.dtos.dashboard.ProductListResponseDTO;
import com.web.service.addmix_store.dtos.dashboard.ProductUpdateRequestDTO;
import com.web.service.addmix_store.dtos.dashboard.SubCategoryDTO;
import com.web.service.addmix_store.dtos.dashboard.UpdateVariantPriceRequestDTO;
import com.web.service.addmix_store.dtos.response.ProductsListResponseDTO;
import com.web.service.addmix_store.dtos.response.TopSellersProductResponseDTO;
import com.web.service.addmix_store.dtos.response.TrendingProductsResponseDTO;

public interface ProductService {
    List<TopSellersProductResponseDTO> getTopSellerProducts();
    List<TrendingProductsResponseDTO> getTrendingProducts();
    void markProductAsTopSeller(Long productId);
    void unmarkProductAsTopSeller(Long productId);

    public Long countByCategoryId(Long categoryId);
    public Long countBySubCategoryId(Long subCategoryId);
    public Long countByBrandId(Long brandId);

    Long create(ProductCreateRequestDTO request);
    ProductListResponseDTO getProductsList(int page, int size, int categoryId, int subCategoryId, int brandId, int collectionId, String searchTxt);

    ProductFilterDataResponseDTO getProductFilterData(Long collectionId, Long categoryId);
    ProductsListResponseDTO getProductsListWithFilter(ProductsListFilterRequestDTO filterRequest);

    public ProductDetailsResponseDTO getProductDashboardDetails(Long productId);
    public ProductDetailsResponseDTO getProductDashboardDetailsForDashboard(Long productId);
    
    public ProductsListVariantsDTO getProductsVariants();

    void updateProductBasicInfo(Long productId, ProductUpdateRequestDTO request);
    void updateProductPrices(Long id, UpdateVariantPriceRequestDTO request);
    // void toggleProductActive(Long productId);
    // void toggleTopSeller(Long productId);
    void deleteProduct(Long productId);
    void toggleStatus(Long productId, String statusName);
    public List<CollectionDTO> getAllActiveCollections();
    public List<CategoryDTO> getAllActiveCategories(Long collectionId);
    public List<SubCategoryDTO> getAllActiveSubCategories(Long categoryId);
    public List<BrandDTO> getAllActiveBrands();

    
    // // Variant Management
    // ProductVariantDTO addVariant(Long productId, ProductVariantCreateRequestDTO request);
    // ProductVariantDTO updateVariant(Long variantId, ProductVariantUpdateRequestDTO request);
    // void deleteVariant(Long variantId);
    // void toggleVariantActive(Long variantId);
    
    // // Price Management
    // void updateVariantPrice(Long variantId, Double price, Double discountPrice);
    // void applyDiscountToProduct(Long productId, Double discountPercentage);
    
    // // Image Management
    // void updateProductImages(Long productId, List<ProductImageUpdateRequestDTO> images);
    // void setPrimaryImage(Long productId, Long imageId);

    //default implementation for common logic
    default Double calculateSavings(Double price, Double discountPrice) {
        if (discountPrice != null && price != null && price > discountPrice) {
            return price - discountPrice;
        }
        return 0.0;
    }
}
