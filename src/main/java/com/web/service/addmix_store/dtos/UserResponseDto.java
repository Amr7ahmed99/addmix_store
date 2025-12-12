package com.web.service.addmix_store.dtos;

import com.web.service.addmix_store.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String mobile;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private UserAddressDto userAddressDto;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.isActive= user.getIsActive();
        this.mobile= user.getMobileNumber();
    }

    public UserResponseDto(User user, UserAddressDto userAddressDto) {
        this(user);
        this.userAddressDto = userAddressDto;
    }
}
