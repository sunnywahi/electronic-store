package com.sample.electronicstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketItemDTO {
    private Long id;
    private Long basketId;
    private Long productId;
    private int quantity;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) // This field is only for reading, not for writing
    private long lastUpdated;
}
