package com.forumviajeros.backend.dto.trivia;

import java.util.List;

import com.forumviajeros.backend.model.TriviaQuestion.QuestionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para enviar una pregunta de trivia al frontend
 * (sin incluir la respuesta correcta directamente)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriviaQuestionDTO {
    
    private Long id;
    
    /** Tipo de pregunta */
    private QuestionType questionType;
    
    /** Texto de la pregunta */
    private String questionText;
    
    /** Todas las opciones mezcladas (incluyendo la correcta) */
    private List<String> options;
    
    /** URL de imagen si aplica */
    private String imageUrl;
    
    /** Nivel de dificultad */
    private Integer difficulty;
    
    /** Puntos que otorga */
    private Integer points;
    
    /** Tiempo límite en segundos */
    private Integer timeLimitSeconds;
    
    /** Índice de la pregunta en la partida */
    private Integer questionIndex;
    
    /** Total de preguntas en la partida */
    private Integer totalQuestions;
    
    /** Nombre del país relacionado (para contexto) */
    private String countryName;
    
    /** Bandera del país (emoji o URL) */
    private String countryFlag;
}

