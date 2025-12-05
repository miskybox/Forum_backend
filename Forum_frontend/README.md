# ForumViajeros Frontend

Este proyecto es el frontend para la aplicación ForumViajeros, un foro de viajes donde los usuarios pueden explorar categorías organizadas por continentes, crear foros, participar en discusiones y compartir experiencias de viaje con fotos.

![logo2](https://github.com/user-attachments/assets/dfb32fea-09c1-4007-ad23-c2b783a1c3ee)

## Características principales

- Visualización de categorías por continente (accesible sin registro)
- Sistema completo de registro y autenticación de usuarios
- Perfiles de usuario editables
- Gestión completa de foros (CRUD)
- Creación, edición y eliminación de publicaciones
- Sistema de comentarios
- Carga de imágenes para foros y publicaciones
- Búsqueda de foros por palabras clave
- Interfaz responsive con diseño mobile-first

## Tecnologías utilizadas

- React con Vite
- React Router para la navegación
- Tailwind CSS para los estilos
- Axios para las peticiones HTTP
- React Hot Toast para notificaciones
- Date-fns para el formateo de fechas

## Estructura del proyecto

```
forumviajeros-frontend/
├── src/
│   ├── assets/           # Imágenes, iconos y otros recursos estáticos
│   ├── components/       # Componentes reutilizables
│   │   ├── auth/         # Componentes de autenticación
│   │   ├── categories/   # Componentes para las categorías
│   │   ├── comments/     # Componentes para los comentarios
│   │   ├── common/       # Componentes comunes (botones, navbar, etc.)
│   │   ├── forums/       # Componentes para los foros
│   │   ├── posts/        # Componentes para las publicaciones
│   │   └── user/         # Componentes relacionados con el usuario
│   ├── contexts/         # Contextos de React (autenticación, etc.)
│   ├── hooks/            # Hooks personalizados
│   ├── pages/            # Páginas principales de la aplicación
│   ├── services/         # Servicios para interactuar con la API
│   ├── utils/            # Utilidades y funciones auxiliares
│   ├── App.jsx           # Componente principal de la aplicación
│   ├── main.jsx          # Punto de entrada de React
│   └── index.css         # Estilos globales
├── .gitignore
├── package.json
├── tailwind.config.js    # Configuración de Tailwind CSS
├── vite.config.js        # Configuración de Vite
└── index.html
```

## Requisitos previos

- Node.js (v14 o superior)
- npm (v6 o superior)

## Instalación

1. Clona el repositorio:
```bash
git clone https://github.com/tu-usuario/forumviajeros-frontend.git
cd forumviajeros-frontend
```

2. Instala las dependencias:
```bash
npm install
```

3. Configura las variables de entorno:

Crea un archivo `.env.local` en la raíz del proyecto con el siguiente contenido:
```
VITE_API_BASE_URL=http://localhost:8080/api
```

Ajusta la URL de la API según la configuración de tu backend.

## Ejecución en desarrollo

Para iniciar el servidor de desarrollo:
```bash
npm run dev
```

La aplicación estará disponible en `http://localhost:5173`.

## Construcción para producción

Para generar una versión de producción:
```bash
npm run build
```

Los archivos generados estarán en la carpeta `dist/`.

## Notas para el desarrollador

- Asegúrate de que el backend esté en ejecución antes de iniciar el frontend.
- La estructura del proyecto sigue un patrón modular para facilitar el mantenimiento.
- Se ha implementado un diseño mobile-first para garantizar la accesibilidad en dispositivos móviles.
- El sistema de autenticación utiliza tokens JWT con refresh token para mantener la sesión del usuario.

## Endpoints de la API

El frontend se comunica con el backend a través de los siguientes endpoints:

- Autenticación: `/api/auth/*`
- Usuarios: `/api/users/*`
- Categorías: `/api/categories/*`
- Foros: `/api/forums/*`
- Publicaciones: `/api/posts/*`
- Comentarios: `/api/comments/*`
- Imágenes: `/api/images/*`

Para más detalles sobre los endpoints, consulta la documentación del backend.
![FV-home-mobile](https://github.com/user-attachments/assets/09ed988d-65d3-4e24-8015-25f12005ceb1)
