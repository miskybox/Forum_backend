package com.forumviajeros.backend.security.filter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de rate limiting para proteger endpoints de autenticación contra ataques de fuerza bruta.
 * 
 * Implementa un algoritmo de ventana deslizante simple:
 * - Máximo 5 intentos por IP por minuto para /api/auth/login
 * - Máximo 3 intentos por IP por minuto para /api/auth/register
 * - Máximo 10 intentos por IP por minuto para /api/auth/refresh
 */
@Component
@Order(1) // Ejecutar antes de otros filtros de seguridad
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Configuración de rate limits (intentos por ventana de tiempo)
    private static final int LOGIN_MAX_ATTEMPTS = 5;
    private static final int LOGIN_WINDOW_SECONDS = 60; // 1 minuto
    
    private static final int REGISTER_MAX_ATTEMPTS = 3;
    private static final int REGISTER_WINDOW_SECONDS = 60; // 1 minuto
    
    private static final int REFRESH_MAX_ATTEMPTS = 10;
    private static final int REFRESH_WINDOW_SECONDS = 60; // 1 minuto

    // Almacenamiento de intentos por IP y endpoint
    private final Map<String, RequestHistory> loginAttempts = new ConcurrentHashMap<>();
    private final Map<String, RequestHistory> registerAttempts = new ConcurrentHashMap<>();
    private final Map<String, RequestHistory> refreshAttempts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String clientIp = getClientIpAddress(request);

        // Solo aplicar rate limiting a endpoints de autenticación
        if (path.equals("/api/auth/login")) {
            if (!isAllowed(clientIp, loginAttempts, LOGIN_MAX_ATTEMPTS, LOGIN_WINDOW_SECONDS)) {
                handleRateLimitExceeded(response, "Demasiados intentos de login. Por favor, espera un minuto.");
                return;
            }
        } else if (path.equals("/api/auth/register")) {
            if (!isAllowed(clientIp, registerAttempts, REGISTER_MAX_ATTEMPTS, REGISTER_WINDOW_SECONDS)) {
                handleRateLimitExceeded(response, "Demasiados intentos de registro. Por favor, espera un minuto.");
                return;
            }
        } else if (path.equals("/api/auth/refresh")) {
            if (!isAllowed(clientIp, refreshAttempts, REFRESH_MAX_ATTEMPTS, REFRESH_WINDOW_SECONDS)) {
                handleRateLimitExceeded(response, "Demasiados intentos de renovación de token. Por favor, espera un minuto.");
                return;
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Verifica si una IP está permitida según el rate limit configurado.
     */
    private boolean isAllowed(String clientIp, Map<String, RequestHistory> attemptsMap,
            int maxAttempts, int windowSeconds) {
        
        Instant now = Instant.now();
        RequestHistory history = attemptsMap.computeIfAbsent(clientIp, k -> new RequestHistory());

        // Limpiar intentos antiguos (fuera de la ventana de tiempo)
        history.cleanOldAttempts(now, windowSeconds);

        // Verificar si se excedió el límite
        if (history.getAttemptCount() >= maxAttempts) {
            logger.warn("Rate limit excedido para IP: {} - {} intentos en {} segundos",
                    clientIp, history.getAttemptCount(), windowSeconds);
            return false;
        }

        // Registrar nuevo intento
        history.addAttempt(now);
        return true;
    }

    /**
     * Obtiene la dirección IP real del cliente, considerando proxies y load balancers.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Tomar la primera IP de la lista (IP original del cliente)
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Responde con error 429 (Too Many Requests) cuando se excede el rate limit.
     */
    private void handleRateLimitExceeded(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, String> errorResponse = Map.of(
                "message", message,
                "error", "TOO_MANY_REQUESTS",
                "status", "429"
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }

    /**
     * Clase interna para almacenar el historial de intentos de una IP.
     */
    private static class RequestHistory {
        private final java.util.List<Instant> attempts = new java.util.ArrayList<>();

        public synchronized void addAttempt(Instant timestamp) {
            attempts.add(timestamp);
        }

        public synchronized void cleanOldAttempts(Instant now, int windowSeconds) {
            Instant cutoff = now.minusSeconds(windowSeconds);
            attempts.removeIf(attempt -> attempt.isBefore(cutoff));
        }

        public synchronized int getAttemptCount() {
            return attempts.size();
        }
    }
}

