package com.forumviajeros.backend.security.constants;

public class SecurityConstants {
    public static final String SECRET = getSecretKey();
    public static final long EXPIRATION_TIME = 600_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    private SecurityConstants() {
    }

    // quitar este método al deployar
    private static String getSecretKey() {
        String secretKey = System.getenv("JWT_SECRET_KEY");
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException(
                    "La variable de entorno JWT_SECRET_KEY debe estar configurada antes de iniciar la aplicación");
        }
        return secretKey;
    }
}