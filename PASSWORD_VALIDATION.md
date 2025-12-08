# Validación de Contraseñas - Forum Viajeros

## Requisitos de Contraseña

Todos los usuarios deben cumplir con los siguientes requisitos al crear o cambiar su contraseña:

### Requisitos Mínimos

1. **Longitud mínima**: 8 caracteres
2. **Mayúscula**: Al menos una letra mayúscula (A-Z)
3. **Minúscula**: Al menos una letra minúscula (a-z)
4. **Carácter especial**: Al menos un carácter especial de la siguiente lista:
   - `! @ # $ % ^ & * ( ) _ + - = [ ] { } | ; : , . < > ?`

### Ejemplos de Contraseñas Válidas

✅ `Password123!`  
✅ `MiContraseña2024@`  
✅ `Secure#Pass1`  
✅ `TravelForum$2024`  
✅ `User123!abc`

### Ejemplos de Contraseñas Inválidas

❌ `password` - Falta mayúscula y carácter especial  
❌ `PASSWORD123!` - Falta minúscula  
❌ `Password123` - Falta carácter especial  
❌ `Pass1!` - Menos de 8 caracteres  
❌ `password123!` - Falta mayúscula

## Implementación

### Validador Personalizado

Se ha creado un validador personalizado `@ValidPassword` que verifica automáticamente todos los requisitos:

```java
@ValidPassword
private String password;
```

### Endpoints que Validan Contraseñas

1. **Registro de Usuario**
   - Endpoint: `POST /api/auth/register`
   - DTO: `UserRegisterDTO`
   - Validación: Automática mediante `@ValidPassword`

2. **Cambio de Contraseña**
   - Endpoint: `PUT /api/users/{id}/change-password`
   - DTO: `ChangePasswordRequestDTO`
   - Validación: Automática mediante `@ValidPassword` en `newPassword`

### Mensajes de Error

El validador proporciona mensajes específicos para cada requisito no cumplido:

- **Longitud insuficiente**: "La contraseña debe tener al menos 8 caracteres"
- **Sin mayúscula**: "La contraseña debe contener al menos una letra mayúscula"
- **Sin minúscula**: "La contraseña debe contener al menos una letra minúscula"
- **Sin carácter especial**: "La contraseña debe contener al menos un carácter especial (!@#$%^&*()_+-=[]{}|;:,.<>?)"

## Seguridad

- Las contraseñas se almacenan encriptadas usando **BCrypt**
- La validación se realiza tanto en el **frontend** como en el **backend**
- Las contraseñas nunca se exponen en los logs o respuestas de la API
- El cambio de contraseña requiere la contraseña actual

## Tests

Se han creado tests unitarios para el validador de contraseñas en:
- `src/test/java/com/forumviajeros/backend/validation/PasswordValidatorTest.java`

Los tests verifican:
- Contraseñas válidas
- Contraseñas con menos de 8 caracteres
- Contraseñas sin mayúsculas
- Contraseñas sin minúsculas
- Contraseñas sin caracteres especiales
- Contraseñas null o vacías
- Diferentes tipos de caracteres especiales

