package com.forumviajeros.backend.dto.visitedplace;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.forumviajeros.backend.dto.country.CountrySummaryDTO;
import com.forumviajeros.backend.model.VisitedPlace.PlaceStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta con información de un lugar visitado
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitedPlaceResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private CountrySummaryDTO country;
    private String cityName;
    private PlaceStatus status;
    private LocalDate visitDate;
    private LocalDate visitEndDate;
    private String notes;
    private Integer rating;
    private Boolean favorite;
    private Integer visitCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

