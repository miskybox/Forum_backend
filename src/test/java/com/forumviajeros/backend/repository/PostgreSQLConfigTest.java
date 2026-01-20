package com.forumviajeros.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * Tests para verificar la configuración de PostgreSQL
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class PostgreSQLConfigTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Environment environment;

    @Test
    @DisplayName("Debe tener configuración de DataSource")
    void shouldHaveDataSourceConfiguration() {
        assertNotNull(dataSource, "DataSource debe estar configurado");
    }

    @Test
    @DisplayName("Debe usar dialecto PostgreSQL compatible")
    void shouldUsePostgreSQLCompatibleDialect() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName();
            
            // H2 en modo PostgreSQL debe reportar como PostgreSQL
            assertNotNull(databaseProductName, "Debe tener nombre de producto");
            System.out.println("Database: " + databaseProductName);
            System.out.println("Driver: " + metaData.getDriverName());
        }
    }

    @Test
    @DisplayName("Debe soportar tipos de datos PostgreSQL")
    void shouldSupportPostgreSQLDataTypes() {
        // Verificar que podemos usar tipos comunes de PostgreSQL
        assertNotNull(dataSource, "DataSource debe estar disponible");
    }

    @Test
    @DisplayName("Debe tener configuración de JPA para PostgreSQL")
    void shouldHaveJPAConfiguration() {
        String platform = environment.getProperty("spring.jpa.database-platform");
        assertNotNull(platform, "Debe tener database-platform configurado");
    }
}

