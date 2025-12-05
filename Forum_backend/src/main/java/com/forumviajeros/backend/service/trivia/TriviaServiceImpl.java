package com.forumviajeros.backend.service.trivia;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.forumviajeros.backend.dto.trivia.TriviaAnswerRequestDTO;
import com.forumviajeros.backend.dto.trivia.TriviaAnswerResponseDTO;
import com.forumviajeros.backend.dto.trivia.TriviaGameRequestDTO;
import com.forumviajeros.backend.dto.trivia.TriviaGameResponseDTO;
import com.forumviajeros.backend.dto.trivia.TriviaLeaderboardDTO;
import com.forumviajeros.backend.dto.trivia.TriviaLeaderboardDTO.LeaderboardEntryDTO;
import com.forumviajeros.backend.dto.trivia.TriviaQuestionDTO;
import com.forumviajeros.backend.dto.trivia.TriviaScoreDTO;
import com.forumviajeros.backend.exception.BadRequestException;
import com.forumviajeros.backend.exception.ResourceNotFoundException;
import com.forumviajeros.backend.model.TriviaAnswer;
import com.forumviajeros.backend.model.TriviaGame;
import com.forumviajeros.backend.model.TriviaGame.GameMode;
import com.forumviajeros.backend.model.TriviaGame.GameStatus;
import com.forumviajeros.backend.model.TriviaQuestion;
import com.forumviajeros.backend.model.TriviaScore;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.TriviaAnswerRepository;
import com.forumviajeros.backend.repository.TriviaGameRepository;
import com.forumviajeros.backend.repository.TriviaQuestionRepository;
import com.forumviajeros.backend.repository.TriviaScoreRepository;
import com.forumviajeros.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de trivia
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TriviaServiceImpl implements TriviaService {

    private final TriviaGameRepository gameRepository;
    private final TriviaQuestionRepository questionRepository;
    private final TriviaAnswerRepository answerRepository;
    private final TriviaScoreRepository scoreRepository;
    private final UserRepository userRepository;

    @Override
    public TriviaGameResponseDTO startGame(Long userId, TriviaGameRequestDTO request) {
        User user = findUserById(userId);

        // Verificar si hay partida en progreso
        gameRepository.findByUserIdAndStatus(userId, GameStatus.IN_PROGRESS)
                .ifPresent(g -> {
                    throw new BadRequestException("Ya tienes una partida en progreso. Finalízala antes de iniciar otra.");
                });

        // Verificar trivia diaria
        if (request.getGameMode() == GameMode.DAILY) {
            LocalDateTime startOfDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
            if (gameRepository.hasPlayedDailyToday(userId, startOfDay)) {
                throw new BadRequestException("Ya has jugado la trivia diaria de hoy. Vuelve mañana!");
            }
        }

        // Crear nueva partida
        TriviaGame game = TriviaGame.builder()
                .user(user)
                .gameMode(request.getGameMode())
                .totalQuestions(request.getTotalQuestions())
                .difficulty(request.getDifficulty() != null ? request.getDifficulty() : 1)
                .category(request.getCategory())
                .continent(request.getContinent())
                .build();

        // Si es duelo, agregar oponente
        if (request.getGameMode() == GameMode.DUEL && request.getOpponentId() != null) {
            User opponent = findUserById(request.getOpponentId());
            game.setOpponent(opponent);
            game.setStatus(GameStatus.WAITING);
        }

        game = gameRepository.save(game);

        // Obtener primera pregunta
        TriviaQuestionDTO firstQuestion = generateQuestion(game);

        return toGameResponseDTO(game, firstQuestion);
    }

    @Override
    @Transactional(readOnly = true)
    public TriviaGameResponseDTO getGameStatus(Long gameId, Long userId) {
        TriviaGame game = findGameById(gameId);
        validateGameOwnership(game, userId);
        return toGameResponseDTO(game, null);
    }

    @Override
    @Transactional(readOnly = true)
    public TriviaQuestionDTO getNextQuestion(Long gameId, Long userId) {
        TriviaGame game = findGameById(gameId);
        validateGameOwnership(game, userId);

        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new BadRequestException("La partida no está en progreso");
        }

        if (game.getCurrentQuestionIndex() >= game.getTotalQuestions()) {
            throw new BadRequestException("No hay más preguntas en esta partida");
        }

        return generateQuestion(game);
    }

    @Override
    public TriviaAnswerResponseDTO answerQuestion(Long userId, TriviaAnswerRequestDTO request) {
        TriviaGame game = findGameById(request.getGameId());
        validateGameOwnership(game, userId);

        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new BadRequestException("La partida no está en progreso");
        }

        TriviaQuestion question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Pregunta no encontrada"));

        // Verificar si ya fue respondida
        if (answerRepository.existsByGameIdAndQuestionId(game.getId(), question.getId())) {
            throw new BadRequestException("Esta pregunta ya fue respondida");
        }

        // Evaluar respuesta
        boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(request.getSelectedAnswer());
        boolean timedOut = request.getTimedOut() != null && request.getTimedOut();

        // Calcular puntos
        int points = 0;
        if (isCorrect && !timedOut) {
            points = calculatePoints(question, request.getResponseTimeMs(), 
                    request.getHintUsed() != null && request.getHintUsed());
        }

        // Crear respuesta
        TriviaAnswer answer = TriviaAnswer.builder()
                .game(game)
                .question(question)
                .selectedAnswer(request.getSelectedAnswer())
                .isCorrect(isCorrect)
                .responseTimeMs(request.getResponseTimeMs())
                .pointsEarned(points)
                .questionIndex(game.getCurrentQuestionIndex() + 1)
                .hintUsed(request.getHintUsed() != null && request.getHintUsed())
                .timedOut(timedOut)
                .build();

        answerRepository.save(answer);

        // Actualizar partida
        game.setScore(game.getScore() + points);
        if (isCorrect) {
            game.setCorrectAnswers(game.getCorrectAnswers() + 1);
        }
        game.setCurrentQuestionIndex(game.getCurrentQuestionIndex() + 1);

        // Actualizar racha del usuario
        TriviaScore score = getOrCreateScore(userId);
        if (isCorrect) {
            score.setCurrentStreak(score.getCurrentStreak() + 1);
            if (score.getCurrentStreak() > score.getBestStreak()) {
                score.setBestStreak(score.getCurrentStreak());
            }
        } else {
            score.setCurrentStreak(0);
        }
        scoreRepository.save(score);

        // Verificar si la partida terminó
        boolean hasNext = game.getCurrentQuestionIndex() < game.getTotalQuestions();
        TriviaQuestionDTO nextQuestion = null;

        if (!hasNext) {
            finishGameInternal(game);
        } else {
            gameRepository.save(game);
            nextQuestion = generateQuestion(game);
        }

        return TriviaAnswerResponseDTO.builder()
                .correct(isCorrect)
                .correctAnswer(question.getCorrectAnswer())
                .pointsEarned(points)
                .explanation(question.getExplanation())
                .currentGameScore(game.getScore())
                .correctAnswersCount(game.getCorrectAnswers())
                .currentStreak(score.getCurrentStreak())
                .hasNextQuestion(hasNext)
                .nextQuestion(nextQuestion)
                .build();
    }

    @Override
    public TriviaGameResponseDTO finishGame(Long gameId, Long userId) {
        TriviaGame game = findGameById(gameId);
        validateGameOwnership(game, userId);

        if (game.getStatus() == GameStatus.COMPLETED) {
            return toGameResponseDTO(game, null);
        }

        finishGameInternal(game);
        return toGameResponseDTO(game, null);
    }

    @Override
    public void abandonGame(Long gameId, Long userId) {
        TriviaGame game = findGameById(gameId);
        validateGameOwnership(game, userId);

        if (game.getStatus() == GameStatus.COMPLETED) {
            throw new BadRequestException("La partida ya está completada");
        }

        game.setStatus(GameStatus.ABANDONED);
        game.setFinishedAt(LocalDateTime.now());
        gameRepository.save(game);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TriviaGameResponseDTO> getUserGameHistory(Long userId, int page, int size) {
        return gameRepository.findByUserIdOrderByStartedAtDesc(userId, PageRequest.of(page, size))
                .stream()
                .map(g -> toGameResponseDTO(g, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TriviaScoreDTO getUserScore(Long userId) {
        TriviaScore score = scoreRepository.findByUserId(userId)
                .orElse(TriviaScore.builder()
                        .user(findUserById(userId))
                        .build());

        Integer rank = scoreRepository.findUserRankByScore(userId);

        return toScoreDTO(score, rank);
    }

    @Override
    @Transactional(readOnly = true)
    public TriviaLeaderboardDTO getLeaderboard(String type, int page, int size) {
        List<TriviaScore> scores = scoreRepository.findByOrderByTotalScoreDesc(PageRequest.of(page, size))
                .getContent();

        List<LeaderboardEntryDTO> entries = new ArrayList<>();
        int rank = page * size + 1;

        for (TriviaScore score : scores) {
            entries.add(LeaderboardEntryDTO.builder()
                    .rank(rank++)
                    .userId(score.getUser().getId())
                    .username(score.getUser().getUsername())
                    .profileImageUrl(score.getUser().getProfileImageUrl())
                    .score(score.getTotalScore())
                    .level(score.getLevel())
                    .playerTitle(TriviaScoreDTO.getPlayerTitle(score.getLevel()))
                    .accuracyPercentage(score.getAccuracyPercentage())
                    .totalGames(score.getTotalGames())
                    .perfectGames(score.getPerfectGames())
                    .bestStreak(score.getBestStreak())
                    .build());
        }

        return TriviaLeaderboardDTO.builder()
                .leaderboardType(type)
                .period("all-time")
                .entries(entries)
                .totalPlayers((int) scoreRepository.countActivePlayers())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getUserRank(Long userId) {
        return scoreRepository.findUserRankByScore(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public TriviaQuestionDTO getRandomQuestion() {
        List<TriviaQuestion> questions = questionRepository.findRandomQuestions(1);
        if (questions.isEmpty()) {
            throw new ResourceNotFoundException("No hay preguntas disponibles");
        }
        return toQuestionDTO(questions.get(0), 1, 1);
    }

    @Override
    @Transactional(readOnly = true)
    public TriviaAnswerResponseDTO checkAnswer(Long questionId, String answer) {
        TriviaQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Pregunta no encontrada"));

        boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(answer);

        return TriviaAnswerResponseDTO.builder()
                .correct(isCorrect)
                .correctAnswer(question.getCorrectAnswer())
                .explanation(question.getExplanation())
                .pointsEarned(0)
                .hasNextQuestion(false)
                .build();
    }

    // === Métodos auxiliares ===

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private TriviaGame findGameById(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Partida no encontrada"));
    }

    private void validateGameOwnership(TriviaGame game, Long userId) {
        if (!game.getUser().getId().equals(userId)) {
            throw new BadRequestException("No tienes acceso a esta partida");
        }
    }

    private TriviaScore getOrCreateScore(Long userId) {
        return scoreRepository.findByUserId(userId)
                .orElseGet(() -> {
                    TriviaScore newScore = TriviaScore.builder()
                            .user(findUserById(userId))
                            .build();
                    return scoreRepository.save(newScore);
                });
    }

    private TriviaQuestionDTO generateQuestion(TriviaGame game) {
        List<TriviaQuestion> questions;

        if (game.getContinent() != null) {
            questions = questionRepository.findRandomQuestionsByContinent(game.getContinent(), 1);
        } else if (game.getDifficulty() != null) {
            questions = questionRepository.findRandomQuestionsByDifficulty(game.getDifficulty(), 1);
        } else {
            questions = questionRepository.findRandomQuestions(1);
        }

        if (questions.isEmpty()) {
            throw new ResourceNotFoundException("No hay preguntas disponibles para estos criterios");
        }

        return toQuestionDTO(questions.get(0), game.getCurrentQuestionIndex() + 1, game.getTotalQuestions());
    }

    private int calculatePoints(TriviaQuestion question, Long responseTimeMs, boolean hintUsed) {
        int basePoints = question.getPoints();
        int timeLimitMs = question.getTimeLimitSeconds() * 1000;

        if (responseTimeMs == null || responseTimeMs >= timeLimitMs) {
            return basePoints;
        }

        // Bonus por velocidad
        double timeRatio = (double) responseTimeMs / timeLimitMs;
        double speedBonus = Math.max(0, 1 - timeRatio) * 0.5;

        // Penalización por pista
        double hintPenalty = hintUsed ? 0.25 : 0;

        return (int) (basePoints * (1 + speedBonus - hintPenalty));
    }

    private void finishGameInternal(TriviaGame game) {
        game.setStatus(GameStatus.COMPLETED);
        game.setFinishedAt(LocalDateTime.now());

        // Calcular tiempo total
        long seconds = ChronoUnit.SECONDS.between(game.getStartedAt(), game.getFinishedAt());
        game.setTotalTimeSeconds((int) seconds);

        gameRepository.save(game);

        // Actualizar estadísticas del usuario
        TriviaScore score = getOrCreateScore(game.getUser().getId());
        score.setTotalScore(score.getTotalScore() + game.getScore());
        score.setTotalGames(score.getTotalGames() + 1);
        score.setTotalQuestions(score.getTotalQuestions() + game.getTotalQuestions());
        score.setCorrectAnswers(score.getCorrectAnswers() + game.getCorrectAnswers());
        score.setLastPlayed(LocalDateTime.now());

        if (game.isPerfectGame()) {
            score.setPerfectGames(score.getPerfectGames() + 1);
        }

        // Calcular nivel
        score.setExperiencePoints(score.getExperiencePoints() + game.getScore());
        score.calculateLevel();

        scoreRepository.save(score);
    }

    private TriviaGameResponseDTO toGameResponseDTO(TriviaGame game, TriviaQuestionDTO firstQuestion) {
        return TriviaGameResponseDTO.builder()
                .id(game.getId())
                .userId(game.getUser().getId())
                .username(game.getUser().getUsername())
                .opponentId(game.getOpponent() != null ? game.getOpponent().getId() : null)
                .opponentUsername(game.getOpponent() != null ? game.getOpponent().getUsername() : null)
                .gameMode(game.getGameMode())
                .status(game.getStatus())
                .score(game.getScore())
                .opponentScore(game.getOpponentScore())
                .totalQuestions(game.getTotalQuestions())
                .correctAnswers(game.getCorrectAnswers())
                .currentQuestionIndex(game.getCurrentQuestionIndex())
                .totalTimeSeconds(game.getTotalTimeSeconds())
                .difficulty(game.getDifficulty())
                .accuracyPercentage(game.getAccuracyPercentage())
                .perfectGame(game.isPerfectGame())
                .firstQuestion(firstQuestion)
                .startedAt(game.getStartedAt())
                .finishedAt(game.getFinishedAt())
                .build();
    }

    private TriviaQuestionDTO toQuestionDTO(TriviaQuestion question, int index, int total) {
        // Mezclar opciones
        List<String> options = new ArrayList<>();
        options.add(question.getCorrectAnswer());
        options.addAll(question.getWrongOptions());
        Collections.shuffle(options);

        return TriviaQuestionDTO.builder()
                .id(question.getId())
                .questionType(question.getQuestionType())
                .questionText(question.getQuestionText())
                .options(options)
                .imageUrl(question.getImageUrl())
                .difficulty(question.getDifficulty())
                .points(question.getPoints())
                .timeLimitSeconds(question.getTimeLimitSeconds())
                .questionIndex(index)
                .totalQuestions(total)
                .countryName(question.getCountry().getName())
                .countryFlag(question.getCountry().getFlagEmoji())
                .build();
    }

    private TriviaScoreDTO toScoreDTO(TriviaScore score, Integer rank) {
        int expToNext = 100 * (score.getLevel() + 1);

        return TriviaScoreDTO.builder()
                .id(score.getId())
                .userId(score.getUser().getId())
                .username(score.getUser().getUsername())
                .profileImageUrl(score.getUser().getProfileImageUrl())
                .totalScore(score.getTotalScore())
                .totalGames(score.getTotalGames())
                .totalQuestions(score.getTotalQuestions())
                .correctAnswers(score.getCorrectAnswers())
                .accuracyPercentage(score.getAccuracyPercentage())
                .currentStreak(score.getCurrentStreak())
                .bestStreak(score.getBestStreak())
                .level(score.getLevel())
                .experiencePoints(score.getExperiencePoints())
                .experienceToNextLevel(expToNext)
                .gamesWon(score.getGamesWon())
                .perfectGames(score.getPerfectGames())
                .avgResponseTime(score.getAvgResponseTime())
                .bestResponseTime(score.getBestResponseTime())
                .dailyStreak(score.getDailyStreak())
                .lastPlayed(score.getLastPlayed())
                .globalRank(rank)
                .playerTitle(TriviaScoreDTO.getPlayerTitle(score.getLevel()))
                .build();
    }
}

