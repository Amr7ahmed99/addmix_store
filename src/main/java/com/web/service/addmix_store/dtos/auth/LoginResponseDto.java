package com.web.service.addmix_store.dtos.auth;

import com.web.service.addmix_store.dtos.UserResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private static final String type = "Bearer";
    private UserResponseDto user;
}
