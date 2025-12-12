package com.web.service.addmix_store.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAddressDto {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String phoneNumber;
    private Boolean isDefault;
}
