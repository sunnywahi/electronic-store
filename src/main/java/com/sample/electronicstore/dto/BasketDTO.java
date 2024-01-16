package com.sample.electronicstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketDTO {
    private Long id;
    private Long customerId;
    private List<BasketItemDTO> items;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) // This field is only for reading, not for writing
    private long lastUpdated;
}
