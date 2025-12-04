package com.forumviajeros.backend.dto.trivia;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para enviar una respuesta a una pregunta de trivia
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriviaAnswerRequestDTO {
    
    @NotNull(message = "El ID de la partida es obligatorio")
    private Long gameId;
    
    @NotNull(message = "El ID de la pregunta es obligatorio")
    private Long questionId;
    
    /** Respuesta seleccionada por el usuario */
    private String selectedAnswer;
    
    /** Tiempo que tardó en responder (en milisegundos) */
    private Long responseTimeMs;
    
    /** Si usó una pista */
    private Boolean hintUsed;
    
    /** Si el tiempo se agotó */
    private Boolean timedOut;
}

