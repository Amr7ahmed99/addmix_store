package com.web.service.addmix_store.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StatusRequestDTO {
    @NotBlank
    private String statusName;
}
