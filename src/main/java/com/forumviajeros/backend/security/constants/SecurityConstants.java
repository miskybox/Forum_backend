package com.forumviajeros.backend.security.constants;

import io.github.cdimascio.dotenv.Dotenv;

public class SecurityConstants {
    public static final String SECRET = getSecretKey();
    public static final long EXPIRATION_TIME = 600_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    private SecurityConstants() {
    }

    private static String getSecretKey() {
        // Primero intentar desde variable de entorno del sistema
        String secretKey = System.getenv("JWT_SECRET_KEY");
        
        // Si no está, leer desde archivo .env
        if (secretKey == null || secretKey.isBlank()) {
            try {
                Dotenv dotenv = Dotenv.configure()
                        .ignoreIfMissing()
                        .load();
                secretKey = dotenv.get("JWT_SECRET_KEY");
            } catch (Exception e) {
                // Ignorar errores de carga de .env
            }
        }
        
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException(
                    "La variable de entorno JWT_SECRET_KEY debe estar configurada antes de iniciar la aplicación");
        }
        return secretKey;
    }
}