package com.forumviajeros.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una partida de trivia.
 * Registra cada sesión de juego con sus respuestas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trivia_games")
@EntityListeners(AuditingEntityListener.class)
public class TriviaGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario que juega la partida
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Oponente en caso de duelo (opcional)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opponent_id")
    private User opponent;

    /**
     * Modo de juego
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "game_mode", nullable = false)
    private GameMode gameMode;

    /**
     * Estado actual de la partida
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private GameStatus status = GameStatus.IN_PROGRESS;

    /**
     * Puntuación obtenida en esta partida
     */
    @Builder.Default
    private Integer score = 0;

    /**
     * Puntuación del oponente (en duelos)
     */
    @Column(name = "opponent_score")
    @Builder.Default
    private Integer opponentScore = 0;

    /**
     * Número total de preguntas en la partida
     */
    @Column(name = "total_questions")
    @Builder.Default
    private Integer totalQuestions = 10;

    /**
     * Número de respuestas correctas
     */
    @Column(name = "correct_answers")
    @Builder.Default
    private Integer correctAnswers = 0;

    /**
     * Pregunta actual (índice)
     */
    @Column(name = "current_question_index")
    @Builder.Default
    private Integer currentQuestionIndex = 0;

    /**
     * Tiempo total de la partida en segundos
     */
    @Column(name = "total_time_seconds")
    private Integer totalTimeSeconds;

    /**
     * Dificultad de la partida
     */
    @Builder.Default
    private Integer difficulty = 1;

    /**
     * Categoría específica (opcional, ej: "Europa", "Capitales")
     */
    private String category;

    /**
     * Continente específico para filtrar preguntas (opcional)
     */
    private String continent;

    /**
     * Respuestas dadas en esta partida
     */
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TriviaAnswer> answers = new ArrayList<>();

    /**
     * Fecha de inicio de la partida
     */
    @CreatedDate
    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    /**
     * Fecha de finalización de la partida
     */
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    /**
     * Modos de juego disponibles
     */
    public enum GameMode {
        /** Preguntas aleatorias rápidas */
        QUICK,
        /** Serie de preguntas con puntuación */
        CHALLENGE,
        /** Enfrentamiento entre dos usuarios */
        DUEL,
        /** Práctica sin puntuación */
        PRACTICE,
        /** Preguntas diarias */
        DAILY
    }

    /**
     * Estados posibles de una partida
     */
    public enum GameStatus {
        /** Partida en curso */
        IN_PROGRESS,
        /** Partida completada */
        COMPLETED,
        /** Partida abandonada */
        ABANDONED,
        /** Esperando oponente (duelos) */
        WAITING,
        /** Partida pausada */
        PAUSED
    }

    /**
     * Verifica si la partida fue perfecta (todas correctas)
     */
    public boolean isPerfectGame() {
        return correctAnswers.equals(totalQuestions) && status == GameStatus.COMPLETED;
    }

    /**
     * Calcula el porcentaje de aciertos
     */
    public Double getAccuracyPercentage() {
        if (totalQuestions == 0) return 0.0;
        return (correctAnswers * 100.0) / totalQuestions;
    }
}

