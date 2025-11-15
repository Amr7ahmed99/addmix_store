package com.web.service.addmix_store.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyRegisterRequestDto {
    @NotBlank(message = "Email or mobile is required")
    @Pattern(
        regexp = "^([0-9]{10,15}|[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$",
        message = "Please provide a valid email or mobile number"
    )
    private String emailOrMobile;

    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Please provide a valid verification code")
    private String verificationCode;
}
