# 🌍 ForumViajeros — Backend

Backend de la plataforma **ForumViajeros**, una aplicación que permite a los usuarios compartir experiencias de viaje organizadas por regiones del mundo. Este backend está desarrollado en **Java con Spring Boot**, y utiliza **PostgreSQL** como base de datos.

![Logo de ForumViajeros](./assets/logo.png)


## 1. 📌 Briefing del Proyecto

* **Título:** ForumViajeros
* **Objetivo:** Crear una plataforma donde usuarios puedan compartir y descubrir experiencias de viaje por regiones geográficas.
* **Contexto:** Existe una carencia de espacios digitales estructurados para compartir relatos de viaje organizados por continentes o regiones.
* **Descripción funcional:** La aplicación permite la creación de foros por continente, publicación de posts, subida de imágenes, y una sección de blogs. Cuenta con áreas públicas (explorar foros/posts) y privadas (crear/editar contenido, perfil de usuario).

---

## 2. ✅ Historias de Usuario (MVP)

1. Como **visitante**, quiero ver los foros por continente, para inspirarme a viajar.
2. Como **usuario registrado**, quiero registrarme e iniciar sesión, para acceder a funcionalidades privadas.
3. Como **usuario**, quiero crear y editar foros, para compartir mis experiencias de viaje.
4. Como **usuario**, quiero publicar comentarios en los posts de otros, para interactuar.
5. Como **usuario**, quiero editar mi perfil, para mantener mi información actualizada.
6. Como **usuario**, quiero subir imágenes a mis publicaciones, para ilustrar mis viajes.
7. Como **administrador**, quiero gestionar usuarios y contenidos inapropiados, para mantener la calidad de la plataforma.

---

## 3. ⚙️ Tecnologías y Dependencias

* Java 21
* Spring Boot
* Spring Security + JWT
* Spring Data JPA
* PostgreSQL
* Lombok
* MapStruct
* Maven
* Postaman

---

## 4. 📡 Endpoints Destacados

* `POST /api/auth/login`: Inicio de sesión con JWT
* `POST /api/auth/register`: Registro de usuario
* `GET /api/forums`: Listar foros por continente
* `POST /api/forums`: Crear foro (auth)
* `POST /api/posts`: Crear post con imagen
* `GET /api/users/me`: Obtener perfil del usuario autenticado

## 5. 🔐 Seguridad

* Autenticación con JWT
* Roles: `USER`, `ADMIN`
* Seguridad en subida de imágenes (tipos MIME permitidos: jpeg, png, webp)
* Validaciones de entrada con Jakarta Validation
