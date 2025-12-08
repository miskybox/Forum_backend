# Usuarios de Prueba - Forum Viajeros

## Credenciales de Prueba

### 🔴 Administrador
- **Username**: `admin` (configurable en `.env` como `ADMIN_USERNAME`)
- **Email**: `admin@forumviajeros.com` (configurable en `.env` como `ADMIN_EMAIL`)
- **Password**: Configurado en `.env` como `ADMIN_PASSWORD`
- **Rol**: `ROLE_ADMIN`
- **Permisos**: Acceso completo al sistema

### 🟡 Moderador
- **Username**: `moderator`
- **Email**: `moderator@forumviajeros.com`
- **Password**: `Moderator123!`
- **Rol**: `ROLE_MODERATOR`
- **Permisos**: Puede editar/eliminar cualquier foro, post o comentario

### 🟢 Usuario Normal
- **Username**: `user` (configurable en `.env` como `USER_USERNAME`)
- **Email**: `user@forumviajeros.com` (configurable en `.env` como `USER_EMAIL`)
- **Password**: Configurado en `.env` como `USER_PASSWORD`
- **Rol**: `ROLE_USER`
- **Permisos**: Puede crear y gestionar su propio contenido

## Configuración en .env

Para personalizar los usuarios de prueba, agrega estas variables a tu archivo `.env`:

```env
# Usuario Administrador
ADMIN_USERNAME=admin
ADMIN_EMAIL=admin@forumviajeros.com
ADMIN_PASSWORD=Admin123!

# Usuario Normal
USER_USERNAME=user
USER_EMAIL=user@forumviajeros.com
USER_PASSWORD=User123!

# Usuario Moderador (valores por defecto si no se especifican)
MODERATOR_USERNAME=moderator
MODERATOR_EMAIL=moderator@forumviajeros.com
MODERATOR_PASSWORD=Moderator123!
```

## Requisitos de Contraseña

Todas las contraseñas deben cumplir:
- ✅ Mínimo 8 caracteres
- ✅ Al menos una mayúscula (A-Z)
- ✅ Al menos una minúscula (a-z)
- ✅ Al menos un carácter especial (!@#$%^&*()_+-=[]{}|;:,.<>?)

## Inicialización Automática

Los usuarios se crean automáticamente al iniciar la aplicación si:
1. `app.init.users.enabled=true` (por defecto es `true`)
2. Los usuarios no existen previamente en la base de datos

## Acceso a Dashboards

### Dashboard de Administrador
- **URL**: `/admin/dashboard`
- **Requisito**: Rol `ROLE_ADMIN`
- **Funcionalidades**:
  - Ver estadísticas del sistema
  - Gestionar usuarios
  - Ver foros recientes
  - Eliminar usuarios

### Dashboard de Moderador
- **URL**: `/moderator/dashboard`
- **Requisito**: Rol `ROLE_MODERATOR` o `ROLE_ADMIN`
- **Funcionalidades**:
  - Ver estadísticas de contenido
  - Editar/eliminar foros
  - Editar/eliminar posts
  - Moderar comentarios

## Notas

- Los usuarios se crean solo si no existen previamente
- Las contraseñas se almacenan encriptadas con BCrypt
- El usuario moderador se crea automáticamente con valores por defecto si no se especifican en `.env`
- Los roles se asignan automáticamente durante la creación

