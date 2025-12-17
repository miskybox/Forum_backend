# 🧪 GUÍA DE TESTING

**Proyecto:** Forum Viajeros  
**Última actualización:** 2025-12-15

---

## 📋 ESTRUCTURA DE TESTS

### Tests Existentes

#### Tests de Repositorio (9 suites)
- `CategoryRepositoryTest`
- `CommentRepositoryTest`
- `ForumRepositoryTest`
- `PostRepositoryTest`
- `RoleRepositoryTest`
- `UserRepositoryTest`
- `DatabaseConnectionTest`
- `PostgreSQLConfigTest`

#### Tests de Validación (2 suites)
- `PasswordValidatorTest`
- `PasswordValidatorIntegrationTest`

#### Tests de Servicios (3 suites - modelos/DTOs)
- `CountryServiceTest`
- `TriviaServiceTest`
- `VisitedPlaceServiceTest`

#### Tests de Servicios (1 suite - ejemplo)
- `AuthServiceTest` - **Ejemplo de test de servicio con mocking**

---

## 🎯 EJEMPLO: TEST DE SERVICIO

Se ha creado `AuthServiceTest.java` como ejemplo de cómo estructurar tests de servicios:

### Características del Ejemplo

1. **Mocking con Mockito**
   - `@Mock` para dependencias
   - `@InjectMocks` para el servicio a probar
   - `@ExtendWith(MockitoExtension.class)`

2. **Casos de Prueba**
   - Registro exitoso
   - Registro con username existente
   - Registro con email existente
   - Login exitoso
   - Login con credenciales inválidas
   - Login con usuario no encontrado
   - Login con email

3. **Patrón AAA**
   - **Arrange:** Configurar mocks y datos
   - **Act:** Ejecutar el método
   - **Assert:** Verificar resultados

### Ejemplo de Estructura

```java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @InjectMocks
    private AuthServiceImpl authService;
    
    @Test
    @DisplayName("Registro exitoso de nuevo usuario")
    void register_ShouldSucceed_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByUsername(...)).thenReturn(Optional.empty());
        
        // Act
        UserResponseDTO result = authService.register(registerDTO);
        
        // Assert
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }
}
```

---

## 📝 TESTS PENDIENTES DE CREAR

### Servicios (Alta Prioridad)
- [ ] `ForumServiceTest` - CRUD de foros
- [ ] `PostServiceTest` - CRUD de posts
- [ ] `CommentServiceTest` - CRUD de comentarios
- [ ] `UserServiceTest` - Gestión de usuarios

### Controladores (Media Prioridad)
- [ ] `AuthControllerTest` - Endpoints de autenticación
- [ ] `ForumControllerTest` - Endpoints de foros
- [ ] `UserControllerTest` - Endpoints de usuarios

### Integración (Baja Prioridad)
- [ ] Tests E2E con backend real
- [ ] Tests de flujos completos

---

## 🔧 CONFIGURACIÓN DE TESTS

### Perfil de Test
- **Base de datos:** H2 en memoria
- **Configuración:** `application-test.properties`
- **Logging:** DEBUG para tests

### Dependencias Necesarias
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 📊 COBERTURA ACTUAL

### Estimada
- **Repositorios:** ~60%
- **Validación:** ~80%
- **Servicios:** ~20% (solo modelos)
- **Controladores:** ~0%

### Objetivo
- **Repositorios:** 80%+
- **Servicios:** 70%+
- **Controladores:** 60%+
- **Total:** 70%+

---

## 🎯 MEJORES PRÁCTICAS

### 1. Naming Conventions
- Métodos: `methodName_ShouldDoSomething_WhenCondition`
- DisplayName: Descripción clara en español

### 2. Estructura
- `@BeforeEach` para setup común
- Un test = un escenario
- Tests independientes

### 3. Assertions
- Usar assertions específicas
- Verificar comportamiento, no implementación
- Verificar interacciones con mocks

### 4. Mocking
- Mock solo dependencias externas
- No mockear el código bajo prueba
- Verificar interacciones importantes

---

## 📚 RECURSOS

### Documentación
- [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

### Ejemplos en el Proyecto
- `AuthServiceTest.java` - Test de servicio con mocking
- `PasswordValidatorTest.java` - Test de validación
- `UserRepositoryTest.java` - Test de repositorio

---

**Nota:** El test `AuthServiceTest.java` es un ejemplo y puede necesitar ajustes según la estructura exacta del proyecto. Úsalo como referencia para crear otros tests de servicios.

