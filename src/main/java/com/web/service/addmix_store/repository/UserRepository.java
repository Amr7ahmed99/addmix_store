package com.web.service.addmix_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web.service.addmix_store.models.User;

public interface UserRepository extends JpaRepository<User, Long>{
    
}
