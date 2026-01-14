package com.forumviajeros.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequiredArgsConstructor
@Tag(name = "Trivia", description = "API para el juego de trivia geográfica")
public class TriviaController {

    private final TriviaService triviaService;
    private final UserRepository userRepository;

    // === PARTIDAS ===

    @PostMapping("/games")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Iniciar nueva partida", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TriviaGameResponseDTO> startGame(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TriviaGameRequestDTO request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(triviaService.startGame(userId, request));
    }

    @GetMapping("/games/{gameId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener estado de partida", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TriviaGameResponseDTO> getGameStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long gameId) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(triviaService.getGameStatus(gameId, userId));
    }

    @GetMapping("/games/{gameId}/question")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener siguiente pregunta", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TriviaQuestionDTO> getNextQuestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long gameId) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(triviaService.getNextQuestion(gameId, userId));
    }

    @PostMapping("/games/answer")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Responder pregunta", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TriviaAnswerResponseDTO> answerQuestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TriviaAnswerRequestDTO request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(triviaService.answerQuestion(userId, request));
    }

    @PostMapping("/games/{gameId}/finish")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Finalizar partida", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TriviaGameResponseDTO> finishGame(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long gameId) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(triviaService.finishGame(gameId, userId));
    }

    @DeleteMapping("/games/{gameId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Abandonar partida", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> abandonGame(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long gameId) {
        Long userId = getUserId(userDetails);
        triviaService.abandonGame(gameId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-games")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener historial de partidas", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<TriviaGameResponseDTO>> getMyGameHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserId(userDetails);
        int validatedSize = validatePageSize(size);
        return ResponseEntity.ok(triviaService.getUserGameHistory(userId, page, validatedSize));
    }

    // === PUNTUACIONES ===

    @GetMapping("/my-score")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener mis estadísticas", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TriviaScoreDTO> getMyScore(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(triviaService.getUserScore(userId));
    }

    @GetMapping("/users/{userId}/score")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener estadísticas de un usuario", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TriviaScoreDTO> getUserScore(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long userId) {
        Long currentUserId = getUserId(userDetails);
        
        // Solo puede ver sus propias estadísticas, a menos que sea ADMIN
        if (!currentUserId.equals(userId) && !isAdmin()) {
            throw new AccessDeniedException("No tienes permiso para ver las estadísticas de otro usuario");
        }
        
        return ResponseEntity.ok(triviaService.getUserScore(userId));
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Obtener ranking global")
    public ResponseEntity<TriviaLeaderboardDTO> getLeaderboard(
            @RequestParam(defaultValue = "score") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int validatedSize = validatePageSize(size);
        return ResponseEntity.ok(triviaService.getLeaderboard(type, page, validatedSize));
    }

    @GetMapping("/my-rank")
    @PreAuthorize("isAuthenticated()")
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
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }

    /**
     * Verifica si el usuario actual tiene rol de ADMIN
     */
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
    }

    /**
     * Valida y limita el tamaño de página a un máximo de 100
     */
    private int validatePageSize(int size) {
        if (size < 1) {
            return 10; // Tamaño mínimo
        }
        if (size > 100) {
            return 100; // Tamaño máximo
        }
        return size;
    }
}

