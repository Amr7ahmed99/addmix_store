package com.web.service.addmix_store.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web.service.addmix_store.dtos.AddAddressRequest;
import com.web.service.addmix_store.dtos.UserAddressDto;
import com.web.service.addmix_store.services.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/addresses")
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
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<UserAddressDto>> getUserAddresses(
        @RequestParam(required = true) Long userId
    ) {
        List<UserAddressDto> addresses= addressService.getUserAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
        @PathVariable Long addressId
    ) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> addAddress(@Valid @RequestBody AddAddressRequest request) {
        addressService.addAddress(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{addressId}/set-default")
    public ResponseEntity<Void> setAddressAsDefault(@PathVariable Long addressId) {
        addressService.setAddressAsDefault(addressId);
        return ResponseEntity.ok().build();
    }

    
}