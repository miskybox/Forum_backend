package com.forumviajeros.backend.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Entidad que representa una pregunta de trivia geográfica.
 * Las preguntas pueden ser generadas dinámicamente o predefinidas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trivia_questions")
public class TriviaQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tipo de pregunta (capital, bandera, moneda, etc.)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    /**
     * País relacionado con la pregunta
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    /**
     * Texto de la pregunta (puede ser generado dinámicamente según el tipo)
     */
    @Column(name = "question_text", length = 500)
    private String questionText;

    /**
     * Respuesta correcta
     */
    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    /**
     * Opciones de respuesta incorrectas (para multiple choice)
     */
    @ElementCollection
    @CollectionTable(name = "trivia_question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text")
    @Builder.Default
    private List<String> wrongOptions = new ArrayList<>();

    /**
     * Nivel de dificultad (1-5)
     */
    @Builder.Default
    private Integer difficulty = 1;

    /**
     * Puntos que otorga responder correctamente
     */
    @Builder.Default
    private Integer points = 10;

    /**
     * Tiempo límite en segundos para responder
     */
    @Column(name = "time_limit_seconds")
    @Builder.Default
    private Integer timeLimitSeconds = 15;

    /**
     * URL de imagen asociada (para preguntas de banderas, mapas, etc.)
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * Explicación que se muestra después de responder
     */
    @Column(length = 500)
    private String explanation;

    /**
     * Categoría adicional para filtrar preguntas
     */
    private String category;

    /**
     * Si la pregunta está activa
     */
    @Builder.Default
    private Boolean active = true;

    /**
     * Tipos de preguntas disponibles en la trivia
     */
    public enum QuestionType {
        /** ¿Cuál es la capital de X? */
        CAPITAL,
        /** ¿A qué país pertenece esta bandera? */
        FLAG,
        /** ¿Cuál es la moneda oficial de X? */
        CURRENCY,
        /** ¿En qué continente está X? */
        CONTINENT,
        /** ¿Cuál es el idioma oficial de X? */
        LANGUAGE,
        /** ¿Cuál es la población aproximada de X? */
        POPULATION,
        /** ¿Cuál es el país más grande/pequeño de X región? */
        AREA,
        /** Pregunta sobre dato curioso */
        FUN_FACT,
        /** ¿Cuál es el código de llamada de X? */
        CALLING_CODE,
        /** Ubicar país en el mapa */
        MAP_LOCATION
    }
}

