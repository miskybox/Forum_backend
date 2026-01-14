# Auditoría de Permisos y Roles

## Fecha: 2024-12-17

## Resumen Ejecutivo

Se ha realizado una auditoría completa de los permisos y roles en el sistema. Se identificaron varios problemas de seguridad y se implementaron correcciones para mejorar la protección de los endpoints.

## Roles Definidos

El sistema utiliza tres roles principales:

1. **ROLE_USER**: Usuario registrado (rol por defecto)
2. **ROLE_MODERATOR**: Moderador del foro
3. **ROLE_ADMIN**: Administrador del sistema

## Problemas Identificados y Corregidos

### 1. Falta de @PreAuthorize en Endpoints de Modificación

**Problema**: Varios endpoints de modificación (POST, PUT, DELETE) no tenían anotaciones `@PreAuthorize`, confiando únicamente en la configuración de `SecurityConfig`.

**Impacto**: Medio - Aunque `SecurityConfig` requiere autenticación, la falta de `@PreAuthorize` hace que la seguridad sea menos explícita y más difícil de mantener.

**Correcciones Aplicadas**:

#### ForumController
- ✅ `POST /api/forums` - Agregado `@PreAuthorize("isAuthenticated()")`
- ✅ `PUT /api/forums/{id}` - Agregado `@PreAuthorize("isAuthenticated()")`
- ✅ `DELETE /api/forums/{id}` - Agregado `@PreAuthorize("isAuthenticated()")`
- ✅ `POST /api/forums/{id}/image` - Agregado `@PreAuthorize("isAuthenticated()")`
- ✅ `GET /api/forums/user` - Agregado `@PreAuthorize("isAuthenticated()")`
- ✅ `PUT /api/forums/{id}/status` - Agregado `@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")`

#### PostController
- ✅ `POST /api/posts` - Agregado `@PreAuthorize("isAuthenticated()")`
- ✅ `PUT /api/posts/{id}` - Agregado `@PreAuthorize("isAuthenticated()")`
- ✅ `DELETE /api/posts/{id}` - Agregado `@PreAuthorize("isAuthenticated()")`
- ✅ `POST /api/posts/{id}/images` - Agregado `@PreAuthorize("isAuthenticated()")`
- ✅ `DELETE /api/posts/{postId}/images/{imageId}` - Agregado `@PreAuthorize("isAuthenticated()")`
- ✅ `GET /api/posts/user` - Agregado `@PreAuthorize("isAuthenticated()")`

#### CommentController
- ✅ `POST /api/comments/post/{postId}` - Agregado `@PreAuthorize("isAuthenticated()")`
- ✅ `PUT /api/comments/{id}` - Agregado `@PreAuthorize("isAuthenticated()")`
- ✅ `DELETE /api/comments/{id}` - Agregado `@PreAuthorize("isAuthenticated()")`

### 2. Verificación de Roles en Servicios

**Estado**: ✅ Correcto

Los servicios implementan correctamente la verificación de permisos:

- `ForumServiceImpl`: Usa `assertOwnershipOrAdmin()` que verifica propiedad o roles ADMIN/MODERATOR
- `PostServiceImpl`: Usa `assertOwnershipOrAdmin()` que verifica propiedad o roles ADMIN/MODERATOR
- `CommentServiceImpl`: Verifica propiedad o roles ADMIN/MODERATOR antes de eliminar
- `UserServiceImpl`: Verifica roles ADMIN antes de operaciones administrativas

### 3. Endpoints Protegidos Correctamente

#### Solo ADMIN
- ✅ `GET /api/users` - `@PreAuthorize("hasRole('ADMIN')")`
- ✅ `POST /api/users` - `@PreAuthorize("hasRole('ADMIN')")`
- ✅ `DELETE /api/users/{id}` - `@PreAuthorize("hasRole('ADMIN')")`
- ✅ `PUT /api/users/{id}/roles` - `@PreAuthorize("hasRole('ADMIN')")`
- ✅ `POST /api/categories` - `@PreAuthorize("hasRole('ADMIN')")`
- ✅ `PUT /api/categories/{id}` - `@PreAuthorize("hasRole('ADMIN')")`
- ✅ `DELETE /api/categories/{id}` - `@PreAuthorize("hasRole('ADMIN')")`
- ✅ `POST /api/roles` - `@PreAuthorize("hasRole('ADMIN')")`
- ✅ `PUT /api/roles/{id}` - `@PreAuthorize("hasRole('ADMIN')")`
- ✅ `DELETE /api/roles/{id}` - `@PreAuthorize("hasRole('ADMIN')")`
- ✅ `DELETE /api/tags/{id}` - `@PreAuthorize("hasRole('ADMIN')")`

#### ADMIN o MODERATOR
- ✅ `PUT /api/users/{id}/status` - `@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")`
- ✅ `PUT /api/forums/{id}/status` - `@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")`
- ✅ `POST /api/tags` - `@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")`
- ✅ `PUT /api/tags/{id}` - `@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")`

### 4. Endpoints de Mapa de Viajes y Trivia sin `@PreAuthorize`

**Problema**: Los controladores `VisitedPlaceController` y `TriviaController` dependían solo de la configuración global de seguridad y de parámetros `@AuthenticationPrincipal`, pero no tenían anotaciones `@PreAuthorize`. Esto permitía que solicitudes sin token llegaran al método, provocando `NullPointerException` o respuestas 500 antes de que Spring Security rechazara la petición.

**Impacto**: Alto - Exposición de endpoints sensibles (crear/actualizar lugares y partidas) a accesos no autenticados y potencial revelación de información sensible a través de mensajes de error.

**Correcciones Aplicadas**:
- ✅ `POST /api/travel/places`, `PUT /api/travel/places/{placeId}`, `DELETE /api/travel/places/{placeId}` - agregado `@PreAuthorize("isAuthenticated()")`
- ✅ Endpoints `/api/travel/my-*`, `/api/travel/places/{placeId}/favorite`, `/api/travel/users/{userId}/stats`, `/api/travel/check/{countryId}` - agregado `@PreAuthorize("isAuthenticated()")`
- ✅ Endpoints `/api/trivia/games*`, `/api/trivia/my-*`, `/api/trivia/users/{userId}/score` - agregado `@PreAuthorize("isAuthenticated()")`

## Matriz de Permisos

| Endpoint | USER | MODERATOR | ADMIN | Público |
|----------|------|-----------|-------|---------|
| GET /api/categories | ✅ | ✅ | ✅ | ✅ |
| POST /api/categories | ❌ | ❌ | ✅ | ❌ |
| PUT /api/categories/{id} | ❌ | ❌ | ✅ | ❌ |
| DELETE /api/categories/{id} | ❌ | ❌ | ✅ | ❌ |
| GET /api/forums | ✅ | ✅ | ✅ | ✅ |
| POST /api/forums | ✅ | ✅ | ✅ | ❌ |
| PUT /api/forums/{id} | ✅* | ✅ | ✅ | ❌ |
| DELETE /api/forums/{id} | ✅* | ✅ | ✅ | ❌ |
| PUT /api/forums/{id}/status | ❌ | ✅ | ✅ | ❌ |
| GET /api/posts | ✅ | ✅ | ✅ | ✅ |
| POST /api/posts | ✅ | ✅ | ✅ | ❌ |
| PUT /api/posts/{id} | ✅* | ✅ | ✅ | ❌ |
| DELETE /api/posts/{id} | ✅* | ✅ | ✅ | ❌ |
| GET /api/comments | ✅ | ✅ | ✅ | ✅ |
| POST /api/comments | ✅ | ✅ | ✅ | ❌ |
| PUT /api/comments/{id} | ✅* | ✅ | ✅ | ❌ |
| DELETE /api/comments/{id} | ✅* | ✅ | ✅ | ❌ |
| GET /api/users | ❌ | ❌ | ✅ | ❌ |
| POST /api/users | ❌ | ❌ | ✅ | ❌ |
| PUT /api/users/{id} | ✅* | ✅* | ✅ | ❌ |
| DELETE /api/users/{id} | ❌ | ❌ | ✅ | ❌ |
| PUT /api/users/{id}/status | ❌ | ✅ | ✅ | ❌ |

*Solo si es el propietario del recurso

## Recomendaciones

### Implementadas ✅
1. ✅ Agregar `@PreAuthorize` en todos los endpoints de modificación
2. ✅ Proteger endpoints de estado con roles específicos (ADMIN/MODERATOR)
3. ✅ Documentar matriz de permisos

### Pendientes (Opcionales)
1. ⚠️ Considerar crear una clase de constantes para roles (`ROLE_ADMIN`, `ROLE_MODERATOR`, `ROLE_USER`)
2. ⚠️ Implementar tests de integración para verificar permisos
3. ⚠️ Considerar usar `@PreAuthorize` con expresiones más complejas para verificación de propiedad

## Conclusión

Los permisos y roles están ahora correctamente configurados y documentados. Todos los endpoints críticos tienen protección adecuada mediante `@PreAuthorize` y verificación de permisos en los servicios. El sistema implementa correctamente el principio de menor privilegio, donde los usuarios solo pueden modificar sus propios recursos, excepto los administradores y moderadores que tienen permisos adicionales.

## Notas Técnicas

- Spring Security automáticamente agrega el prefijo `ROLE_` cuando se usa `hasRole()`, por lo que `hasRole('ADMIN')` verifica `ROLE_ADMIN`
- Los servicios verifican manualmente los roles usando `authority.equals("ROLE_ADMIN")` porque trabajan directamente con `Authentication.getAuthorities()`
- La verificación de propiedad se realiza en los servicios, no en los controladores, para mantener la lógica de negocio centralizada

