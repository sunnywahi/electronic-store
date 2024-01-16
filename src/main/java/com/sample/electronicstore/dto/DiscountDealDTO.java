package com.sample.electronicstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDealDTO {
    private Long id;
    private Long productId;
    private String dealDescription;
    private boolean active;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) // This field is only for reading, not for writing
    private long lastUpdated;
}

