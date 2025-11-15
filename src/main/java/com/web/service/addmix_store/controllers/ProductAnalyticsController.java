package com.web.service.addmix_store.controllers;

import java.util.List;
import java.util.Locale;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.web.service.addmix_store.dtos.TopSellerProductsDTO;
import com.web.service.addmix_store.services.ProductAnalyticsService;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/analytics/products")
@RestController
@RequiredArgsConstructor
// @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@CrossOrigin(
    origins = {
        "http://localhost:3000",
        "http://addmix-dashboard.s3-website-us-east-1.amazonaws.com",
        "http://addmix-wep-app.s3-website-us-east-1.amazonaws.com"
    },
    allowCredentials = "true"
)
public class ProductAnalyticsController {

    private final ProductAnalyticsService productAnalyticsService;

    @GetMapping("/top-sellers")
    public ResponseEntity<List<TopSellerProductsDTO>> getTop10Sellers(@RequestParam int limit, Locale locale) throws Exception {
        String lang = locale.getLanguage();
        return ResponseEntity.ok(productAnalyticsService.getTopSellerProducts(limit, lang));
    }

    
}
