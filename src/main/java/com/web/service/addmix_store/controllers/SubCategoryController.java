package com.web.service.addmix_store.controllers;

import com.web.service.addmix_store.dtos.dashboard.CreateSubCategoryRequest;
import com.web.service.addmix_store.dtos.dashboard.SubCategoryDTO;
import com.web.service.addmix_store.dtos.dashboard.UpdateSubCategoryRequest;
import com.web.service.addmix_store.services.SubCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subcategories")
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
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping("/admin")
    public ResponseEntity<List<SubCategoryDTO>> getAll() {
        List<SubCategoryDTO> subCategories= subCategoryService.getAll();
        return ResponseEntity.ok(subCategories);
    }

    @PostMapping("/admin")
    public ResponseEntity<Void> create(@Valid @RequestBody CreateSubCategoryRequest request) {
        subCategoryService.addSubCategory(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<Void> updateSubCategory(@Valid @RequestBody UpdateSubCategoryRequest request, @PathVariable Long id) {
        subCategoryService.updateSubCategory(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id){
        subCategoryService.deleteSubCategoryById(id);
        return ResponseEntity.noContent().build();
    }


 
}
