package com.forumviajeros.backend.dto.country;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta con información completa de un país
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryResponseDTO {
    private Long id;
    private String isoCode;
    private String isoCode3;
    private String name;
    private String nameEn;
    private String officialName;
    private String capital;
    private String continent;
    private String region;
    private String currencyName;
    private String currencyCode;
    private String currencySymbol;
    private String flagUrl;
    private String flagEmoji;
    private Long population;
    private Double areaSqKm;
    private Double latitude;
    private Double longitude;
    private List<String> languages;
    private List<String> funFacts;
    private String callingCode;
    private String timezone;
}

