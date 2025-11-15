package com.web.service.addmix_store.controllers;

import com.web.service.addmix_store.dtos.CollectionDTO;
import com.web.service.addmix_store.dtos.dashboard.CreateCollectionRequest;
import com.web.service.addmix_store.dtos.dashboard.UpdateCollectionRequest;
import com.web.service.addmix_store.models.Collection;
import com.web.service.addmix_store.services.CollectionService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Locale;

@RestController
@AllArgsConstructor
@RequestMapping("/api/collections")
// @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@CrossOrigin(
    origins = {
        "http://localhost:3000",
        "http://addmix-dashboard.s3-website-us-east-1.amazonaws.com",
        "http://addmix-wep-app.s3-website-us-east-1.amazonaws.com"
    },
    allowCredentials = "true"
)
public class CollectionController {

    private final CollectionService collectionService;

    @GetMapping
    public ResponseEntity<List<CollectionDTO>> getAllSystemCollectionsData(Locale locale) {
        String lang= locale.getLanguage();
        List<CollectionDTO> collections= collectionService.getCollectionsWithCategoriesAndBrands(lang);
        return ResponseEntity.ok(collections);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<com.web.service.addmix_store.dtos.dashboard.CollectionDTO>> getAllCollections() {
        return ResponseEntity.ok(collectionService.getCollections());
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteCollection(@PathVariable Long id){
        ResponseEntity<?> res= collectionService.deleteCollectionById(id);
        return res;
    }

    @PostMapping("/admin")
    public ResponseEntity<Collection> addCollection(@Valid @ModelAttribute CreateCollectionRequest request) {
        Collection createdCollection = collectionService.addCollection(request);
        return ResponseEntity.ok().body(createdCollection);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<Void> updateCollection(@ModelAttribute UpdateCollectionRequest request, @PathVariable Long id) {
        collectionService.updateCollection(id, request);
        return ResponseEntity.noContent().build();
    }

}
