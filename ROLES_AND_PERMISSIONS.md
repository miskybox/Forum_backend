# Roles y Permisos - Forum Viajeros

## Roles del Sistema

El sistema cuenta con tres roles principales:

### 1. ROLE_USER (Usuario Registrado)
- **Descripción**: Rol por defecto asignado a todos los usuarios que se registran
- **Permisos**:
  - Crear foros
  - Crear posts en foros
  - Crear comentarios en posts
  - Editar sus propios foros
  - Editar sus propios posts
  - Editar sus propios comentarios
  - Eliminar sus propios foros
  - Eliminar sus propios posts
  - Eliminar sus propios comentarios
  - Ver todos los foros, posts y comentarios
  - Participar en trivia
  - Gestionar su mapa de viajes
  - Actualizar su perfil
  - Cambiar su contraseña

### 2. ROLE_MODERATOR (Moderador)
- **Descripción**: Rol para usuarios que moderan el contenido del foro
- **Permisos** (además de los de ROLE_USER):
  - Crear y editar tags
  - Editar cualquier foro
  - Editar cualquier post
  - Editar cualquier comentario
  - Eliminar cualquier post
  - Eliminar cualquier comentario
  - Ver todos los usuarios
  - Moderar contenido inapropiado

### 3. ROLE_ADMIN (Administrador)
- **Descripción**: Rol con acceso completo al sistema
- **Permisos** (además de los de ROLE_USER y ROLE_MODERATOR):
  - Gestionar usuarios (crear, editar, eliminar)
  - Gestionar roles
  - Gestionar categorías
  - Eliminar tags
  - Eliminar cualquier foro
  - Acceso completo a todas las funcionalidades

## Matriz de Permisos por Endpoint

### Autenticación (`/api/auth`)
| Endpoint | USER | MODERATOR | ADMIN |
|----------|------|-----------|-------|
| POST /register | ✅ | ✅ | ✅ |
| POST /login | ✅ | ✅ | ✅ |
| POST /logout | ✅ | ✅ | ✅ |
| POST /refresh | ✅ | ✅ | ✅ |

### Usuarios (`/api/users`)
| Endpoint | USER | MODERATOR | ADMIN |
|----------|------|-----------|-------|
| GET / | ❌ | ❌ | ✅ |
| GET /{id} | ✅ (propio) | ✅ (propio) | ✅ |
| GET /me | ✅ | ✅ | ✅ |
| POST / | ❌ | ❌ | ✅ |
| PUT /{id} | ✅ (propio) | ✅ (propio) | ✅ |
| DELETE /{id} | ❌ | ❌ | ✅ |
| PUT /{id}/change-password | ✅ (propio) | ✅ (propio) | ✅ |

### Foros (`/api/forums`)
| Endpoint | USER | MODERATOR | ADMIN |
|----------|------|-----------|-------|
| GET / | ✅ | ✅ | ✅ |
| GET /{id} | ✅ | ✅ | ✅ |
| GET /category/{id} | ✅ | ✅ | ✅ |
| GET /search | ✅ | ✅ | ✅ |
| POST / | ✅ | ✅ | ✅ |
| PUT /{id} | ✅ (propio) | ✅ (cualquiera) | ✅ |
| DELETE /{id} | ✅ (propio) | ✅ (cualquiera) | ✅ |
| POST /{id}/image | ✅ (propio) | ✅ (cualquiera) | ✅ |
| GET /user | ✅ | ✅ | ✅ |

### Posts (`/api/posts`)
| Endpoint | USER | MODERATOR | ADMIN |
|----------|------|-----------|-------|
| GET / | ✅ | ✅ | ✅ |
| GET /{id} | ✅ | ✅ | ✅ |
| GET /forum/{id} | ✅ | ✅ | ✅ |
| POST / | ✅ | ✅ | ✅ |
| PUT /{id} | ✅ (propio) | ✅ (cualquiera) | ✅ |
| DELETE /{id} | ✅ (propio) | ✅ (cualquiera) | ✅ |
| POST /{id}/images | ✅ (propio) | ✅ (cualquiera) | ✅ |
| DELETE /{postId}/images/{imageId} | ✅ (propio) | ✅ (cualquiera) | ✅ |
| GET /user | ✅ | ✅ | ✅ |

### Comentarios (`/api/comments`)
| Endpoint | USER | MODERATOR | ADMIN |
|----------|------|-----------|-------|
| GET / | ✅ | ✅ | ✅ |
| GET /{id} | ✅ | ✅ | ✅ |
| GET /post/{id} | ✅ | ✅ | ✅ |
| POST /post/{id} | ✅ | ✅ | ✅ |
| PUT /{id} | ✅ (propio) | ✅ (cualquiera) | ✅ |
| DELETE /{id} | ✅ (propio) | ✅ (cualquiera) | ✅ |

### Tags (`/api/tags`)
| Endpoint | USER | MODERATOR | ADMIN |
|----------|------|-----------|-------|
| GET / | ✅ | ✅ | ✅ |
| GET /{id} | ✅ | ✅ | ✅ |
| GET /name/{name} | ✅ | ✅ | ✅ |
| GET /popular | ✅ | ✅ | ✅ |
| POST / | ❌ | ✅ | ✅ |
| PUT /{id} | ❌ | ✅ | ✅ |
| DELETE /{id} | ❌ | ❌ | ✅ |

### Categorías (`/api/categories`)
| Endpoint | USER | MODERATOR | ADMIN |
|----------|------|-----------|-------|
| GET / | ✅ | ✅ | ✅ |
| GET /{id} | ✅ | ✅ | ✅ |
| POST / | ❌ | ❌ | ✅ |
| PUT /{id} | ❌ | ❌ | ✅ |
| DELETE /{id} | ❌ | ❌ | ✅ |
| POST /{id}/image | ❌ | ❌ | ✅ |

### Roles (`/api/roles`)
| Endpoint | USER | MODERATOR | ADMIN |
|----------|------|-----------|-------|
| GET / | ✅ | ✅ | ✅ |
| GET /{id} | ✅ | ✅ | ✅ |
| POST / | ❌ | ❌ | ✅ |
| PUT /{id} | ❌ | ❌ | ✅ |
| DELETE /{id} | ❌ | ❌ | ✅ |

## Validación de Contraseñas

Todos los usuarios deben cumplir con los siguientes requisitos de contraseña:

- **Mínimo 8 caracteres**
- **Al menos una letra mayúscula** (A-Z)
- **Al menos una letra minúscula** (a-z)
- **Al menos un carácter especial** (!@#$%^&*()_+-=[]{}|;:,.<>?)

### Endpoints que validan contraseñas:
- `POST /api/auth/register` - Registro de nuevos usuarios
- `PUT /api/users/{id}/change-password` - Cambio de contraseña

## Inicialización de Roles

Los roles se crean automáticamente al iniciar la aplicación:

1. **ROLE_USER**: Creado en `DataInitializer` y `BackendApplication`
2. **ROLE_MODERATOR**: Creado en `DataInitializer` y `BackendApplication`
3. **ROLE_ADMIN**: Creado en `DataInitializer` y `BackendApplication`

## Asignación de Roles

- **Registro automático**: Todos los usuarios nuevos reciben `ROLE_USER` por defecto
- **Asignación manual**: Solo los administradores pueden asignar roles `ROLE_MODERATOR` y `ROLE_ADMIN` a través de la API de usuarios

## Notas de Seguridad

1. Los usuarios solo pueden modificar/eliminar su propio contenido (foros, posts, comentarios)
2. Los moderadores pueden editar/eliminar cualquier contenido de foros, posts y comentarios
3. Los administradores tienen acceso completo a todas las funcionalidades
4. Las contraseñas se validan tanto en el frontend como en el backend
5. Las contraseñas se almacenan encriptadas usando BCrypt

