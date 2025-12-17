package com.forumviajeros.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de CORS.
 * 
 * NOTA: La configuración principal de CORS está en SecurityConfig.java
 * Esta clase solo mantiene compatibilidad con WebMvcConfigurer.
 * 
 * Para producción, configurar CORS_ALLOWED_ORIGINS en variables de entorno.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configuración de CORS para WebMvc (complementaria a SecurityConfig).
     * La configuración principal y más segura está en SecurityConfig.corsConfigurationSource()
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Obtener orígenes desde variable de entorno o usar default de desarrollo
        String allowedOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
        if (allowedOrigins == null || allowedOrigins.isBlank()) {
            allowedOrigins = System.getProperty("CORS_ALLOWED_ORIGINS", "http://localhost:5173");
        }
        
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins.split(","))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("Authorization", "Content-Type", "Accept", "Refresh-Token")
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    // NOTA: No crear CorsFilter aquí para evitar conflictos con SecurityConfig
    // La configuración de CORS se maneja en SecurityConfig.corsConfigurationSource()
}