package com.forumviajeros.backend.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que almacena las estadísticas globales de trivia de un usuario.
 * Mantiene un registro acumulativo del rendimiento del usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trivia_scores")
@EntityListeners(AuditingEntityListener.class)
public class TriviaScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario al que pertenecen estas estadísticas
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Puntuación total acumulada
     */
    @Column(name = "total_score")
    @Builder.Default
    private Long totalScore = 0L;

    /**
     * Número total de partidas jugadas
     */
    @Column(name = "total_games")
    @Builder.Default
    private Integer totalGames = 0;

    /**
     * Número total de preguntas respondidas
     */
    @Column(name = "total_questions")
    @Builder.Default
    private Integer totalQuestions = 0;

    /**
     * Número de respuestas correctas
     */
    @Column(name = "correct_answers")
    @Builder.Default
    private Integer correctAnswers = 0;

    /**
     * Racha actual de respuestas correctas consecutivas
     */
    @Column(name = "current_streak")
    @Builder.Default
    private Integer currentStreak = 0;

    /**
     * Mejor racha histórica
     */
    @Column(name = "best_streak")
    @Builder.Default
    private Integer bestStreak = 0;

    /**
     * Nivel del jugador (calculado según puntuación)
     */
    @Builder.Default
    private Integer level = 1;

    /**
     * Experiencia actual hacia el siguiente nivel
     */
    @Column(name = "experience_points")
    @Builder.Default
    private Integer experiencePoints = 0;

    /**
     * Número de partidas ganadas (en modo desafío o duelo)
     */
    @Column(name = "games_won")
    @Builder.Default
    private Integer gamesWon = 0;

    /**
     * Número de partidas perfectas (todas las respuestas correctas)
     */
    @Column(name = "perfect_games")
    @Builder.Default
    private Integer perfectGames = 0;

    /**
     * Tiempo promedio de respuesta en segundos
     */
    @Column(name = "avg_response_time")
    @Builder.Default
    private Double avgResponseTime = 0.0;

    /**
     * Mejor tiempo de respuesta registrado
     */
    @Column(name = "best_response_time")
    private Double bestResponseTime;

    /**
     * Fecha de la última partida
     */
    @Column(name = "last_played")
    private LocalDateTime lastPlayed;

    /**
     * Días consecutivos jugando (para badges)
     */
    @Column(name = "daily_streak")
    @Builder.Default
    private Integer dailyStreak = 0;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Calcula el porcentaje de aciertos
     */
    public Double getAccuracyPercentage() {
        if (totalQuestions == 0) return 0.0;
        return (correctAnswers * 100.0) / totalQuestions;
    }

    /**
     * Calcula el nivel basado en la experiencia
     */
    public void calculateLevel() {
        // Cada nivel requiere 100 * nivel puntos de experiencia
        int exp = experiencePoints;
        int lvl = 1;
        int required = 100;
        
        while (exp >= required) {
            exp -= required;
            lvl++;
            required = 100 * lvl;
        }
        
        this.level = lvl;
    }
}

