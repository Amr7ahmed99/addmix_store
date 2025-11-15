package com.web.service.addmix_store.controllers;

import com.web.service.addmix_store.dtos.dashboard.CategoryDTO;
import com.web.service.addmix_store.dtos.dashboard.CreateCategoryRequest;
import com.web.service.addmix_store.dtos.dashboard.UpdateCategoryRequest;
import com.web.service.addmix_store.models.Category;
import com.web.service.addmix_store.services.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/categories")
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
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/admin")
    public ResponseEntity<List<CategoryDTO>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());

    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getById(@PathVariable Long id) throws Exception{
        CategoryDTO category= categoryService.getById(id)
            .orElseThrow(()-> new Exception("Category not found"));

        return ResponseEntity.ok(category);
    }

    @PostMapping("/admin")
    public ResponseEntity<Category> create(@Valid @ModelAttribute @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.addCategory(request));
    }

    // @PutMapping("/{id}")
    // public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @RequestBody CategoryDTO updated) {
    //     updated.setId(id);
    //     return ResponseEntity.ok(categoryService.save(updated));
    // }

    // @DeleteMapping("/{id}")
    // public void delete(@PathVariable Long id) {
    //     categoryService.delete(id);
    // }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }

    // @PostMapping("/admin")
    // public ResponseEntity<Collection> addCategory(@Valid @ModelAttribute CreateCollectionRequest request) {
    //     Category createdCategory = categoryService.addCategory(request);
    //     return ResponseEntity.ok().body(createdCategory);
    // }

    @PutMapping("/admin/{id}")
    public ResponseEntity<Void> updateCategory(@Valid @ModelAttribute UpdateCategoryRequest request, @PathVariable Long id) {
        categoryService.updateCategory(id, request);
        return ResponseEntity.noContent().build();
    }
}
