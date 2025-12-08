# Tests de PostgreSQL - Forum Viajeros

## Resumen

Se han creado tests de integración para verificar la conexión y operaciones con PostgreSQL en el backend.

## Tests Creados

### 1. DatabaseConnectionTest
- **Ubicación**: `src/test/java/com/forumviajeros/backend/repository/DatabaseConnectionTest.java`
- **Propósito**: Verificar la conexión básica a la base de datos
- **Tests**:
  - Establecer conexión con la base de datos
  - Obtener metadata de la base de datos
  - Ejecutar queries SQL básicas
  - Verificar que las tablas existen
  - Verificar transacciones

### 2. PostgreSQLConfigTest
- **Ubicación**: `src/test/java/com/forumviajeros/backend/repository/PostgreSQLConfigTest.java`
- **Propósito**: Verificar la configuración de PostgreSQL
- **Tests**:
  - Configuración de DataSource
  - Dialecto PostgreSQL compatible
  - Soporte de tipos de datos PostgreSQL
  - Configuración de JPA

### 3. UserRepositoryTest
- **Ubicación**: `src/test/java/com/forumviajeros/backend/repository/UserRepositoryTest.java`
- **Propósito**: Tests de integración para operaciones CRUD de usuarios
- **Tests**:
  - Guardar usuario
  - Buscar por username
  - Buscar por email
  - Buscar por username o email
  - Verificar existencia de username/email
  - Actualizar usuario
  - Eliminar usuario
  - Unicidad de username y email

### 4. ForumRepositoryTest
- **Ubicación**: `src/test/java/com/forumviajeros/backend/repository/ForumRepositoryTest.java`
- **Propósito**: Tests de integración para operaciones CRUD de foros
- **Tests**:
  - Guardar foro
  - Buscar por usuario
  - Buscar por categoría
  - Buscar por keyword
  - Paginación
  - Filtrar por categoría y estado
  - Actualizar foro
  - Eliminar foro
  - Relaciones con usuario y categoría

### 5. PostRepositoryTest
- **Ubicación**: `src/test/java/com/forumviajeros/backend/repository/PostRepositoryTest.java`
- **Propósito**: Tests de integración para operaciones CRUD de posts
- **Tests**:
  - Guardar post
  - Buscar por foro
  - Buscar por usuario
  - Actualizar post
  - Eliminar post
  - Relaciones con foro y usuario
  - Incrementar viewCount

### 6. CategoryRepositoryTest
- **Ubicación**: `src/test/java/com/forumviajeros/backend/repository/CategoryRepositoryTest.java`
- **Propósito**: Tests de integración para operaciones CRUD de categorías
- **Tests**:
  - Guardar categoría
  - Buscar por nombre
  - Buscar por tipo
  - Verificar existencia de nombre
  - Actualizar categoría
  - Eliminar categoría
  - Unicidad de nombre

### 7. CommentRepositoryTest
- **Ubicación**: `src/test/java/com/forumviajeros/backend/repository/CommentRepositoryTest.java`
- **Propósito**: Tests de integración para operaciones CRUD de comentarios
- **Tests**:
  - Guardar comentario
  - Buscar por post
  - Actualizar comentario
  - Eliminar comentario
  - Relaciones con post y usuario

## Configuración

Los tests usan H2 en modo PostgreSQL para simular el comportamiento de PostgreSQL sin necesidad de una instancia real. Esto permite:

- Ejecutar tests rápidamente
- No requerir configuración de base de datos externa
- Simular comportamiento de PostgreSQL

### Configuración de Test

```properties
spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```

## Ejecutar Tests

### Todos los tests de repositorio:
```bash
mvn test -Dtest="*RepositoryTest"
```

### Test específico:
```bash
mvn test -Dtest=DatabaseConnectionTest
mvn test -Dtest=UserRepositoryTest
mvn test -Dtest=ForumRepositoryTest
```

### Todos los tests:
```bash
mvn test
```

## Cobertura

Los tests cubren:
- ✅ Conexión a base de datos
- ✅ Operaciones CRUD básicas
- ✅ Búsquedas y filtros
- ✅ Relaciones entre entidades
- ✅ Validaciones de unicidad
- ✅ Paginación
- ✅ Transacciones

## Notas

- Los tests usan `@DataJpaTest` para cargar solo el contexto de JPA
- Se usa `TestEntityManager` para operaciones de persistencia en tests
- Los tests son independientes y se ejecutan en orden aleatorio
- Cada test limpia la base de datos después de ejecutarse

