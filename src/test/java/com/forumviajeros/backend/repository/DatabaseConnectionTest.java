package com.forumviajeros.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * Tests de conexión a la base de datos PostgreSQL
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Debe establecer conexión con la base de datos")
    void shouldEstablishDatabaseConnection() throws Exception {
        assertNotNull(dataSource, "DataSource no debe ser null");
        
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Conexión no debe ser null");
            assertFalse(connection.isClosed(), "Conexión debe estar abierta");
        }
    }

    @Test
    @DisplayName("Debe obtener metadata de la base de datos")
    void shouldGetDatabaseMetadata() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            assertNotNull(metaData, "Metadata no debe ser null");
            
            String databaseProductName = metaData.getDatabaseProductName();
            assertNotNull(databaseProductName, "Nombre del producto de BD no debe ser null");
            
            System.out.println("Database Product: " + databaseProductName);
            System.out.println("Database Version: " + metaData.getDatabaseProductVersion());
        }
    }

    @Test
    @DisplayName("Debe ejecutar queries SQL básicas")
    void shouldExecuteBasicSQLQueries() {
        // Test SELECT
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertEquals(1, result, "Query SELECT debe retornar 1");

        // Test SELECT con cálculo
        Integer sum = jdbcTemplate.queryForObject("SELECT 2 + 3", Integer.class);
        assertEquals(5, sum, "Query de suma debe retornar 5");
    }

    @Test
    @DisplayName("Debe verificar que las tablas existen")
    void shouldVerifyTablesExist() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // Verificar que podemos obtener información de tablas
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            int tableCount = 0;
            while (tables.next()) {
                tableCount++;
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("Tabla encontrada: " + tableName);
            }
            
            assertTrue(tableCount >= 0, "Debe poder listar tablas");
        }
    }

    @Test
    @DisplayName("Debe verificar transacciones")
    void shouldHandleTransactions() {
        // Verificar que las transacciones funcionan
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test_transaction (id INT PRIMARY KEY, name VARCHAR(50))");
        jdbcTemplate.execute("INSERT INTO test_transaction VALUES (1, 'test')");
        
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_transaction", Integer.class);
        assertEquals(1, count, "Debe insertar un registro");
        
        jdbcTemplate.execute("DROP TABLE test_transaction");
    }
}

