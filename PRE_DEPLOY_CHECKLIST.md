# ✅ Checklist Pre-Deploy - Forum Viajeros

**Fecha:** 2025-12-04
**Estado del proyecto:** Optimizado y listo para deploy

---

## 🎯 CHECKLIST DE DEPLOY

### ✅ **COMPLETADO**

- [x] **Optimización del GeoJSON** ✅ HECHO
  - Reducido de 14 MB → 251 KB (98.2%)
  - Movido a `/public/` para producción
  - Actualizado WorldMap.jsx con path correcto
  - Sistema de fallback a CDN implementado

---

### 🔧 **OPTIMIZACIONES PENDIENTES (Prioridad ALTA)**

#### 1. **Crear TriviaDataInitializer** ⚠️ CRÍTICO
**Prioridad:** ALTA
**Tiempo estimado:** 2-3 horas

**Problema:** No existe un inicializador de preguntas de trivia, la BD podría estar vacía.

**Acción requerida:**
```java
// Crear: Forum_backend/src/main/java/com/forumviajeros/backend/config/TriviaDataInitializer.java

@Component
public class TriviaDataInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        // Generar preguntas automáticamente desde datos de países
        // - CAPITAL: "¿Cuál es la capital de {país}?"
        // - FLAG: "¿A qué país pertenece esta bandera?"
        // - CURRENCY: "¿Cuál es la moneda de {país}?"
        // - etc.
    }
}
```

**Verificar:**
```bash
# Después de crear el inicializador, verificar que las preguntas se crean:
curl http://localhost:8080/api/trivia/questions/random
```

---

#### 2. **Añadir Tests Unitarios Backend** ⚠️ IMPORTANTE
**Prioridad:** ALTA
**Tiempo estimado:** 4-6 horas

**Crear archivos:**
```
Forum_backend/src/test/java/com/forumviajeros/backend/service/
├── CountryServiceTest.java
├── VisitedPlaceServiceTest.java
└── TriviaServiceTest.java
```

**Tests mínimos requeridos:**
- ✅ Test de cálculo de porcentaje por área
- ✅ Test de cálculo de porcentaje por países
- ✅ Test de ranking de usuarios
- ✅ Test de generación de preguntas de trivia
- ✅ Test de validación de respuestas
- ✅ Test de cálculo de accuracy

**Comando para ejecutar:**
```bash
cd Forum_backend
mvn test
```

---

#### 3. **Configurar Variables de Entorno** ⚠️ CRÍTICO
**Prioridad:** ALTA (ANTES DE DEPLOY)

**Backend - `.env` (NO commitear):**
```bash
# Base de datos PostgreSQL (producción)
DB_URL=jdbc:postgresql://tu-servidor.com:5432/forum_viajeros_prod
DB_USER=tu_usuario_prod
DB_PASSWORD=tu_password_seguro_prod

# JWT (CAMBIAR en producción - usar secreto fuerte)
JWT_SECRET_KEY=<generar-secreto-seguro-64-chars>

# Admin (CAMBIAR en producción)
ADMIN_USERNAME=admin
ADMIN_EMAIL=admin@tudominio.com
ADMIN_PASSWORD=<password-seguro-prod>

# Usuario demo (OPCIONAL - eliminar en producción)
USER_USERNAME=demo
USER_EMAIL=demo@tudominio.com
USER_PASSWORD=<password-demo>

# Configuración Spring
SPRING_PROFILES_ACTIVE=prod
```

**Generar secreto JWT seguro:**
```bash
# Linux/Mac:
openssl rand -base64 64 | tr -d '\n'

# O usar generador online:
# https://generate-secret.vercel.app/
```

**Frontend - Variables de entorno:**
```bash
# Crear archivo: Forum_frontend/.env.production
VITE_API_BASE_URL=https://api.tudominio.com/api
VITE_APP_NAME=Forum Viajeros
```

---

#### 4. **Revisar Constante Hardcodeada** 🟡 MEDIA
**Prioridad:** MEDIA
**Tiempo estimado:** 15 minutos

**Archivo:** `VisitedPlaceServiceImpl.java:46`

**Cambiar:**
```java
// ANTES:
private static final int TOTAL_COUNTRIES = 195;

// DESPUÉS:
private int getTotalCountries() {
    return (int) countryRepository.count();
}
```

---

### 🔐 **SEGURIDAD (Antes de Deploy)**

#### 5. **Revisar Configuración de CORS** ⚠️ CRÍTICO
**Prioridad:** ALTA

**Archivo a revisar:** `WebSecurityConfig.java` o similar

**Verificar:**
```java
@Configuration
public class WebSecurityConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ❌ NO usar en producción:
        // config.addAllowedOrigin("*");

        // ✅ Usar origen específico:
        config.addAllowedOrigin("https://tudominio.com");
        config.addAllowedOrigin("https://www.tudominio.com");

        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        return source;
    }
}
```

---

#### 6. **Validar Configuración de JWT** ⚠️ CRÍTICO
**Prioridad:** ALTA

**Verificar:**
- ✅ JWT_SECRET_KEY con mínimo 64 caracteres
- ✅ Tiempo de expiración configurado (ej: 24 horas)
- ✅ Refresh token implementado (si es necesario)

---

#### 7. **Configurar HTTPS** ⚠️ CRÍTICO
**Prioridad:** ALTA (Obligatorio en producción)

**Backend - application.properties:**
```properties
# Forzar HTTPS en producción
server.ssl.enabled=true
server.port=8443

# O usar reverse proxy (Nginx/Apache) para SSL
```

**Certificado SSL:**
- Let's Encrypt (gratis): https://letsencrypt.org/
- Cloudflare SSL (gratis): https://www.cloudflare.com/

---

### 📦 **BASE DE DATOS (Antes de Deploy)**

#### 8. **Preparar Base de Datos PostgreSQL** ⚠️ CRÍTICO
**Prioridad:** ALTA

**Checklist:**
```bash
# 1. Crear base de datos
createdb forum_viajeros_prod

# 2. Crear usuario
psql -c "CREATE USER forum_user WITH PASSWORD 'password_seguro';"

# 3. Otorgar permisos
psql -c "GRANT ALL PRIVILEGES ON DATABASE forum_viajeros_prod TO forum_user;"

# 4. Verificar conexión
psql -U forum_user -d forum_viajeros_prod -h localhost
```

**Configurar en `.env`:**
```bash
DB_URL=jdbc:postgresql://localhost:5432/forum_viajeros_prod
DB_USER=forum_user
DB_PASSWORD=password_seguro
```

---

#### 9. **Ejecutar Migraciones** ⚠️ CRÍTICO
**Prioridad:** ALTA

**Backend iniciará automáticamente con:**
- Spring Boot JPA creará las tablas
- `CountryDataInitializer` poblará países
- `TriviaDataInitializer` poblará preguntas (PENDIENTE DE CREAR)

**Verificar:**
```bash
# Después de iniciar backend, verificar tablas:
psql -U forum_user -d forum_viajeros_prod

\dt   # Listar tablas
SELECT COUNT(*) FROM countries;        # Debe tener ~195 países
SELECT COUNT(*) FROM trivia_questions; # Debe tener preguntas
```

---

#### 10. **Backup de Base de Datos** 🟡 MEDIA
**Prioridad:** MEDIA (Configurar post-deploy)

**Script de backup:**
```bash
#!/bin/bash
# backup-db.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backups"
DB_NAME="forum_viajeros_prod"

pg_dump -U forum_user $DB_NAME | gzip > "$BACKUP_DIR/backup_$DATE.sql.gz"

# Mantener solo últimos 7 días
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +7 -delete
```

**Configurar cron:**
```bash
# Backup diario a las 2 AM
0 2 * * * /path/to/backup-db.sh
```

---

### 🚀 **BUILD Y DEPLOY**

#### 11. **Build del Backend** ✅ LISTO
**Comando:**
```bash
cd Forum_backend
mvn clean package -DskipTests

# Genera: target/backend-0.0.1-SNAPSHOT.jar
```

**Verificar:**
```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
# Debe iniciar en puerto 8080
```

---

#### 12. **Build del Frontend** ✅ LISTO
**Comando:**
```bash
cd Forum_frontend
npm run build

# Genera: dist/ (968 KB)
# Incluye: countries.geojson (251 KB) ✅
```

**Verificar build:**
```bash
npm run preview
# Abre en http://localhost:4173
```

---

#### 13. **Configurar Servidor de Producción** ⚠️ PENDIENTE
**Prioridad:** ALTA

**Opciones de hosting:**

**A) VPS/Servidor Dedicado:**
- DigitalOcean, Linode, AWS EC2, etc.
- Instalar: Java 21, PostgreSQL, Nginx
- Configurar systemd para auto-inicio

**B) PaaS (Platform as a Service):**
- **Backend:** Railway, Render, Heroku
- **Frontend:** Vercel, Netlify, Cloudflare Pages
- **Base de datos:** Railway PostgreSQL, Supabase

**C) Contenedores (Recomendado):**
- Docker + Docker Compose
- Kubernetes (para escalabilidad)

---

#### 14. **Crear Dockerfile (Recomendado)** 🟡 MEDIA

**Backend Dockerfile:**
```dockerfile
# Forum_backend/Dockerfile
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Frontend Dockerfile:**
```dockerfile
# Forum_frontend/Dockerfile
FROM nginx:alpine

COPY dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
```

**docker-compose.yml:**
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: forum_viajeros_prod
      POSTGRES_USER: forum_user
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  backend:
    build: ./Forum_backend
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:postgresql://postgres:5432/forum_viajeros_prod
      DB_USER: forum_user
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET_KEY: ${JWT_SECRET_KEY}
    depends_on:
      - postgres

  frontend:
    build: ./Forum_frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  postgres_data:
```

---

### 📊 **MONITOREO Y LOGS**

#### 15. **Configurar Logging** 🟡 MEDIA
**Prioridad:** MEDIA

**Backend - application.properties:**
```properties
# Logging en producción
logging.level.root=INFO
logging.level.com.forumviajeros=DEBUG
logging.file.name=/var/log/forum-viajeros/application.log
logging.file.max-size=10MB
logging.file.max-history=7
```

---

#### 16. **Health Check Endpoints** 🟡 BAJA
**Prioridad:** BAJA (útil para monitoreo)

**Añadir al pom.xml:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**application.properties:**
```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

**Endpoints disponibles:**
- `GET /actuator/health` - Estado del sistema
- `GET /actuator/metrics` - Métricas de rendimiento

---

### 🧪 **TESTING FINAL**

#### 17. **Tests E2E en Producción** ✅ LISTOS
**Comando:**
```bash
cd Forum_frontend
npm run test:e2e
```

**Tests incluidos:**
- ✅ 17 tests Playwright
- ✅ Auth flow
- ✅ Travel map
- ✅ Trivia game
- ✅ Navigation

---

### 📈 **PERFORMANCE**

#### 18. **Optimizaciones Opcionales** 🟢 BAJA
**Prioridad:** BAJA (post-deploy)

- [ ] Habilitar compresión Gzip/Brotli en Nginx
- [ ] CDN para assets estáticos (CloudFlare)
- [ ] Redis para caché de ranking
- [ ] Database indexing optimizado
- [ ] Service Worker para PWA
- [ ] Lazy loading de componentes React

---

### 📋 **DOCUMENTACIÓN**

#### 19. **README para Deploy** 🟡 RECOMENDADO

**Crear:** `DEPLOY.md` con instrucciones específicas:
- URLs de producción
- Credenciales (en gestor seguro)
- Procedimiento de deploy
- Rollback plan
- Contactos de emergencia

---

## 🎯 RESUMEN DE PRIORIDADES

### 🔴 **CRÍTICO (Hacer ANTES de deploy):**
1. ✅ Optimizar GeoJSON → **COMPLETADO**
2. ⚠️ Crear TriviaDataInitializer
3. ⚠️ Configurar variables de entorno (.env prod)
4. ⚠️ Revisar CORS
5. ⚠️ Configurar JWT seguro
6. ⚠️ Preparar base de datos PostgreSQL
7. ⚠️ Configurar HTTPS/SSL

### 🟡 **IMPORTANTE (Hacer pronto):**
8. Añadir tests unitarios backend
9. Revisar constante hardcodeada (TOTAL_COUNTRIES)
10. Configurar servidor de producción
11. Crear Dockerfiles

### 🟢 **RECOMENDADO (Puede esperar):**
12. Configurar backups automáticos
13. Health check endpoints
14. Optimizaciones de performance
15. Documentación de deploy

---

## ✅ CHECKLIST FINAL ANTES DE DEPLOY

```bash
# Backend
[ ] Variables de entorno configuradas (.env)
[ ] JWT_SECRET_KEY generado (64+ chars)
[ ] CORS configurado con dominio específico
[ ] Base de datos PostgreSQL creada
[ ] TriviaDataInitializer implementado
[ ] Build exitoso: mvn clean package
[ ] Jar generado en target/

# Frontend
[ ] Variables de entorno (.env.production)
[ ] VITE_API_BASE_URL apuntando a producción
[ ] Build exitoso: npm run build
[ ] GeoJSON optimizado en dist/ (251 KB) ✅
[ ] Preview funcional: npm run preview

# Base de Datos
[ ] PostgreSQL instalado y configurado
[ ] Usuario y permisos creados
[ ] Conexión verificada
[ ] Backup configurado

# Seguridad
[ ] HTTPS/SSL configurado
[ ] Contraseñas seguras
[ ] .env NO commiteado en git
[ ] CORS restringido a dominio específico

# Testing
[ ] Tests E2E pasando
[ ] Tests unitarios pasando (cuando se creen)
[ ] Health checks funcionando

# Deploy
[ ] Servidor configurado
[ ] DNS apuntando al servidor
[ ] Backend deployado y corriendo
[ ] Frontend deployado y corriendo
[ ] Base de datos poblada con datos iniciales
```

---

## 🚀 COMANDO DE DEPLOY RÁPIDO

```bash
#!/bin/bash
# deploy.sh

echo "🚀 Iniciando deploy de Forum Viajeros..."

# Backend
echo "📦 Building backend..."
cd Forum_backend
mvn clean package -DskipTests
echo "✅ Backend build completado"

# Frontend
echo "📦 Building frontend..."
cd ../Forum_frontend
npm run build
echo "✅ Frontend build completado"

# Verificación
echo "✅ Archivos listos para deploy:"
echo "  - Backend: Forum_backend/target/backend-0.0.1-SNAPSHOT.jar"
echo "  - Frontend: Forum_frontend/dist/"
echo ""
echo "🎉 Build completado! Listo para deploy."
```

---

**Última actualización:** 2025-12-04
**Estado:** Optimización de GeoJSON completada ✅
**Siguiente paso:** Implementar TriviaDataInitializer antes de deploy
