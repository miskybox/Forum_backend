package com.forumviajeros.backend.dto.trivia;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el ranking/leaderboard de trivia
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriviaLeaderboardDTO {
    
    /** Tipo de ranking */
    private String leaderboardType;
    
    /** Período del ranking (all-time, monthly, weekly, daily) */
    private String period;
    
    /** Lista de jugadores en el ranking */
    private List<LeaderboardEntryDTO> entries;
    
    /** Posición del usuario actual (si está autenticado) */
    private LeaderboardEntryDTO currentUserEntry;
    
    /** Total de jugadores en el ranking */
    private Integer totalPlayers;
    
    /**
     * Entrada individual en el ranking
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaderboardEntryDTO {
        
        /** Posición en el ranking */
        private Integer rank;
        
        /** ID del usuario */
        private Long userId;
        
        /** Nombre del usuario */
        private String username;
        
        /** Avatar */
        private String profileImageUrl;
        
        /** Puntuación */
        private Long score;
        
        /** Nivel */
        private Integer level;
        
        /** Título del jugador */
        private String playerTitle;
        
        /** Porcentaje de aciertos */
        private Double accuracyPercentage;
        
        /** Total de partidas */
        private Integer totalGames;
        
        /** Partidas perfectas */
        private Integer perfectGames;
        
        /** Mejor racha */
        private Integer bestStreak;
        
        /** País del usuario (para banderas) */
        private String userCountry;
    }
}

