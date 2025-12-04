package com.forumviajeros.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.forumviajeros.backend.model.TriviaScore;

/**
 * Repositorio para la entidad TriviaScore
 */
@Repository
public interface TriviaScoreRepository extends JpaRepository<TriviaScore, Long> {

    /**
     * Busca las estadísticas de un usuario
     */
    Optional<TriviaScore> findByUserId(Long userId);

    /**
     * Verifica si existe registro de puntuación para un usuario
     */
    boolean existsByUserId(Long userId);

    /**
     * Ranking por puntuación total
     */
    Page<TriviaScore> findByOrderByTotalScoreDesc(Pageable pageable);

    /**
     * Ranking por nivel
     */
    Page<TriviaScore> findByOrderByLevelDescExperiencePointsDesc(Pageable pageable);

    /**
     * Ranking por porcentaje de aciertos (mínimo 50 preguntas)
     */
    @Query("SELECT ts FROM TriviaScore ts WHERE ts.totalQuestions >= 50 " +
           "ORDER BY (ts.correctAnswers * 1.0 / ts.totalQuestions) DESC")
    Page<TriviaScore> findByAccuracyRanking(Pageable pageable);

    /**
     * Ranking por mejor racha
     */
    Page<TriviaScore> findByOrderByBestStreakDesc(Pageable pageable);

    /**
     * Ranking por partidas perfectas
     */
    Page<TriviaScore> findByOrderByPerfectGamesDesc(Pageable pageable);

    /**
     * Obtiene la posición de un usuario en el ranking por puntuación
     */
    @Query(value = "SELECT rank FROM (" +
           "SELECT user_id, RANK() OVER (ORDER BY total_score DESC) as rank " +
           "FROM trivia_scores) ranked WHERE user_id = :userId", nativeQuery = true)
    Integer findUserRankByScore(@Param("userId") Long userId);

    /**
     * Obtiene usuarios con racha diaria activa
     */
    @Query("SELECT ts FROM TriviaScore ts WHERE ts.dailyStreak > 0 ORDER BY ts.dailyStreak DESC")
    Page<TriviaScore> findByDailyStreakRanking(Pageable pageable);

    /**
     * Cuenta total de jugadores
     */
    @Query("SELECT COUNT(ts) FROM TriviaScore ts WHERE ts.totalGames > 0")
    long countActivePlayers();
}

