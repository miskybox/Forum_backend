package com.forumviajeros.backend.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una respuesta individual en una partida de trivia.
 * Registra cada pregunta respondida con su resultado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trivia_answers")
@EntityListeners(AuditingEntityListener.class)
public class TriviaAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Partida a la que pertenece esta respuesta
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private TriviaGame game;

    /**
     * Pregunta que fue respondida
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private TriviaQuestion question;

    /**
     * Respuesta seleccionada por el usuario
     */
    @Column(name = "selected_answer")
    private String selectedAnswer;

    /**
     * Indica si la respuesta fue correcta
     */
    @Column(name = "is_correct", nullable = false)
    @Builder.Default
    private Boolean isCorrect = false;

    /**
     * Tiempo que tardó en responder (en milisegundos)
     */
    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    /**
     * Puntos obtenidos por esta respuesta
     */
    @Column(name = "points_earned")
    @Builder.Default
    private Integer pointsEarned = 0;

    /**
     * Índice de la pregunta en la partida (1, 2, 3...)
     */
    @Column(name = "question_index")
    private Integer questionIndex;

    /**
     * Si el usuario usó alguna ayuda/pista
     */
    @Column(name = "hint_used")
    @Builder.Default
    private Boolean hintUsed = false;

    /**
     * Si el tiempo se agotó antes de responder
     */
    @Column(name = "timed_out")
    @Builder.Default
    private Boolean timedOut = false;

    @CreatedDate
    @Column(name = "answered_at", nullable = false, updatable = false)
    private LocalDateTime answeredAt;

    /**
     * Calcula los puntos basados en tiempo y correctitud
     */
    public void calculatePoints(int basePoints, int timeLimitMs) {
        if (!isCorrect || timedOut) {
            this.pointsEarned = 0;
            return;
        }

        // Bonus por velocidad: hasta 50% extra si responde en menos de la mitad del tiempo
        double timeRatio = (double) responseTimeMs / timeLimitMs;
        double speedBonus = Math.max(0, 1 - timeRatio) * 0.5;
        
        // Penalización por usar pista: -25%
        double hintPenalty = hintUsed ? 0.25 : 0;
        
        this.pointsEarned = (int) (basePoints * (1 + speedBonus - hintPenalty));
    }
}

