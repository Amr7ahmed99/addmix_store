package com.web.service.addmix_store.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.web.service.addmix_store.dtos.CreateVariantRequestDTO;
import com.web.service.addmix_store.dtos.response.VariantResponseDTO;
import com.web.service.addmix_store.services.ProductVariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/variants")
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
@Tag(name = "Product variants", description = "APIs for product variants management")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    @PostMapping
    @Operation(summary = "Create new product variant")
    public ResponseEntity<VariantResponseDTO> createVariant(
            @Valid @RequestBody CreateVariantRequestDTO request) {
        VariantResponseDTO variant = productVariantService.createVariant(request);
        return ResponseEntity.ok(variant);
    }
}
