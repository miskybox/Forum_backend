package com.forumviajeros.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.forumviajeros.backend.model.TriviaAnswer;
import com.forumviajeros.backend.model.TriviaQuestion.QuestionType;

/**
 * Repositorio para la entidad TriviaAnswer
 */
@Repository
public interface TriviaAnswerRepository extends JpaRepository<TriviaAnswer, Long> {

    /**
     * Obtiene todas las respuestas de una partida
     */
    List<TriviaAnswer> findByGameIdOrderByQuestionIndexAsc(Long gameId);

    /**
     * Cuenta respuestas correctas de una partida
     */
    long countByGameIdAndIsCorrectTrue(Long gameId);

    /**
     * Obtiene el tiempo promedio de respuesta de un usuario
     */
    @Query("SELECT AVG(a.responseTimeMs) FROM TriviaAnswer a " +
           "JOIN a.game g WHERE g.user.id = :userId AND a.isCorrect = true")
    Double findAverageResponseTimeByUserId(@Param("userId") Long userId);

    /**
     * Obtiene el mejor tiempo de respuesta de un usuario
     */
    @Query("SELECT MIN(a.responseTimeMs) FROM TriviaAnswer a " +
           "JOIN a.game g WHERE g.user.id = :userId AND a.isCorrect = true")
    Long findBestResponseTimeByUserId(@Param("userId") Long userId);

    /**
     * Estadísticas por tipo de pregunta para un usuario
     */
    @Query("SELECT q.questionType, COUNT(a), SUM(CASE WHEN a.isCorrect = true THEN 1 ELSE 0 END) " +
           "FROM TriviaAnswer a JOIN a.question q JOIN a.game g " +
           "WHERE g.user.id = :userId GROUP BY q.questionType")
    List<Object[]> findStatsByQuestionType(@Param("userId") Long userId);

    /**
     * Países donde el usuario tiene más errores
     */
    @Query("SELECT c.name, COUNT(a) FROM TriviaAnswer a " +
           "JOIN a.question q JOIN q.country c JOIN a.game g " +
           "WHERE g.user.id = :userId AND a.isCorrect = false " +
           "GROUP BY c.id, c.name ORDER BY COUNT(a) DESC")
    List<Object[]> findMostMissedCountries(@Param("userId") Long userId);

    /**
     * Verifica si una pregunta ya fue respondida en una partida
     */
    boolean existsByGameIdAndQuestionId(Long gameId, Long questionId);
}

