package com.forumviajeros.backend.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
import com.forumviajeros.backend.service.token.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final UserDetailsService userDetailsService;
        private final RefreshTokenService refreshTokenService;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration authConfig)
                        throws Exception {
                return http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth

                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                .requestMatchers("/api/auth/register", "/api/auth/login",
                                                                "/api/auth/refresh")
                                                .permitAll()

                                                .requestMatchers(HttpMethod.GET, "/api/categories", "/api/forums",
                                                                "/api/forums/**", "/api/posts/**", "/api/comments/**")
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
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
                configuration.setExposedHeaders(Arrays.asList("Authorization"));
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