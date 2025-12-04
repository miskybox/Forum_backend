package com.forumviajeros.backend.service.visitedplace;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.forumviajeros.backend.dto.visitedplace.TravelStatsDTO;
import com.forumviajeros.backend.dto.visitedplace.VisitedPlaceRequestDTO;
import com.forumviajeros.backend.dto.visitedplace.VisitedPlaceResponseDTO;
import com.forumviajeros.backend.model.VisitedPlace.PlaceStatus;

/**
 * Servicio para gestión de lugares visitados
 */
public interface VisitedPlaceService {

    /**
     * Agrega un nuevo lugar visitado
     */
    VisitedPlaceResponseDTO addVisitedPlace(Long userId, VisitedPlaceRequestDTO request);

    /**
     * Actualiza un lugar visitado
     */
    VisitedPlaceResponseDTO updateVisitedPlace(Long userId, Long placeId, VisitedPlaceRequestDTO request);

    /**
     * Elimina un lugar visitado
     */
    void deleteVisitedPlace(Long userId, Long placeId);

    /**
     * Obtiene un lugar por ID
     */
    VisitedPlaceResponseDTO getVisitedPlaceById(Long placeId);

    /**
     * Obtiene todos los lugares de un usuario
     */
    List<VisitedPlaceResponseDTO> getUserPlaces(Long userId);

    /**
     * Obtiene lugares de un usuario con paginación
     */
    Page<VisitedPlaceResponseDTO> getUserPlacesPaginated(Long userId, Pageable pageable);

    /**
     * Obtiene lugares por estado (VISITED, WISHLIST, LIVED)
     */
    List<VisitedPlaceResponseDTO> getUserPlacesByStatus(Long userId, PlaceStatus status);

    /**
     * Obtiene lugares favoritos de un usuario
     */
    List<VisitedPlaceResponseDTO> getUserFavoritePlaces(Long userId);

    /**
     * Marca/desmarca un lugar como favorito
     */
    VisitedPlaceResponseDTO toggleFavorite(Long userId, Long placeId);

    /**
     * Obtiene estadísticas de viaje de un usuario
     */
    TravelStatsDTO getUserTravelStats(Long userId);

    /**
     * Verifica si un usuario ha visitado un país
     */
    boolean hasUserVisitedCountry(Long userId, Long countryId);

    /**
     * Obtiene el ranking de viajeros
     */
    List<TravelStatsDTO> getTravelersRanking(int limit);

    /**
     * Obtiene la posición de un usuario en el ranking
     */
    Integer getUserRanking(Long userId);
}

