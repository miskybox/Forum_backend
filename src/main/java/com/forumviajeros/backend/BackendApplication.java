package com.forumviajeros.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.forumviajeros.backend.model.Role;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.repository.RoleRepository;
import com.forumviajeros.backend.repository.UserRepository;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class BackendApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(BackendApplication.class);

	public static final String DB_URL = "DB_URL";
	public static final String DB_USER = "DB_USER";
	public static final String DB_PASSWORD = "DB_PASSWORD";
	public static final String JWT_SECRET_KEY = "JWT_SECRET_KEY";
	public static final String SPRING_APPLICATION_NAME = "SPRING_APPLICATION_NAME";
	public static final String SPRING_PROFILES_ACTIVE = "SPRING_PROFILES_ACTIVE";
	public static final String ADMIN_USERNAME_KEY = "ADMIN_USERNAME";
	public static final String ADMIN_EMAIL_KEY = "ADMIN_EMAIL";
	public static final String ADMIN_PASSWORD_KEY = "ADMIN_PASSWORD";
	public static final String USER_USERNAME_KEY = "USER_USERNAME";
	public static final String USER_EMAIL_KEY = "USER_EMAIL";
	public static final String USER_PASSWORD_KEY = "USER_PASSWORD";
	public static final String MODERATOR_USERNAME_KEY = "MODERATOR_USERNAME";
	public static final String MODERATOR_EMAIL_KEY = "MODERATOR_EMAIL";
	public static final String MODERATOR_PASSWORD_KEY = "MODERATOR_PASSWORD";

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		// Validar variables de entorno críticas antes de iniciar
		validateEnvironmentVariables(dotenv);

		// Configurar variables de entorno como System Properties
		System.setProperty(DB_URL, dotenv.get(DB_URL, ""));
		System.setProperty(DB_USER, dotenv.get(DB_USER, ""));
		System.setProperty(DB_PASSWORD, dotenv.get(DB_PASSWORD, ""));
		System.setProperty(JWT_SECRET_KEY, dotenv.get(JWT_SECRET_KEY, ""));
		System.setProperty("spring.application.name", dotenv.get(SPRING_APPLICATION_NAME, "backend"));

		String activeProfile = dotenv.get(SPRING_PROFILES_ACTIVE);
		if (activeProfile != null && !activeProfile.isBlank()) {
			System.setProperty("spring.profiles.active", activeProfile);
		}

		SpringApplication.run(BackendApplication.class, args);
	}

	/**
	 * Valida que todas las variables de entorno críticas estén configuradas.
	 * 
	 * @param dotenv El objeto Dotenv cargado
	 * @throws IllegalStateException si alguna variable crítica falta o es inválida
	 */
	private static void validateEnvironmentVariables(Dotenv dotenv) {
		LOGGER.info("Validando variables de entorno...");
		
		boolean hasErrors = false;
		StringBuilder errors = new StringBuilder();
		
		// Validar DB_URL
		String dbUrl = dotenv.get(DB_URL);
		if (dbUrl == null || dbUrl.isBlank()) {
			errors.append("\n  - DB_URL: Requerida (ej: jdbc:postgresql://localhost:5432/forum_viajeros)");
			hasErrors = true;
		} else if (!dbUrl.startsWith("jdbc:")) {
			errors.append("\n  - DB_URL: Formato inválido (debe empezar con 'jdbc:')");
			hasErrors = true;
		}
		
		// Validar DB_USER
		String dbUser = dotenv.get(DB_USER);
		if (dbUser == null || dbUser.isBlank()) {
			errors.append("\n  - DB_USER: Requerida (ej: postgres)");
			hasErrors = true;
		}
		
		// Validar DB_PASSWORD - puede ser vacía para desarrollo local
		String dbPassword = dotenv.get(DB_PASSWORD);
		// DB_PASSWORD es opcional - puede estar vacía para bases de datos sin contraseña
		if (dbPassword == null) {
			LOGGER.info("⚠️ DB_PASSWORD no está configurada - usando conexión sin contraseña");
		}
		
		// Validar JWT_SECRET_KEY
		String jwtSecret = dotenv.get(JWT_SECRET_KEY);
		if (jwtSecret == null || jwtSecret.isBlank()) {
			errors.append("\n  - JWT_SECRET_KEY: Requerida (mínimo 64 caracteres)");
			hasErrors = true;
		} else if (jwtSecret.length() < 64) {
			errors.append("\n  - JWT_SECRET_KEY: Debe tener al menos 64 caracteres (longitud actual: ")
					.append(jwtSecret.length()).append(")");
			hasErrors = true;
		}
		
		if (hasErrors) {
			String errorMessage = "Variables de entorno críticas faltantes o inválidas:" + errors.toString() +
					"\n\nPor favor, configura estas variables en el archivo .env o como variables de entorno del sistema.";
			LOGGER.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
		
		LOGGER.info("✅ Todas las variables de entorno críticas están configuradas correctamente");
	}

	@Bean
	@ConditionalOnProperty(name = "app.init.users.enabled", havingValue = "true", matchIfMissing = true)
	public CommandLineRunner logEnvironment(Environment env) {
		return args -> {
			LOGGER.info("====== Environment Variables ======");
			LOGGER.info("DB_URL: {}", env.getProperty("spring.datasource.url"));
			LOGGER.info("JWT secret available: {}", env.getProperty(JWT_SECRET_KEY) != null);
			LOGGER.info("Active profiles: {}", String.join(", ", env.getActiveProfiles()));
			LOGGER.info("==================================");
		};
	}

	@Bean
	@ConditionalOnProperty(name = "app.init.users.enabled", havingValue = "true", matchIfMissing = true)
	public CommandLineRunner initUsers(UserRepository userRepository, RoleRepository roleRepository,
			BCryptPasswordEncoder encoder) {
		return args -> {
			Dotenv dotenv = Dotenv.load();

		Role adminRole = createRoleIfNotExists(roleRepository, "ROLE_ADMIN", "Rol para administradores del sistema");
		Role moderatorRole = createRoleIfNotExists(roleRepository, "ROLE_MODERATOR", "Rol para moderadores del foro");
		Role userRole = createRoleIfNotExists(roleRepository, "ROLE_USER", "Rol por defecto para usuarios registrados");

		LOGGER.info("Roles creados/verificados: ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER");

			String adminUsername = dotenv.get(ADMIN_USERNAME_KEY);
			String adminEmail = dotenv.get(ADMIN_EMAIL_KEY);
			String adminPassword = dotenv.get(ADMIN_PASSWORD_KEY);
			String userUsername = dotenv.get(USER_USERNAME_KEY);
			String userEmail = dotenv.get(USER_EMAIL_KEY);
			String userPassword = dotenv.get(USER_PASSWORD_KEY);
			String moderatorUsername = dotenv.get(MODERATOR_USERNAME_KEY, "moderator");
			String moderatorEmail = dotenv.get(MODERATOR_EMAIL_KEY, "moderator@forumviajeros.com");
			String moderatorPassword = dotenv.get(MODERATOR_PASSWORD_KEY, "Moderator123!");

			// Crear usuario administrador
			if (adminUsername != null && !adminUsername.isBlank() && userRepository.findByUsername(adminUsername).isEmpty()) {
				User admin = User.builder()
						.username(adminUsername)
						.email(adminEmail)
						.password(encoder.encode(adminPassword))
						.build();

				admin.getRoles().add(adminRole);
				userRepository.save(admin);
				LOGGER.info("Usuario administrador creado: {}", adminUsername);
			} else if (adminUsername != null && !adminUsername.isBlank()) {
				LOGGER.info("Usuario administrador ya existe: {}", adminUsername);
			}

			// Crear usuario moderador
			if (userRepository.findByUsername(moderatorUsername).isEmpty()) {
				User moderator = User.builder()
						.username(moderatorUsername)
						.email(moderatorEmail)
						.password(encoder.encode(moderatorPassword))
						.build();

				moderator.getRoles().add(moderatorRole);
				userRepository.save(moderator);
				LOGGER.info("Usuario moderador creado: {}", moderatorUsername);
			} else {
				LOGGER.info("Usuario moderador ya existe: {}", moderatorUsername);
			}

			// Crear usuario normal
			if (userUsername != null && !userUsername.isBlank() && userRepository.findByUsername(userUsername).isEmpty()) {
				User user = User.builder()
						.username(userUsername)
						.email(userEmail)
						.password(encoder.encode(userPassword))
						.build();

				user.getRoles().add(userRole);
				userRepository.save(user);
				LOGGER.info("Usuario normal creado: {}", userUsername);
			} else if (userUsername != null && !userUsername.isBlank()) {
				LOGGER.info("Usuario normal ya existe: {}", userUsername);
			}
		};
	}

	// Método helper para crear roles (usado internamente)
	@SuppressWarnings("unused")
	private Role createRoleIfNotExists(RoleRepository roleRepository, String roleName) {
		return createRoleIfNotExists(roleRepository, roleName, null);
	}

	private Role createRoleIfNotExists(RoleRepository roleRepository, String roleName, String description) {
		return roleRepository.findByName(roleName)
				.orElseGet(() -> {
					Role role = new Role();
					role.setName(roleName);
					if (description != null) {
						role.setDescription(description);
					}
					return roleRepository.save(role);
				});
	}
}