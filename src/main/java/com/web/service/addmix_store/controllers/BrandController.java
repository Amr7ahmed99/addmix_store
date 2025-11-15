package com.web.service.addmix_store.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.service.addmix_store.dtos.dashboard.BrandDTO;
import com.web.service.addmix_store.dtos.dashboard.CreateBrandRequest;
import com.web.service.addmix_store.dtos.dashboard.UpdateBrandRequest;
import com.web.service.addmix_store.dtos.dashboard.UpdateCategoryRequest;
import com.web.service.addmix_store.services.BrandService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/brands")
// @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@CrossOrigin(
    origins = {
        "http://localhost:3000",
        "http://addmix-dashboard.s3-website-us-east-1.amazonaws.com",
        "http://addmix-wep-app.s3-website-us-east-1.amazonaws.com"
    },
    allowCredentials = "true"
)
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping("/admin")
    public ResponseEntity<List<BrandDTO>> getAll() {
        List<BrandDTO> brands= brandService.getAll();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> getById(@PathVariable Long id) throws Exception{
        BrandDTO brand= brandService.getById(id)
            .orElseThrow(()-> new Exception("Brand not found"));

        return ResponseEntity.ok(brand);
    }

    @PostMapping("/admin")
    public ResponseEntity<Void> create(@Valid @ModelAttribute @RequestBody CreateBrandRequest request) {
        brandService.addBrand(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<Void> updateBrand(@Valid @ModelAttribute UpdateBrandRequest request, @PathVariable Long id) {
        brandService.updateBrand(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id){
        brandService.deleteBrandById(id);
        return ResponseEntity.noContent().build();
    }
}