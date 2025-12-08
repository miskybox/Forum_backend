package com.forumviajeros.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.forumviajeros.backend.model.Category;
import com.forumviajeros.backend.model.Role;
import com.forumviajeros.backend.repository.CategoryRepository;
import com.forumviajeros.backend.repository.RoleRepository;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private static final String CONTINENT_TYPE = "CONTINENT";

    @Bean
    public CommandLineRunner initData(CategoryRepository categoryRepository, RoleRepository roleRepository) {
        return args -> {

            initRoles(roleRepository);

            initCategories(categoryRepository);
        };
    }

    private void initRoles(RoleRepository roleRepository) {
        logger.info("Iniciando creación de roles...");

        // Crear ROLE_USER (Usuario Registrado)
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            logger.info("Creando rol ROLE_USER");
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole.setDescription("Rol por defecto para usuarios registrados");
            roleRepository.save(userRole);
            logger.info("Rol ROLE_USER creado exitosamente");
        } else {
            logger.info("Rol ROLE_USER ya existe, no es necesario crearlo");
        }

        // Crear ROLE_MODERATOR (Moderador)
        if (roleRepository.findByName("ROLE_MODERATOR").isEmpty()) {
            logger.info("Creando rol ROLE_MODERATOR");
            Role moderatorRole = new Role();
            moderatorRole.setName("ROLE_MODERATOR");
            moderatorRole.setDescription("Rol para moderadores del foro");
            roleRepository.save(moderatorRole);
            logger.info("Rol ROLE_MODERATOR creado exitosamente");
        } else {
            logger.info("Rol ROLE_MODERATOR ya existe, no es necesario crearlo");
        }

        // Crear ROLE_ADMIN (Administrador)
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            logger.info("Creando rol ROLE_ADMIN");
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setDescription("Rol para administradores del sistema");
            roleRepository.save(adminRole);
            logger.info("Rol ROLE_ADMIN creado exitosamente");
        } else {
            logger.info("Rol ROLE_ADMIN ya existe, no es necesario crearlo");
        }

        logger.info("Total de roles disponibles en el sistema: {}", roleRepository.count());
    }

    private void initCategories(CategoryRepository categoryRepository) {

        if (categoryRepository.count() == 0) {
            logger.info("Iniciando creación de categorías...");

            Category general = new Category();
            general.setName("General");
            general.setDescription("Discusiones generales sobre viajes");
            general.setType("GENERAL");
            categoryRepository.save(general);

            Category europe = new Category();
            europe.setName("Europa");
            europe.setDescription("Viajes por Europa");
            europe.setType(CONTINENT_TYPE);
            categoryRepository.save(europe);

            Category asia = new Category();
            asia.setName("Asia");
            asia.setDescription("Viajes por Asia");
            asia.setType(CONTINENT_TYPE);
            categoryRepository.save(asia);

            Category northAmerica = new Category();
            northAmerica.setName("América del Norte");
            northAmerica.setDescription("Viajes por América del Norte");
            northAmerica.setType(CONTINENT_TYPE);
            categoryRepository.save(northAmerica);

            Category southAmerica = new Category();
            southAmerica.setName("América del Sur");
            southAmerica.setDescription("Viajes por América del Sur");
            southAmerica.setType(CONTINENT_TYPE);
            categoryRepository.save(southAmerica);

            Category africa = new Category();
            africa.setName("África");
            africa.setDescription("Viajes por África");
            africa.setType(CONTINENT_TYPE);
            categoryRepository.save(africa);

            Category oceania = new Category();
            oceania.setName("Oceanía");
            oceania.setDescription("Viajes por Oceanía");
            oceania.setType(CONTINENT_TYPE);
            categoryRepository.save(oceania);

            Category antarctica = new Category();
            antarctica.setName("Antártida");
            antarctica.setDescription("Viajes por la Antártida");
            antarctica.setType(CONTINENT_TYPE);
            categoryRepository.save(antarctica);

            logger.info("Categorías creadas exitosamente. Total: {}", categoryRepository.count());
        } else {
            logger.info("Las categorías ya existen, no es necesario crearlas. Total: {}", categoryRepository.count());
        }
    }
}