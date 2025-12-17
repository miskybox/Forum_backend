package com.forumviajeros.backend.security.constants;

import io.github.cdimascio.dotenv.Dotenv;

public class SecurityConstants {
    private static volatile String SECRET = null;
    private static final Object SECRET_LOCK = new Object();
    public static final long EXPIRATION_TIME = 600_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    private SecurityConstants() {
    }

    /**
     * Obtiene el secret key de JWT de forma lazy (solo cuando se necesita).
     * Esto permite que BackendApplication configure las variables de entorno primero.
     * 
     * @return El secret key para firmar tokens JWT
     * @throws IllegalStateException si el secret key no está configurado
     */
    public static String getSecret() {
        if (SECRET == null) {
            synchronized (SECRET_LOCK) {
                if (SECRET == null) {
                    SECRET = getSecretKey();
                }
            }
        }
        return SECRET;
    }

    private static String getSecretKey() {
        // Primero intentar desde System Property (configurado por BackendApplication)
        String secretKey = System.getProperty("JWT_SECRET_KEY");
        
        // Si no está, intentar desde variable de entorno del sistema
        if (secretKey == null || secretKey.isBlank()) {
            secretKey = System.getenv("JWT_SECRET_KEY");
        }
        
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
                    "La variable de entorno JWT_SECRET_KEY debe estar configurada. " +
                    "Configúrala en el archivo .env o como variable de entorno del sistema.");
        }
        
        // Validar que el secret tenga al menos 64 caracteres para seguridad
        if (secretKey.length() < 64) {
            throw new IllegalStateException(
                    "JWT_SECRET_KEY debe tener al menos 64 caracteres para seguridad. " +
                    "Longitud actual: " + secretKey.length());
        }
        
        return secretKey;
    }
}