# 🔒 GUÍA DE SEGURIDAD

**Proyecto:** Forum Viajeros  
**Última actualización:** 2025-12-15

---

## 📋 ÍNDICE

1. [Configuración de Seguridad](#configuración-de-seguridad)
2. [Variables de Entorno](#variables-de-entorno)
3. [Rate Limiting](#rate-limiting)
4. [CORS](#cors)
5. [Security Headers](#security-headers)
6. [Autenticación y Autorización](#autenticación-y-autorización)
7. [Checklist de Producción](#checklist-de-producción)

---

## 🔐 CONFIGURACIÓN DE SEGURIDAD

### Implementaciones Actuales

#### ✅ **Autenticación JWT**
- Tokens de acceso con expiración (10 minutos)
- Refresh tokens con expiración (30 días)
- Tokens almacenados en base de datos para invalidación

#### ✅ **Hash de Contraseñas**
- BCrypt con salt automático
- Validación de contraseñas robusta (min 8 chars, mayúscula, minúscula, especial)

#### ✅ **Rate Limiting**
- Implementado para endpoints de autenticación
- Protección contra brute force attacks
- Límites configurables por endpoint

#### ✅ **Security Headers**
- Content Security Policy (CSP)
- X-Frame-Options: DENY
- HTTP Strict Transport Security (HSTS)

#### ✅ **CORS**
- Configuración restrictiva
- Validación de orígenes
- No permite '*' en producción

---

## 🔑 VARIABLES DE ENTORNO

### Variables Críticas (Requeridas)

```bash
# Base de Datos
DB_URL=jdbc:postgresql://localhost:5432/forum_viajeros
DB_USER=postgres
DB_PASSWORD=tu_password_seguro

# JWT Secret (MÍNIMO 64 CARACTERES)
JWT_SECRET_KEY=tu_secret_key_minimo_64_caracteres_aqui

# CORS (Producción)
CORS_ALLOWED_ORIGINS=https://tudominio.com,https://www.tudominio.com
```

### Generar JWT Secret Seguro

**Linux/Mac:**
```bash
openssl rand -base64 64 | tr -d '\n'
```

**PowerShell:**
```powershell
[Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
```

**Online:**
- https://generate-secret.vercel.app/

### Validación Automática

El sistema valida automáticamente que:
- ✅ `DB_URL` existe y tiene formato correcto
- ✅ `DB_USER` no está vacío
- ✅ `DB_PASSWORD` no está vacío
- ✅ `JWT_SECRET_KEY` existe y tiene mínimo 64 caracteres

Si falta alguna variable, **el backend NO inicia** y muestra un error claro.

---

## 🛡️ RATE LIMITING

### Configuración Actual

| Endpoint | Límite | Ventana | Propósito |
|----------|--------|---------|-----------|
| `/api/auth/login` | 5 intentos | 60 segundos | Prevenir brute force |
| `/api/auth/register` | 3 intentos | 60 segundos | Prevenir spam |
| `/api/auth/refresh` | 10 intentos | 60 segundos | Prevenir abuso |

### Comportamiento

- **Al exceder el límite:** Retorna HTTP 429 (Too Many Requests)
- **Mensaje:** "Demasiados intentos. Por favor, espera un minuto."
- **Tracking:** Por IP address
- **Ventana deslizante:** Los intentos se resetean después de la ventana de tiempo

### Implementación

- Filtro: `RateLimitingFilter`
- Orden: Ejecuta antes de otros filtros de seguridad
- Almacenamiento: En memoria (ConcurrentHashMap)

**Nota:** Para producción con múltiples instancias, considerar usar Redis para rate limiting distribuido.

---

## 🌐 CORS

### Configuración

**Desarrollo:**
```bash
# Por defecto: http://localhost:5173
CORS_ALLOWED_ORIGINS=http://localhost:5173
```

**Producción:**
```bash
# Especificar orígenes exactos (NO usar '*')
CORS_ALLOWED_ORIGINS=https://tudominio.com,https://www.tudominio.com
```

### Validaciones

- ✅ No permite `*` (demasiado permisivo)
- ✅ Valida que esté configurado
- ✅ Permite múltiples orígenes separados por comas

### Headers Permitidos

- `Authorization`
- `Content-Type`
- `Accept`
- `Refresh-Token`

### Métodos Permitidos

- GET, POST, PUT, DELETE, OPTIONS, PATCH

---

## 🔒 SECURITY HEADERS

### Headers Configurados

#### Content Security Policy (CSP)
```
default-src 'self'
```
- Previene XSS attacks
- Solo permite recursos del mismo origen

#### X-Frame-Options
```
DENY
```
- Previene clickjacking
- No permite que la página se cargue en iframes

#### HTTP Strict Transport Security (HSTS)
```
max-age=31536000; includeSubDomains
```
- Fuerza HTTPS
- Válido por 1 año
- Incluye subdominios

---

## 👤 AUTENTICACIÓN Y AUTORIZACIÓN

### Roles Implementados

1. **ROLE_USER**
   - Usuario normal
   - Puede crear/editar su propio contenido
   - Acceso a funcionalidades básicas

2. **ROLE_MODERATOR**
   - Puede editar/eliminar cualquier contenido
   - Puede crear/editar tags
   - Acceso a dashboard de moderador

3. **ROLE_ADMIN**
   - Acceso completo
   - Gestión de usuarios y roles
   - Acceso a dashboard de administrador

### Endpoints Protegidos

**Solo ADMIN:**
- `GET /api/users` - Listar usuarios
- `PUT /api/users/{id}` - Actualizar usuario
- `DELETE /api/users/{id}` - Eliminar usuario
- `PUT /api/users/{id}/roles` - Actualizar roles
- `POST /api/roles` - Crear rol
- `PUT /api/roles/{id}` - Actualizar rol
- `DELETE /api/roles/{id}` - Eliminar rol
- `POST /api/categories` - Crear categoría
- `PUT /api/categories/{id}` - Actualizar categoría
- `DELETE /api/categories/{id}` - Eliminar categoría

**ADMIN o MODERATOR:**
- `POST /api/tags` - Crear tag
- `PUT /api/tags/{id}` - Actualizar tag

**Autenticados:**
- `GET /api/users/me` - Obtener usuario actual
- `POST /api/forums` - Crear foro
- `POST /api/posts` - Crear post
- `POST /api/comments` - Crear comentario

---

## ✅ CHECKLIST DE PRODUCCIÓN

### Antes de Desplegar

#### Configuración
- [ ] Variables de entorno configuradas
- [ ] JWT_SECRET_KEY tiene mínimo 64 caracteres
- [ ] CORS_ALLOWED_ORIGINS configurado (no '*')
- [ ] Base de datos de producción configurada
- [ ] `application-prod.properties` activo

#### Seguridad
- [ ] HTTPS configurado (servidor web o reverse proxy)
- [ ] Rate limiting funcionando
- [ ] Security headers configurados
- [ ] CORS restringido a orígenes específicos
- [ ] `.env` NO está en el repositorio

#### Base de Datos
- [ ] Base de datos creada
- [ ] Usuario de BD con permisos mínimos necesarios
- [ ] Backup configurado
- [ ] `spring.jpa.hibernate.ddl-auto=validate` (no update)

#### Monitoreo
- [ ] Logging configurado
- [ ] Alertas configuradas
- [ ] Monitoreo de errores (Sentry, etc.)

#### Testing
- [ ] Tests pasando
- [ ] Pruebas de carga realizadas
- [ ] Pruebas de seguridad realizadas

---

## 🚨 PROBLEMAS COMUNES

### Error: "Variables de entorno críticas faltantes"

**Solución:**
1. Verifica que el archivo `.env` existe
2. Verifica que tiene todas las variables requeridas
3. Verifica que no hay espacios extra en los valores

### Error: "JWT_SECRET_KEY debe tener al menos 64 caracteres"

**Solución:**
1. Genera un nuevo secret de 64+ caracteres
2. Actualiza `JWT_SECRET_KEY` en `.env`
3. Reinicia el backend

### Error: "CORS_ALLOWED_ORIGINS no puede ser '*'"

**Solución:**
1. Configura `CORS_ALLOWED_ORIGINS` con orígenes específicos
2. Separa múltiples orígenes con comas
3. No uses '*' en producción

### Error: "Connection refused" (Base de datos)

**Solución:**
1. Verifica que PostgreSQL está corriendo
2. Verifica que la base de datos existe
3. Verifica las credenciales en `.env`

---

## 📚 RECURSOS ADICIONALES

### Documentación
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT Best Practices](https://datatracker.ietf.org/doc/html/rfc8725)

### Herramientas
- [JWT.io](https://jwt.io/) - Debugger de tokens JWT
- [Security Headers](https://securityheaders.com/) - Verificar headers de seguridad

---

## 📝 NOTAS

- **Rate Limiting:** Actualmente en memoria. Para múltiples instancias, usar Redis.
- **CORS:** La configuración principal está en `SecurityConfig.java`
- **Security Headers:** Configurados en `SecurityConfig.java`
- **Validación:** Se ejecuta al inicio, antes de que Spring Boot inicie

---

**Mantenido por:** Equipo de Desarrollo  
**Última revisión:** 2025-12-15

