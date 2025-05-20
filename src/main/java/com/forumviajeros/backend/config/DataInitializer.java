package com.forumviajeros.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.forumviajeros.backend.model.Category;
import com.forumviajeros.backend.repository.CategoryRepository;

@Configuration
public class DataInitializer {

    private static final String CONTINENT_TYPE = "CONTINENT";

    @Bean
    public CommandLineRunner initCategories(CategoryRepository categoryRepository) {
        return args -> {

            if (categoryRepository.count() == 0) {
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
                antarctica.setType("CONTINENT");
                categoryRepository.save(antarctica);
            }
        };
    }
}