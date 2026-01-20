package com.forumviajeros.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.forumviajeros.backend.model.TriviaQuestion;
import com.forumviajeros.backend.model.TriviaQuestion.QuestionType;

/**
 * Repositorio para la entidad TriviaQuestion
 */
@Repository
public interface TriviaQuestionRepository extends JpaRepository<TriviaQuestion, Long> {

    /**
     * Obtiene preguntas activas de un tipo específico
     */
    List<TriviaQuestion> findByQuestionTypeAndActiveTrue(QuestionType questionType);

    /**
     * Obtiene preguntas de un país
     */
    List<TriviaQuestion> findByCountryIdAndActiveTrue(Long countryId);

    /**
     * Obtiene preguntas por dificultad
     */
    List<TriviaQuestion> findByDifficultyAndActiveTrue(Integer difficulty);

    /**
     * Obtiene preguntas aleatorias
     */
    @Query(value = "SELECT * FROM trivia_questions WHERE active = true ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<TriviaQuestion> findRandomQuestions(@Param("limit") int limit);

    /**
     * Obtiene preguntas aleatorias de un tipo específico
     */
    @Query(value = "SELECT * FROM trivia_questions WHERE active = true AND question_type = :type ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<TriviaQuestion> findRandomQuestionsByType(@Param("type") String type, @Param("limit") int limit);

    /**
     * Obtiene preguntas aleatorias por dificultad
     */
    @Query(value = "SELECT * FROM trivia_questions WHERE active = true AND difficulty = :difficulty ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<TriviaQuestion> findRandomQuestionsByDifficulty(@Param("difficulty") int difficulty, @Param("limit") int limit);

    /**
     * Obtiene preguntas aleatorias por continente
     */
    @Query(value = "SELECT tq.* FROM trivia_questions tq " +
           "JOIN countries c ON tq.country_id = c.id " +
           "WHERE tq.active = true AND LOWER(c.continent) = LOWER(:continent) " +
           "ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<TriviaQuestion> findRandomQuestionsByContinent(@Param("continent") String continent, @Param("limit") int limit);

    /**
     * Obtiene preguntas que un usuario no ha respondido recientemente
     */
    @Query(value = "SELECT tq.* FROM trivia_questions tq " +
           "WHERE tq.active = true AND tq.id NOT IN (" +
           "SELECT ta.question_id FROM trivia_answers ta " +
           "JOIN trivia_games tg ON ta.game_id = tg.id " +
           "WHERE tg.user_id = :userId AND tg.started_at > NOW() - INTERVAL '7 days') " +
           "ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<TriviaQuestion> findFreshQuestionsForUser(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * Cuenta preguntas por tipo
     */
    long countByQuestionTypeAndActiveTrue(QuestionType questionType);

    /**
     * Verifica si existe una pregunta para un país y tipo
     */
    boolean existsByCountryIdAndQuestionType(Long countryId, QuestionType questionType);
}

