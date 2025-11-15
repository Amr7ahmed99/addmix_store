package com.web.service.addmix_store.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.web.service.addmix_store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.web.service.addmix_store.dtos.UserResponseDto;

@RestController
@RequestMapping("/api/users")
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
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/list")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userRepository.findAll()
                .stream()
                .map(UserResponseDto::new) // map entity -> DTO
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping
    public ResponseEntity<UserResponseDto> getUser(@RequestParam Long id) throws UsernameNotFoundException {
        UserResponseDto user = userRepository.findById(id).map(UserResponseDto::new)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(user);
    }
}