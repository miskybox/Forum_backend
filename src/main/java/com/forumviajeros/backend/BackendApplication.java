package com.forumviajeros.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

	public static final String DB_URL = "DB_URL";
	public static final String DB_USER = "DB_USER";
	public static final String JWT_SECRET_KEY = "JWT_SECRET_KEY";

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		if (dotenv.get(DB_URL) == null || dotenv.get(DB_USER) == null) {
			System.err.println("⚠️ Variables de entorno esenciales no están definidas correctamente.");
		}

		System.setProperty(DB_URL, dotenv.get(DB_URL, ""));
		System.setProperty(DB_USER, dotenv.get(DB_USER, ""));
		System.setProperty(JWT_SECRET_KEY, dotenv.get(JWT_SECRET_KEY, ""));
		System.setProperty("spring.application.name", dotenv.get("SPRING_APPLICATION_NAME", "backend"));

		String activeProfile = dotenv.get("SPRING_PROFILES_ACTIVE");
		if (activeProfile != null && !activeProfile.isBlank()) {
			System.setProperty("spring.profiles.active", activeProfile);
		}

		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner logEnvironment(Environment env) {
		return args -> {
			System.out.println("====== Environment Variables ======");
			System.out.println("DB_URL: " + env.getProperty("spring.datasource.url"));
			System.out.println("JWT Secret available: " + (env.getProperty(JWT_SECRET_KEY) != null));
			System.out.println("JWT Secret available: " + (env.getProperty("JWT_SECRET_KEY") != null));
			System.out.println("Active profiles: " + String.join(", ", env.getActiveProfiles()));
			System.out.println("==================================");
		};
	}

	@Bean
	public CommandLineRunner initUsers(UserRepository userRepository, RoleRepository roleRepository,
			BCryptPasswordEncoder encoder) {
		return args -> {
			Dotenv dotenv = Dotenv.load();

			Role adminRole = createRoleIfNotExists(roleRepository, "ROLE_ADMIN");
			Role userRole = createRoleIfNotExists(roleRepository, "ROLE_USER");

			System.out.println("Roles creados/verificados: ROLE_ADMIN, ROLE_USER");

			if (userRepository.findByUsername(dotenv.get("ADMIN_USERNAME")).isEmpty()) {
				User admin = User.builder()
						.username(dotenv.get("ADMIN_USERNAME"))
						.email(dotenv.get("ADMIN_EMAIL"))
						.password(encoder.encode(dotenv.get("ADMIN_PASSWORD")))
						.build();

				admin.getRoles().add(adminRole);
				userRepository.save(admin);
				System.out.println("Usuario administrador creado: " + dotenv.get("ADMIN_USERNAME"));
			} else {
				System.out.println("Usuario administrador ya existe: " + dotenv.get("ADMIN_USERNAME"));
			}

			if (userRepository.findByUsername(dotenv.get("USER_USERNAME")).isEmpty()) {
				User user = User.builder()
						.username(dotenv.get("USER_USERNAME"))
						.email(dotenv.get("USER_EMAIL"))
						.password(encoder.encode(dotenv.get("USER_PASSWORD")))
						.build();

				user.getRoles().add(userRole);
				userRepository.save(user);
				System.out.println("Usuario normal creado: " + dotenv.get("USER_USERNAME"));
			} else {
				System.out.println("Usuario normal ya existe: " + dotenv.get("USER_USERNAME"));
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