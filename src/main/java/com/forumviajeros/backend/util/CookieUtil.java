package com.forumviajeros.backend.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class for secure cookie management.
 * Implements HttpOnly cookies for JWT tokens to prevent XSS attacks.
 */
@Component
public class CookieUtil {

    public static final String ACCESS_TOKEN_COOKIE = "access_token";
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    @Value("${cookie.secure:true}")
    private boolean secureCookie;

    @Value("${cookie.same-site:Strict}")
    private String sameSite;

    @Value("${jwt.access-token.expiration:900}")
    private long accessTokenExpiration; // 15 minutes in seconds

    @Value("${jwt.refresh-token.expiration:604800}")
    private long refreshTokenExpiration; // 7 days in seconds

    /**
     * Creates a secure HttpOnly cookie for the access token.
     *
     * @param token The JWT access token
     * @return ResponseCookie configured with security settings
     */
    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(accessTokenExpiration)
                .sameSite(sameSite)
                .build();
    }

    /**
     * Creates a secure HttpOnly cookie for the refresh token.
     *
     * @param token The JWT refresh token
     * @return ResponseCookie configured with security settings
     */
    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(secureCookie)
                .path("/api/auth")
                .maxAge(refreshTokenExpiration)
                .sameSite(sameSite)
                .build();
    }

    /**
     * Creates an expired cookie to effectively delete it.
     *
     * @param cookieName The name of the cookie to delete
     * @param path The path of the cookie
     * @return ResponseCookie configured to expire immediately
     */
    public ResponseCookie createExpiredCookie(String cookieName, String path) {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(secureCookie)
                .path(path)
                .maxAge(0)
                .sameSite(sameSite)
                .build();
    }

    /**
     * Extracts the access token from cookies.
     *
     * @param request The HTTP request
     * @return The access token or null if not found
     */
    public String getAccessTokenFromCookies(HttpServletRequest request) {
        return getCookieValue(request, ACCESS_TOKEN_COOKIE);
    }

    /**
     * Extracts the refresh token from cookies.
     *
     * @param request The HTTP request
     * @return The refresh token or null if not found
     */
    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        return getCookieValue(request, REFRESH_TOKEN_COOKIE);
    }

    /**
     * Gets a cookie value by name.
     *
     * @param request The HTTP request
     * @param cookieName The name of the cookie
     * @return The cookie value or null if not found
     */
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
