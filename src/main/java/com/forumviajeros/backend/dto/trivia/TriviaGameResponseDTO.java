package com.forumviajeros.backend.dto.trivia;

import java.time.LocalDateTime;

import com.forumviajeros.backend.model.TriviaGame.GameMode;
import com.forumviajeros.backend.model.TriviaGame.GameStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con información de una partida de trivia
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriviaGameResponseDTO {
    
    private Long id;
    
    /** ID del jugador */
    private Long userId;
    
    /** Nombre del jugador */
    private String username;
    
    /** ID del oponente (en duelos) */
    private Long opponentId;
    
    /** Nombre del oponente */
    private String opponentUsername;
    
    /** Modo de juego */
    private GameMode gameMode;
    
    /** Estado de la partida */
    private GameStatus status;
    
    /** Puntuación actual */
    private Integer score;
    
    /** Puntuación del oponente */
    private Integer opponentScore;
    
    /** Total de preguntas */
    private Integer totalQuestions;
    
    /** Respuestas correctas */
    private Integer correctAnswers;
    
    /** Pregunta actual (índice) */
    private Integer currentQuestionIndex;
    
    /** Tiempo total en segundos */
    private Integer totalTimeSeconds;
    
    /** Dificultad */
    private Integer difficulty;
    
    /** Porcentaje de aciertos */
    private Double accuracyPercentage;
    
    /** Si fue partida perfecta */
    private Boolean perfectGame;
    
    /** Primera pregunta (para empezar a jugar) */
    private TriviaQuestionDTO firstQuestion;
    
    /** Fecha de inicio */
    private LocalDateTime startedAt;
    
    /** Fecha de fin */
    private LocalDateTime finishedAt;
}

