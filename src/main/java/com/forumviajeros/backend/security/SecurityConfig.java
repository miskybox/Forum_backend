package com.forumviajeros.backend.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.forumviajeros.backend.security.filter.JwtAuthenticationFilter;
import com.forumviajeros.backend.security.filter.JwtAuthorizationFilter;
import com.forumviajeros.backend.security.filter.RateLimitingFilter;
import com.forumviajeros.backend.service.token.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final UserDetailsService userDetailsService;
        private final RefreshTokenService refreshTokenService;
        private final RateLimitingFilter rateLimitingFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration authConfig)
                        throws Exception {
                // CSRF is disabled for REST API with HttpOnly cookies + SameSite protection
                // SameSite=Lax cookies already prevent CSRF attacks from cross-origin requests
                // This is the recommended approach for stateless JWT APIs with cookie storage
                return http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .headers(headers -> headers
                                                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                                                .frameOptions(frame -> frame.deny())
                                                .httpStrictTransportSecurity(hsts -> hsts
                                                                .maxAgeInSeconds(31536000)))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth

                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                .requestMatchers("/api/auth/register", "/api/auth/login",
                                                                "/api/auth/refresh")
                                                .permitAll()

                                                .requestMatchers(HttpMethod.GET, "/api/categories", "/api/categories/**",
                                                                "/api/forums", "/api/forums/**", "/api/posts/**",
                                                                "/api/comments/**", "/api/countries", "/api/countries/**",
                                                                "/api/trivia/**", "/api/visited-places/**",
                                                                "/api/travel/ranking", "/api/travel/users/*/places",
                                                                "/api/travel/users/*/stats", "/api/travel/places/*",
                                                                "/api/feed/explore",
                                                                "/api/users", "/api/users/search",
                                                                "/api/users/*/followers", "/api/users/*/following",
                                                                "/api/users/*/follow-stats")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/events", "/api/events/{id}")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/events/create").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/events/{id}/edit")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/api/events/{id}/delete")
                                                .authenticated()
                                                .requestMatchers("/api/attendances/**").authenticated()
                                                .requestMatchers("/api/users/me").authenticated()
                                                .anyRequest().authenticated())
                                .addFilterBefore(rateLimitingFilter, JwtAuthenticationFilter.class)
                                .addFilterBefore(
                                                new JwtAuthenticationFilter(authenticationManager(authConfig),
                                                                refreshTokenService),
                                                UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(new JwtAuthorizationFilter(userDetailsService),
                                                UsernamePasswordAuthenticationFilter.class)
                                .build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                
                // Obtener orígenes permitidos desde variable de entorno o usar default
                String allowedOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
                if (allowedOrigins == null || allowedOrigins.isBlank()) {
                        allowedOrigins = System.getProperty("CORS_ALLOWED_ORIGINS", "http://localhost:5173,http://localhost:5174,http://localhost:5175,http://localhost:5176");
                }
                
                // Validar que no esté vacío y no sea "*" en producción
                if (allowedOrigins == null || allowedOrigins.isBlank()) {
                        throw new IllegalStateException(
                                "CORS_ALLOWED_ORIGINS debe estar configurado. " +
                                "No se permite '*' en producción por seguridad.");
                }
                
                // Validar que no sea "*" (demasiado permisivo)
                if (allowedOrigins.trim().equals("*")) {
                        throw new IllegalStateException(
                                "CORS_ALLOWED_ORIGINS no puede ser '*' por seguridad. " +
                                "Especifica orígenes específicos separados por comas.");
                }
                
                // Validar formato de URLs y limpiar espacios
                String[] origins = allowedOrigins.split(",");
                for (int i = 0; i < origins.length; i++) {
                        origins[i] = origins[i].trim();
                        
                        // Validar que cada origen tenga formato válido (http:// o https://)
                        if (!origins[i].startsWith("http://") && !origins[i].startsWith("https://")) {
                                throw new IllegalStateException(
                                        "CORS_ALLOWED_ORIGINS contiene un origen inválido: '" + origins[i] + "'. " +
                                        "Los orígenes deben empezar con http:// o https://");
                        }
                        
                        // Validar que no haya espacios en medio de la URL
                        if (origins[i].contains(" ")) {
                                throw new IllegalStateException(
                                        "CORS_ALLOWED_ORIGINS contiene espacios en el origen: '" + origins[i] + "'. " +
                                        "Asegúrate de separar múltiples orígenes solo con comas.");
                        }
                }
                
                configuration.setAllowedOrigins(Arrays.asList(origins));
                
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "Refresh-Token", "X-XSRF-TOKEN"));
                configuration.setExposedHeaders(Arrays.asList("Authorization", "X-XSRF-TOKEN"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        @Bean
        @Primary
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}