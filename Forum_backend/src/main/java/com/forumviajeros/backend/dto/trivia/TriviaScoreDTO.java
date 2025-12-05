package com.forumviajeros.backend.dto.trivia;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con las estadísticas de trivia de un usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriviaScoreDTO {
    
    private Long id;
    
    /** ID del usuario */
    private Long userId;
    
    /** Nombre del usuario */
    private String username;
    
    /** Avatar del usuario */
    private String profileImageUrl;
    
    /** Puntuación total */
    private Long totalScore;
    
    /** Total de partidas jugadas */
    private Integer totalGames;
    
    /** Total de preguntas respondidas */
    private Integer totalQuestions;
    
    /** Respuestas correctas */
    private Integer correctAnswers;
    
    /** Porcentaje de aciertos */
    private Double accuracyPercentage;
    
    /** Racha actual */
    private Integer currentStreak;
    
    /** Mejor racha */
    private Integer bestStreak;
    
    /** Nivel */
    private Integer level;
    
    /** Experiencia actual */
    private Integer experiencePoints;
    
    /** Experiencia necesaria para el siguiente nivel */
    private Integer experienceToNextLevel;
    
    /** Partidas ganadas */
    private Integer gamesWon;
    
    /** Partidas perfectas */
    private Integer perfectGames;
    
    /** Tiempo promedio de respuesta */
    private Double avgResponseTime;
    
    /** Mejor tiempo de respuesta */
    private Double bestResponseTime;
    
    /** Días consecutivos jugando */
    private Integer dailyStreak;
    
    /** Última partida */
    private LocalDateTime lastPlayed;
    
    /** Posición en el ranking global */
    private Integer globalRank;
    
    /** Título/rango del jugador */
    private String playerTitle;
    
    /**
     * Calcula el título del jugador basado en el nivel
     */
    public static String getPlayerTitle(int level) {
        if (level >= 50) return "🏆 Gran Maestro Geógrafo";
        if (level >= 40) return "🌟 Experto Mundial";
        if (level >= 30) return "🎖️ Sabio de las Naciones";
        if (level >= 20) return "📚 Erudito Geográfico";
        if (level >= 15) return "🗺️ Cartógrafo";
        if (level >= 10) return "🌍 Explorador del Conocimiento";
        if (level >= 5) return "📖 Estudiante Aplicado";
        if (level >= 2) return "🌱 Aprendiz";
        return "👶 Novato";
    }
}

