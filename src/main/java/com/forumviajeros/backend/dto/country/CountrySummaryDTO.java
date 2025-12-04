package com.forumviajeros.backend.dto.country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con información resumida de un país (para listas)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountrySummaryDTO {
    private Long id;
    private String isoCode;
    private String name;
    private String capital;
    private String continent;
    private String flagUrl;
    private String flagEmoji;
}

