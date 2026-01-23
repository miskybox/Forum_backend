package com.forumviajeros.backend.service.token;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.forumviajeros.backend.model.RefreshToken;
import com.forumviajeros.backend.repository.RefreshTokenRepository;
import com.forumviajeros.backend.security.constants.SecurityConstants;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class RefreshTokenService {

    // Security: Reduced from 30 days to 14 days for better security
    private static final long REFRESH_TOKEN_EXPIRATION = 14L * 24 * 60 * 60 * 1000;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void saveToken(String refreshToken, String username) {
        RefreshToken entity = new RefreshToken();
        entity.setToken(refreshToken);
        entity.setUsername(username);
        entity.setExpiresAt(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION));
        refreshTokenRepository.save(entity);
    }

    @Transactional
    public String getUsernameFromToken(String refreshToken) {
        try {
            decode(refreshToken);
        } catch (JWTVerificationException ex) {
            removeToken(refreshToken);
            return null;
        }

        return refreshTokenRepository.findByToken(refreshToken)
                .filter(token -> {
                    if (token.getExpiresAt().isBefore(Instant.now())) {
                        refreshTokenRepository.delete(token);
                        return false;
                    }
                    return true;
                })
                .map(RefreshToken::getUsername)
                .orElse(null);
    }

    @Transactional
    public void removeToken(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(refreshTokenRepository::delete);
    }

    @Transactional
    public boolean validateToken(String refreshToken) {
        try {
            decode(refreshToken);
        } catch (JWTVerificationException ex) {
            return false;
        }

        return refreshTokenRepository.findByToken(refreshToken)
                .filter(token -> {
                    if (token.getExpiresAt().isBefore(Instant.now())) {
                        refreshTokenRepository.delete(token);
                        return false;
                    }
                    return true;
                })
                .isPresent();
    }

    @Transactional
    public String generateRefreshToken(String username) {
        refreshTokenRepository.deleteByUsername(username);
        String tokenId = UUID.randomUUID().toString();

        String refreshToken = JWT.create()
                .withSubject(username)
                .withClaim("tokenId", tokenId)
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC512(SecurityConstants.getSecret()));

        saveToken(refreshToken, username);

        return refreshToken;
    }

    public String generateAccessToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SecurityConstants.getSecret()));
    }

    public String extractRefreshTokenFromRequest(HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh-Token");

        if (refreshToken == null || refreshToken.isEmpty()) {
            refreshToken = request.getParameter("refreshToken");
        }

        return refreshToken;
    }

    private DecodedJWT decode(String refreshToken) {
        return JWT.require(Algorithm.HMAC512(SecurityConstants.getSecret()))
                .build()
                .verify(refreshToken);
    }
}
