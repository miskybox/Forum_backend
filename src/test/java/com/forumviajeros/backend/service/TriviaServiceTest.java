package com.forumviajeros.backend.service;

import com.forumviajeros.backend.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para modelos y lógica de Trivia
 */
class TriviaServiceTest {

    private User testUser;
    private Country testCountry;
    private TriviaQuestion testQuestion;
    private TriviaGame testGame;
    private TriviaScore testScore;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@test.com")
                .build();

        testCountry = Country.builder()
                .id(1L)
                .isoCode("ES")
                .name("España")
                .capital("Madrid")
                .build();

        testQuestion = TriviaQuestion.builder()
                .id(1L)
                .questionType(TriviaQuestion.QuestionType.CAPITAL)
                .country(testCountry)
                .questionText("¿Cuál es la capital de España?")
                .correctAnswer("Madrid")
                .wrongOptions(Arrays.asList("Barcelona", "Sevilla", "Valencia"))
                .difficulty(1)
                .build();

        testGame = TriviaGame.builder()
                .id(1L)
                .user(testUser)
                .gameMode(TriviaGame.GameMode.QUICK)
                .score(0)
                .correctAnswers(0)
                .totalQuestions(10)
                .status(TriviaGame.GameStatus.IN_PROGRESS)
                .build();

        testScore = TriviaScore.builder()
                .id(1L)
                .user(testUser)
                .totalGames(5)
                .correctAnswers(40)
                .totalQuestions(50)
                .currentStreak(3)
                .bestStreak(7)
                .level(2)
                .experiencePoints(500)
                .build();
    }

    @Test
    @DisplayName("TriviaQuestion debe tener opciones incorrectas")
    void triviaQuestion_ShouldHaveWrongOptions() {
        assertNotNull(testQuestion);
        assertEquals(3, testQuestion.getWrongOptions().size());
        assertTrue(testQuestion.getWrongOptions().contains("Barcelona"));
        assertEquals("Madrid", testQuestion.getCorrectAnswer());
    }

    @Test
    @DisplayName("TriviaGame debe inicializarse correctamente")
    void triviaGame_ShouldInitializeCorrectly() {
        assertNotNull(testGame);
        assertEquals(TriviaGame.GameStatus.IN_PROGRESS, testGame.getStatus());
        assertEquals(0, testGame.getScore());
        assertEquals(10, testGame.getTotalQuestions());
    }

    @Test
    @DisplayName("TriviaScore debe tener estadísticas")
    void triviaScore_ShouldHaveStatistics() {
        assertNotNull(testScore);
        assertEquals(5, testScore.getTotalGames());
        assertEquals(40, testScore.getCorrectAnswers());
        assertEquals(50, testScore.getTotalQuestions());
    }

    @Test
    @DisplayName("Debe calcular precisión correctamente")
    void calculateAccuracy_ShouldWork() {
        Double accuracy = testScore.getAccuracyPercentage();
        assertEquals(80.0, accuracy);
    }

    @Test
    @DisplayName("Debe manejar división por cero")
    void calculateAccuracy_ShouldHandleZero() {
        TriviaScore emptyScore = TriviaScore.builder()
                .totalQuestions(0)
                .correctAnswers(0)
                .build();
        
        assertEquals(0.0, emptyScore.getAccuracyPercentage());
    }

    @Test
    @DisplayName("QuestionType enum debe tener valores esperados")
    void questionType_ShouldHaveExpectedValues() {
        assertNotNull(TriviaQuestion.QuestionType.valueOf("CAPITAL"));
        assertNotNull(TriviaQuestion.QuestionType.valueOf("FLAG"));
        assertNotNull(TriviaQuestion.QuestionType.valueOf("CURRENCY"));
        assertNotNull(TriviaQuestion.QuestionType.valueOf("CONTINENT"));
    }

    @Test
    @DisplayName("GameMode enum debe tener valores esperados")
    void gameMode_ShouldHaveExpectedValues() {
        assertNotNull(TriviaGame.GameMode.valueOf("QUICK"));
        assertNotNull(TriviaGame.GameMode.valueOf("CHALLENGE"));
        assertNotNull(TriviaGame.GameMode.valueOf("DAILY"));
        assertNotNull(TriviaGame.GameMode.valueOf("PRACTICE"));
    }

    @Test
    @DisplayName("GameStatus enum debe tener valores esperados")
    void gameStatus_ShouldHaveExpectedValues() {
        assertNotNull(TriviaGame.GameStatus.valueOf("IN_PROGRESS"));
        assertNotNull(TriviaGame.GameStatus.valueOf("COMPLETED"));
        assertNotNull(TriviaGame.GameStatus.valueOf("ABANDONED"));
    }

    @Test
    @DisplayName("Respuesta correcta debe validarse")
    void validateAnswer_ShouldDetectCorrectAnswer() {
        assertTrue("Madrid".equals(testQuestion.getCorrectAnswer()));
    }

    @Test
    @DisplayName("Respuesta incorrecta debe validarse")
    void validateAnswer_ShouldDetectIncorrectAnswer() {
        assertFalse("Barcelona".equals(testQuestion.getCorrectAnswer()));
    }

    @Test
    @DisplayName("TriviaQuestion debe tener dificultad")
    void triviaQuestion_ShouldHaveDifficulty() {
        assertEquals(1, testQuestion.getDifficulty());
    }

    @Test
    @DisplayName("TriviaGame debe calcular precisión")
    void triviaGame_ShouldCalculateAccuracy() {
        TriviaGame game = TriviaGame.builder()
                .correctAnswers(8)
                .totalQuestions(10)
                .build();
        
        assertEquals(80.0, game.getAccuracyPercentage());
    }

    @Test
    @DisplayName("TriviaGame debe detectar partida perfecta")
    void triviaGame_ShouldDetectPerfectGame() {
        TriviaGame perfectGame = TriviaGame.builder()
                .correctAnswers(10)
                .totalQuestions(10)
                .status(TriviaGame.GameStatus.COMPLETED)
                .build();
        
        assertTrue(perfectGame.isPerfectGame());
    }

    @Test
    @DisplayName("TriviaGame no perfecta debe detectarse")
    void triviaGame_ShouldDetectNonPerfectGame() {
        TriviaGame game = TriviaGame.builder()
                .correctAnswers(8)
                .totalQuestions(10)
                .status(TriviaGame.GameStatus.COMPLETED)
                .build();
        
        assertFalse(game.isPerfectGame());
    }

    @Test
    @DisplayName("Best streak debe poder actualizarse")
    void bestStreak_CanBeUpdated() {
        int currentStreak = 8;
        int bestStreak = testScore.getBestStreak();
        int newBest = Math.max(bestStreak, currentStreak);
        
        assertEquals(8, newBest);
    }

    @Test
    @DisplayName("QuestionType CAPITAL debe existir")
    void questionType_CapitalExists() {
        assertEquals(TriviaQuestion.QuestionType.CAPITAL, testQuestion.getQuestionType());
    }
}
