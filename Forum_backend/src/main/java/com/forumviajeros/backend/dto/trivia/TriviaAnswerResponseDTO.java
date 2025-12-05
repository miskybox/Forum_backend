package com.forumviajeros.backend.dto.trivia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con el resultado de una respuesta de trivia
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriviaAnswerResponseDTO {
    
    /** Si la respuesta fue correcta */
    private Boolean correct;
    
    /** La respuesta correcta */
    private String correctAnswer;
    
    /** Puntos obtenidos */
    private Integer pointsEarned;
    
    /** Explicación (dato curioso) */
    private String explanation;
    
    /** Puntuación total actual en la partida */
    private Integer currentGameScore;
    
    /** Respuestas correctas hasta ahora */
    private Integer correctAnswersCount;
    
    /** Racha actual de respuestas correctas */
    private Integer currentStreak;
    
    /** Si hay siguiente pregunta */
    private Boolean hasNextQuestion;
    
    /** Siguiente pregunta (si existe) */
    private TriviaQuestionDTO nextQuestion;
}

