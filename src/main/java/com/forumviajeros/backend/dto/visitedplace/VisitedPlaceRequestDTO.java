package com.forumviajeros.backend.dto.visitedplace;

import java.time.LocalDate;

import com.forumviajeros.backend.model.VisitedPlace.PlaceStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear o actualizar un lugar visitado
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitedPlaceRequestDTO {

    @NotNull(message = "El ID del país es obligatorio")
    private Long countryId;

    @Size(max = 100, message = "El nombre de la ciudad no puede exceder 100 caracteres")
    private String cityName;

    @NotNull(message = "El estado es obligatorio")
    private PlaceStatus status;

    private LocalDate visitDate;
    
    private LocalDate visitEndDate;

    @Size(max = 1000, message = "Las notas no pueden exceder 1000 caracteres")
    private String notes;

    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    private Integer rating;

    private Boolean favorite;

    @Min(value = 1, message = "El número de visitas mínimo es 1")
    private Integer visitCount;
}

