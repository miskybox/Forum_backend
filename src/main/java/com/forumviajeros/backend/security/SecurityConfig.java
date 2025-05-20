package com.forumviajeros.backend.security;

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
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login",
                                "/api/auth/refresh")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/events", "/api/events/{id}")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/events/create").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/events/{id}/edit")
                        .authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/events/{id}/delete")
                        .authenticated()
                        .requestMatchers("/api/attendances/**").authenticated()
                        .requestMatchers("/api/users/me/**").authenticated()
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    @Primary
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}