package com.forumviajeros.backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.forumviajeros.backend.dto.visitedplace.TravelStatsDTO;
import com.forumviajeros.backend.dto.visitedplace.VisitedPlaceRequestDTO;
import com.forumviajeros.backend.dto.visitedplace.VisitedPlaceResponseDTO;
import com.forumviajeros.backend.model.VisitedPlace.PlaceStatus;
import com.forumviajeros.backend.repository.UserRepository;
import com.forumviajeros.backend.service.visitedplace.VisitedPlaceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para gestión de lugares visitados (Mapa de Viajes)
 */
@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
@Tag(name = "Travel Map", description = "API para gestionar el mapa de viajes del usuario")
public class VisitedPlaceController {

    private final VisitedPlaceService visitedPlaceService;
    private final UserRepository userRepository;

    @PostMapping("/places")
    @Operation(summary = "Agregar un lugar visitado", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<VisitedPlaceResponseDTO> addPlace(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody VisitedPlaceRequestDTO request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(visitedPlaceService.addVisitedPlace(userId, request));
    }

    @PutMapping("/places/{placeId}")
    @Operation(summary = "Actualizar un lugar visitado", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<VisitedPlaceResponseDTO> updatePlace(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long placeId,
            @Valid @RequestBody VisitedPlaceRequestDTO request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(visitedPlaceService.updateVisitedPlace(userId, placeId, request));
    }

    @DeleteMapping("/places/{placeId}")
    @Operation(summary = "Eliminar un lugar visitado", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deletePlace(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long placeId) {
        Long userId = getUserId(userDetails);
        visitedPlaceService.deleteVisitedPlace(userId, placeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/places/{placeId}")
    @Operation(summary = "Obtener un lugar por ID")
    public ResponseEntity<VisitedPlaceResponseDTO> getPlaceById(@PathVariable Long placeId) {
        return ResponseEntity.ok(visitedPlaceService.getVisitedPlaceById(placeId));
    }

    @GetMapping("/my-places")
    @Operation(summary = "Obtener mis lugares visitados", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<VisitedPlaceResponseDTO>> getMyPlaces(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(visitedPlaceService.getUserPlaces(userId));
    }

    @GetMapping("/my-places/paginated")
    @Operation(summary = "Obtener mis lugares con paginación", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<VisitedPlaceResponseDTO>> getMyPlacesPaginated(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Long userId = getUserId(userDetails);
        Sort sort = direction.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(visitedPlaceService.getUserPlacesPaginated(
                userId, PageRequest.of(page, size, sort)));
    }

    @GetMapping("/my-places/status/{status}")
    @Operation(summary = "Obtener lugares por estado", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<VisitedPlaceResponseDTO>> getMyPlacesByStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable PlaceStatus status) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(visitedPlaceService.getUserPlacesByStatus(userId, status));
    }

    @GetMapping("/my-places/favorites")
    @Operation(summary = "Obtener mis lugares favoritos", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<VisitedPlaceResponseDTO>> getMyFavorites(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(visitedPlaceService.getUserFavoritePlaces(userId));
    }

    @PatchMapping("/places/{placeId}/favorite")
    @Operation(summary = "Marcar/desmarcar como favorito", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<VisitedPlaceResponseDTO> toggleFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long placeId) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(visitedPlaceService.toggleFavorite(userId, placeId));
    }

    @GetMapping("/my-stats")
    @Operation(summary = "Obtener mis estadísticas de viaje", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TravelStatsDTO> getMyStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(visitedPlaceService.getUserTravelStats(userId));
    }

    @GetMapping("/users/{userId}/stats")
    @Operation(summary = "Obtener estadísticas de un usuario")
    public ResponseEntity<TravelStatsDTO> getUserStats(@PathVariable Long userId) {
        return ResponseEntity.ok(visitedPlaceService.getUserTravelStats(userId));
    }

    @GetMapping("/users/{userId}/places")
    @Operation(summary = "Obtener lugares de un usuario")
    public ResponseEntity<List<VisitedPlaceResponseDTO>> getUserPlaces(@PathVariable Long userId) {
        return ResponseEntity.ok(visitedPlaceService.getUserPlaces(userId));
    }

    @GetMapping("/ranking")
    @Operation(summary = "Obtener ranking de viajeros")
    public ResponseEntity<List<TravelStatsDTO>> getTravelersRanking(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(visitedPlaceService.getTravelersRanking(limit));
    }

    @GetMapping("/my-ranking")
    @Operation(summary = "Obtener mi posición en el ranking", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Integer> getMyRanking(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(visitedPlaceService.getUserRanking(userId));
    }

    @GetMapping("/check/{countryId}")
    @Operation(summary = "Verificar si he visitado un país", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Boolean> hasVisitedCountry(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long countryId) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(visitedPlaceService.hasUserVisitedCountry(userId, countryId));
    }

    /**
     * Obtiene el ID del usuario desde los detalles de autenticación
     */
    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }
}

