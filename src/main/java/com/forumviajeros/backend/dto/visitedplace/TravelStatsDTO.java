package com.forumviajeros.backend.dto.visitedplace;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con estadísticas de viaje de un usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelStatsDTO {
    
    /** ID del usuario */
    private Long userId;
    
    /** Nombre del usuario */
    private String username;
    
    /** Total de países visitados */
    private Integer countriesVisited;
    
    /** Total de países en la lista de deseos */
    private Integer countriesWishlist;
    
    /** Total de países donde ha vivido */
    private Integer countriesLived;
    
    /** Total de ciudades visitadas */
    private Integer citiesVisited;
    
    /** Porcentaje del mundo visitado (por área) */
    private Double worldPercentageByArea;
    
    /** Porcentaje del mundo visitado (por número de países) */
    private Double worldPercentageByCountries;
    
    /** Kilómetros cuadrados totales visitados */
    private Double totalAreaVisitedSqKm;
    
    /** Número de continentes visitados */
    private Integer continentsVisited;
    
    /** Lista de continentes visitados */
    private List<String> continentsList;
    
    /** Desglose por continente: continente -> número de países */
    private Map<String, Integer> countriesByContinent;
    
    /** País más visitado (por número de visitas) */
    private String mostVisitedCountry;
    
    /** Lugar favorito */
    private String favoritePlace;
    
    /** Nivel de viajero */
    private String travelerLevel;
    
    /** Badges/logros desbloqueados */
    private List<String> badges;
    
    /** Ranking global del usuario */
    private Integer globalRanking;
    
    /** Total de usuarios en el ranking */
    private Integer totalUsersInRanking;

    /**
     * Calcula el nivel de viajero basado en países visitados
     */
    public static String calculateTravelerLevel(int countriesVisited) {
        if (countriesVisited >= 100) return "🌟 Leyenda Viajera";
        if (countriesVisited >= 75) return "🏆 Maestro Explorador";
        if (countriesVisited >= 50) return "🎖️ Trotamundos";
        if (countriesVisited >= 30) return "🌍 Viajero Experto";
        if (countriesVisited >= 20) return "✈️ Aventurero";
        if (countriesVisited >= 10) return "🎒 Explorador";
        if (countriesVisited >= 5) return "🗺️ Turista";
        if (countriesVisited >= 1) return "👣 Principiante";
        return "🏠 Soñador";
    }
}

