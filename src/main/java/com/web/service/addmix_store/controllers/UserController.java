package com.web.service.addmix_store.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.service.addmix_store.repository.UserRepository;
import com.web.service.addmix_store.models.User;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserRepository userRepository;
    
    public UserController(UserRepository userRepository){
        this.userRepository= userRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}

