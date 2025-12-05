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

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		if (dotenv.get(DB_URL) == null || dotenv.get(DB_USER) == null) {
			LOGGER.warn("Essential environment variables are not defined correctly.");
		}

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

			Role adminRole = createRoleIfNotExists(roleRepository, "ROLE_ADMIN");
			Role userRole = createRoleIfNotExists(roleRepository, "ROLE_USER");

			LOGGER.info("Roles creados/verificados: ROLE_ADMIN, ROLE_USER");

			String adminUsername = dotenv.get(ADMIN_USERNAME_KEY);
			String adminEmail = dotenv.get(ADMIN_EMAIL_KEY);
			String adminPassword = dotenv.get(ADMIN_PASSWORD_KEY);
			String userUsername = dotenv.get(USER_USERNAME_KEY);
			String userEmail = dotenv.get(USER_EMAIL_KEY);
			String userPassword = dotenv.get(USER_PASSWORD_KEY);

			if (userRepository.findByUsername(adminUsername).isEmpty()) {
				User admin = User.builder()
						.username(adminUsername)
						.email(adminEmail)
						.password(encoder.encode(adminPassword))
						.build();

				admin.getRoles().add(adminRole);
				userRepository.save(admin);
				LOGGER.info("Usuario administrador creado: {}", adminUsername);
			} else {
				LOGGER.info("Usuario administrador ya existe: {}", adminUsername);
			}

			if (userRepository.findByUsername(userUsername).isEmpty()) {
				User user = User.builder()
						.username(userUsername)
						.email(userEmail)
						.password(encoder.encode(userPassword))
						.build();

				user.getRoles().add(userRole);
				userRepository.save(user);
				LOGGER.info("Usuario normal creado: {}", userUsername);
			} else {
				LOGGER.info("Usuario normal ya existe: {}", userUsername);
			}
		};
	}

	private Role createRoleIfNotExists(RoleRepository roleRepository, String roleName) {
		return roleRepository.findByName(roleName)
				.orElseGet(() -> {
					Role role = new Role();
					role.setName(roleName);
					return roleRepository.save(role);
				});
	}
}