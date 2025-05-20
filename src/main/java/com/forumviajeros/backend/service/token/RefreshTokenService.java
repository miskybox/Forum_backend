package com.forumviajeros.backend.service.token;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.forumviajeros.backend.security.constants.SecurityConstants;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class RefreshTokenService {

    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();

    private static final long REFRESH_TOKEN_EXPIRATION = 30 * 24 * 60 * 60 * 1000;

    public void saveToken(String refreshToken, String username) {
        refreshTokenStore.put(refreshToken, username);
    }

    public String getUsernameFromToken(String refreshToken) {
        return refreshTokenStore.get(refreshToken);
    }

    public void removeToken(String refreshToken) {
        refreshTokenStore.remove(refreshToken);
    }

    public boolean validateToken(String refreshToken) {
        return refreshTokenStore.containsKey(refreshToken);
    }

    public String generateRefreshToken(String username) {
        String tokenId = UUID.randomUUID().toString();

        String refreshToken = JWT.create()
                .withSubject(username)
                .withClaim("tokenId", tokenId)
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET));

        // Almacenar el token
        saveToken(refreshToken, username);

        return refreshToken;
    }

    public String generateAccessToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET));
    }

    public String extractRefreshTokenFromRequest(HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh-Token");

        if (refreshToken == null || refreshToken.isEmpty()) {
            refreshToken = request.getParameter("refreshToken");
        }

        return refreshToken;
    }
}