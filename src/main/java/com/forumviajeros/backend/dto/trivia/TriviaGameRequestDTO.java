package com.forumviajeros.backend.dto.trivia;

import com.forumviajeros.backend.model.TriviaGame.GameMode;
import com.forumviajeros.backend.model.TriviaQuestion.QuestionType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para iniciar una nueva partida de trivia
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriviaGameRequestDTO {
    
    @NotNull(message = "El modo de juego es obligatorio")
    private GameMode gameMode;
    
    /** Número de preguntas (por defecto 10) */
    @Min(value = 5, message = "Mínimo 5 preguntas")
    @Max(value = 50, message = "Máximo 50 preguntas")
    @Builder.Default
    private Integer totalQuestions = 10;
    
    /** Dificultad (1-5, opcional) */
    @Min(value = 1, message = "Dificultad mínima es 1")
    @Max(value = 5, message = "Dificultad máxima es 5")
    private Integer difficulty;
    
    /** Tipo de pregunta específico (opcional, si es null se mezclan) */
    private QuestionType questionType;
    
    /** Continente específico (opcional) */
    private String continent;
    
    /** Categoría específica (opcional) */
    private String category;
    
    /** ID del oponente para duelos (opcional) */
    private Long opponentId;
}

