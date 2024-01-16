package com.sample.electronicstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDTO {
    private Long id;
    private Long basketId;
    private Set<Long> discountDealIds;
    private String details;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) // This field is only for reading, not for writing
    private long lastUpdated;
}
