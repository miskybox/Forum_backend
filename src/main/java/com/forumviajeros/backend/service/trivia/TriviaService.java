package com.forumviajeros.backend.service.trivia;

import java.util.List;

import com.forumviajeros.backend.dto.trivia.TriviaAnswerRequestDTO;
import com.forumviajeros.backend.dto.trivia.TriviaAnswerResponseDTO;
import com.forumviajeros.backend.dto.trivia.TriviaGameRequestDTO;
import com.forumviajeros.backend.dto.trivia.TriviaGameResponseDTO;
import com.forumviajeros.backend.dto.trivia.TriviaLeaderboardDTO;
import com.forumviajeros.backend.dto.trivia.TriviaQuestionDTO;
import com.forumviajeros.backend.dto.trivia.TriviaScoreDTO;

/**
 * Servicio para gestión del juego de trivia geográfica
 */
public interface TriviaService {

    // === PARTIDAS ===

    /**
     * Inicia una nueva partida de trivia
     */
    TriviaGameResponseDTO startGame(Long userId, TriviaGameRequestDTO request);

    /**
     * Obtiene el estado actual de una partida
     */
    TriviaGameResponseDTO getGameStatus(Long gameId, Long userId);

    /**
     * Obtiene la partida activa del usuario (si existe)
     */
    TriviaGameResponseDTO getActiveGame(Long userId);

    /**
     * Obtiene la siguiente pregunta de una partida
     */
    TriviaQuestionDTO getNextQuestion(Long gameId, Long userId);

    /**
     * Responde una pregunta
     */
    TriviaAnswerResponseDTO answerQuestion(Long userId, TriviaAnswerRequestDTO request);

    /**
     * Finaliza una partida
     */
    TriviaGameResponseDTO finishGame(Long gameId, Long userId);

    /**
     * Abandona una partida
     */
    void abandonGame(Long gameId, Long userId);

    /**
     * Obtiene el historial de partidas de un usuario
     */
    List<TriviaGameResponseDTO> getUserGameHistory(Long userId, int page, int size);

    // === PUNTUACIONES ===

    /**
     * Obtiene las estadísticas de un usuario
     */
    TriviaScoreDTO getUserScore(Long userId);

    /**
     * Obtiene el ranking global
     */
    TriviaLeaderboardDTO getLeaderboard(String type, int page, int size);

    /**
     * Obtiene la posición del usuario en el ranking
     */
    Integer getUserRank(Long userId);

    // === PREGUNTAS ===

    /**
     * Obtiene una pregunta aleatoria (modo rápido)
     */
    TriviaQuestionDTO getRandomQuestion();

    /**
     * Verifica una respuesta sin estar en una partida (modo práctica)
     */
    TriviaAnswerResponseDTO checkAnswer(Long questionId, String answer);
}

