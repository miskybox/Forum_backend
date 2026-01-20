package com.forumviajeros.backend.security.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.forumviajeros.backend.security.constants.SecurityConstants;
import com.forumviajeros.backend.util.CookieUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
    private final UserDetailsService userDetailsService;

    public JwtAuthorizationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String username = JWT.require(Algorithm.HMAC512(SecurityConstants.getSecret()))
                    .build()
                    .verify(token)
                    .getSubject();
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (logger.isDebugEnabled()) {
                    logger.debug("Token válido para usuario: {} con autoridades: {}", username, userDetails.getAuthorities());
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (JWTVerificationException e) {
            logger.error("Error verificando JWT token: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts JWT token from request.
     * First tries HttpOnly cookie, then falls back to Authorization header for backward compatibility.
     *
     * @param request The HTTP request
     * @return The JWT token or null if not found
     */
    private String extractToken(HttpServletRequest request) {
        // First, try to get token from HttpOnly cookie (preferred method)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (CookieUtil.ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (token != null && !token.isEmpty()) {
                        return token;
                    }
                }
            }
        }

        // Fallback: try Authorization header (backward compatibility)
        String header = request.getHeader(SecurityConstants.HEADER_STRING);
        if (header != null && header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return header.replace(SecurityConstants.TOKEN_PREFIX, "");
        }

        return null;
    }
}
