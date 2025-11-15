package com.web.service.addmix_store.dtos.dashboard;

import java.time.LocalDate;
import java.util.List;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVariantPriceRequestDTO {

    @NotEmpty
    private List<Long> variantIds;

    @NotNull
    @Min(1)
    private Double price;

    @Nullable
    @Min(0)
    private Double discountPrice;

    @Nullable
    private LocalDate priceStartDate;

    @Nullable
    private LocalDate priceEndDate;
}

