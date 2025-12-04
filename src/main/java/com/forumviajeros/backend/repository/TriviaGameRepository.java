package com.forumviajeros.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.forumviajeros.backend.model.TriviaGame;
import com.forumviajeros.backend.model.TriviaGame.GameMode;
import com.forumviajeros.backend.model.TriviaGame.GameStatus;

/**
 * Repositorio para la entidad TriviaGame
 */
@Repository
public interface TriviaGameRepository extends JpaRepository<TriviaGame, Long> {

    /**
     * Obtiene las partidas de un usuario
     */
    Page<TriviaGame> findByUserIdOrderByStartedAtDesc(Long userId, Pageable pageable);

    /**
     * Obtiene partidas de un usuario por modo de juego
     */
    List<TriviaGame> findByUserIdAndGameModeOrderByStartedAtDesc(Long userId, GameMode gameMode);

    /**
     * Obtiene partidas de un usuario por estado
     */
    List<TriviaGame> findByUserIdAndStatusOrderByStartedAtDesc(Long userId, GameStatus status);

    /**
     * Busca partida en progreso de un usuario
     */
    Optional<TriviaGame> findByUserIdAndStatus(Long userId, GameStatus status);

    /**
     * Obtiene las últimas N partidas completadas de un usuario
     */
    List<TriviaGame> findTop10ByUserIdAndStatusOrderByFinishedAtDesc(Long userId, GameStatus status);

    /**
     * Cuenta partidas de un usuario
     */
    long countByUserId(Long userId);

    /**
     * Cuenta partidas completadas de un usuario
     */
    long countByUserIdAndStatus(Long userId, GameStatus status);

    /**
     * Cuenta partidas perfectas de un usuario
     */
    @Query("SELECT COUNT(g) FROM TriviaGame g WHERE g.user.id = :userId AND g.status = 'COMPLETED' " +
           "AND g.correctAnswers = g.totalQuestions")
    long countPerfectGamesByUserId(@Param("userId") Long userId);

    /**
     * Obtiene la mejor puntuación de un usuario
     */
    @Query("SELECT MAX(g.score) FROM TriviaGame g WHERE g.user.id = :userId AND g.status = 'COMPLETED'")
    Integer findBestScoreByUserId(@Param("userId") Long userId);

    /**
     * Busca duelos pendientes donde el usuario es oponente
     */
    List<TriviaGame> findByOpponentIdAndStatus(Long opponentId, GameStatus status);

    /**
     * Obtiene partidas del día de un usuario
     */
    @Query("SELECT g FROM TriviaGame g WHERE g.user.id = :userId AND g.startedAt >= :startOfDay")
    List<TriviaGame> findTodaysGamesByUserId(@Param("userId") Long userId, @Param("startOfDay") LocalDateTime startOfDay);

    /**
     * Verifica si el usuario ya jugó la trivia diaria hoy
     */
    @Query("SELECT COUNT(g) > 0 FROM TriviaGame g WHERE g.user.id = :userId " +
           "AND g.gameMode = 'DAILY' AND g.startedAt >= :startOfDay")
    boolean hasPlayedDailyToday(@Param("userId") Long userId, @Param("startOfDay") LocalDateTime startOfDay);

    /**
     * Obtiene el historial de duelos entre dos usuarios
     */
    @Query("SELECT g FROM TriviaGame g WHERE g.gameMode = 'DUEL' AND g.status = 'COMPLETED' " +
           "AND ((g.user.id = :user1 AND g.opponent.id = :user2) OR (g.user.id = :user2 AND g.opponent.id = :user1)) " +
           "ORDER BY g.finishedAt DESC")
    List<TriviaGame> findDuelHistoryBetweenUsers(@Param("user1") Long user1, @Param("user2") Long user2);
}

