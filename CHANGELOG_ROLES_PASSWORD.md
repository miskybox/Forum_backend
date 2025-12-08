# Changelog - Roles y Validación de Contraseñas

## [2024] - Implementación de Roles y Validación de Contraseñas

### ✨ Nuevas Funcionalidades

#### Sistema de Roles
- **ROLE_USER**: Rol por defecto para usuarios registrados
- **ROLE_MODERATOR**: Rol para moderadores del foro con permisos extendidos
- **ROLE_ADMIN**: Rol de administrador con acceso completo

#### Validación de Contraseñas
- Validador personalizado `@ValidPassword` implementado
- Requisitos de seguridad:
  - Mínimo 8 caracteres
  - Al menos una mayúscula
  - Al menos una minúscula
  - Al menos un carácter especial

### 🔧 Cambios

#### Servicios
- `ForumServiceImpl`: Moderadores pueden editar/eliminar cualquier foro
- `PostServiceImpl`: Moderadores pueden editar/eliminar cualquier post
- `CommentServiceImpl`: Moderadores pueden editar/eliminar cualquier comentario

#### DTOs
- `UserRegisterDTO`: Validación de contraseña en registro
- `ChangePasswordRequestDTO`: Validación de nueva contraseña
- `UserRequestDTO`: Validación de contraseña cuando se proporciona

#### Configuración
- `DataInitializer`: Inicialización automática de los 3 roles
- `BackendApplication`: Creación de roles con descripciones

### 🧪 Tests

#### Nuevos Tests
- `PasswordValidatorTest`: Tests unitarios del validador
- `PasswordValidatorIntegrationTest`: Tests de integración
- `RoleRepositoryTest`: Tests del repositorio de roles

### 📚 Documentación

#### Nuevos Documentos
- `ROLES_AND_PERMISSIONS.md`: Matriz completa de permisos
- `PASSWORD_VALIDATION.md`: Guía de validación de contraseñas
- `IMPLEMENTATION_SUMMARY.md`: Resumen de implementación
- `CHANGELOG_ROLES_PASSWORD.md`: Este archivo

### 🔒 Seguridad

- Validación de contraseñas en frontend y backend
- Encriptación con BCrypt
- Control de acceso basado en roles
- Verificación de propiedad de recursos

### 📝 Notas

- Los roles se crean automáticamente al iniciar la aplicación
- Los usuarios nuevos reciben `ROLE_USER` por defecto
- Solo los administradores pueden asignar roles `ROLE_MODERATOR` y `ROLE_ADMIN`
- La validación de contraseñas se aplica automáticamente con `@Valid`

