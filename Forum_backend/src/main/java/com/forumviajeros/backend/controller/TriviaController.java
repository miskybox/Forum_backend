package com.forumviajeros.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.forumviajeros.backend.dto.trivia.TriviaAnswerRequestDTO;
import com.forumviajeros.backend.dto.trivia.TriviaAnswerResponseDTO;
import com.forumviajeros.backend.dto.trivia.TriviaGameRequestDTO;
import com.forumviajeros.backend.dto.trivia.TriviaGameResponseDTO;
import com.forumviajeros.backend.dto.trivia.TriviaLeaderboardDTO;
import com.forumviajeros.backend.dto.trivia.TriviaQuestionDTO;
import com.forumviajeros.backend.dto.trivia.TriviaScoreDTO;
import com.forumviajeros.backend.exception.ResourceNotFoundException;
import com.forumviajeros.backend.repository.UserRepository;
import com.forumviajeros.backend.service.trivia.TriviaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para el juego de trivia geográfica
 */
@RestController
@RequestMapping("/api/trivia")
@Tag(name = "Trivia", description = "API para el juego de trivia geográfica")
public class TriviaController {

    private final TriviaService triviaService;
    private final UserRepository userRepository;

    public TriviaController(TriviaService triviaService, UserRepository userRepository) {
        this.triviaService = triviaService;
        this.userRepository = userRepository;
    }

    // === PARTIDAS ===

    @PostMapping("/games")
    @Operation(summary = "Iniciar nueva partida", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TriviaGameResponseDTO> startGame(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TriviaGameRequestDTO request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(triviaService.startGame(userId, request));
    }

    @GetMapping("/games/{gameId}")
    @Operation(summary = "Obtener estado de partida", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TriviaGameResponseDTO> getGameStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long gameId) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(triviaService.getGameStatus(gameId, userId));
    }

    @GetMapping("/games/{gameId}/question")
    @Operation(summary = "Obtener siguiente pregunta", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TriviaQuestionDTO> getNextQuestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long gameId) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(triviaService.getNextQuestion(gameId, userId));
    }

    @PostMapping("/games/answer")
    @Operation(summary = "Responder pregunta", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TriviaAnswerResponseDTO> answerQuestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TriviaAnswerRequestDTO request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(triviaService.answerQuestion(userId, request));
    }

    @PostMapping("/games/{gameId}/finish")
    @Operation(summary = "Finalizar partida", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TriviaGameResponseDTO> finishGame(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long gameId) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(triviaService.finishGame(gameId, userId));
    }

    @DeleteMapping("/games/{gameId}")
    @Operation(summary = "Abandonar partida", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> abandonGame(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long gameId) {
        Long userId = getUserId(userDetails);
        triviaService.abandonGame(gameId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-games")
    @Operation(summary = "Obtener historial de partidas", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<TriviaGameResponseDTO>> getMyGameHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(triviaService.getUserGameHistory(userId, page, size));
    }

    // === PUNTUACIONES ===

    @GetMapping("/my-score")
    @Operation(summary = "Obtener mis estadísticas", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TriviaScoreDTO> getMyScore(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(triviaService.getUserScore(userId));
    }

    @GetMapping("/users/{userId}/score")
    @Operation(summary = "Obtener estadísticas de un usuario")
    public ResponseEntity<TriviaScoreDTO> getUserScore(@PathVariable Long userId) {
        return ResponseEntity.ok(triviaService.getUserScore(userId));
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Obtener ranking global")
    public ResponseEntity<TriviaLeaderboardDTO> getLeaderboard(
            @RequestParam(defaultValue = "score") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(triviaService.getLeaderboard(type, page, size));
    }

    @GetMapping("/my-rank")
    @Operation(summary = "Obtener mi posición en el ranking", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Integer> getMyRank(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(triviaService.getUserRank(userId));
    }

    // === PREGUNTAS (PRÁCTICA) ===

    @GetMapping("/questions/random")
    @Operation(summary = "Obtener pregunta aleatoria (modo práctica)")
    public ResponseEntity<TriviaQuestionDTO> getRandomQuestion() {
        return ResponseEntity.ok(triviaService.getRandomQuestion());
    }

    @PostMapping("/questions/{questionId}/check")
    @Operation(summary = "Verificar respuesta (modo práctica)")
    public ResponseEntity<TriviaAnswerResponseDTO> checkAnswer(
            @PathVariable Long questionId,
            @RequestParam String answer) {
        return ResponseEntity.ok(triviaService.checkAnswer(questionId, answer));
    }

    /**
     * Obtiene el ID del usuario desde los detalles de autenticación
     */
    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"))
                .getId();
    }
}

