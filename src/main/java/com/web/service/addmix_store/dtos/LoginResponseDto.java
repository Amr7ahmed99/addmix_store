package com.web.service.addmix_store.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponseDto {
    private String token;
    private String type = "Bearer";
    private UserResponseDto user;

    public LoginResponseDto(String token, UserResponseDto user) {
        this.token = token;
        this.user = user;
    }
}
