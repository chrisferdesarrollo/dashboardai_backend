# Dashboard AI Backend - Sistema de Autenticación

Este proyecto es un backend desarrollado con Spring Boot que implementa un sistema completo de autenticación y autorización usando JWT y PostgreSQL.

## Características

- ✅ Registro de usuarios
- ✅ Login con JWT
- ✅ Autenticación basada en tokens
- ✅ Sistema de roles (USER, MODERATOR, ADMIN)
- ✅ Encriptación de contraseñas con BCrypt
- ✅ Validación de datos
- ✅ Endpoints protegidos por roles

## Tecnologías utilizadas

- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Tokens)
- Maven

## Prerrequisitos

1. **Java 17** o superior
2. **Maven** 3.6 o superior
3. **PostgreSQL** 12 o superior

## Configuración de la base de datos

1. Instala PostgreSQL en tu sistema
2. Crea una base de datos llamada `dashboardai_db`:
   ```sql
   CREATE DATABASE dashboardai_db;
   ```
3. Actualiza las credenciales en `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
   ```

## Instalación y ejecución

1. Clona el proyecto
2. Navega al directorio del proyecto:
   ```bash
   cd dashboardAI_backend
   ```
3. Instala las dependencias:
   ```bash
   mvn clean install
   ```
4. Ejecuta la aplicación:
   ```bash
   mvn spring-boot:run
   ```

La aplicación estará disponible en `http://localhost:8080`

## Endpoints disponibles

### Autenticación

#### Registro de usuario
```http
POST /api/auth/signup
Content-Type: application/json

{
  "username": "usuario",
  "email": "usuario@email.com",
  "password": "123456",
  "role": ["user"]
}
```

#### Login
```http
POST /api/auth/signin
Content-Type: application/json

{
  "username": "usuario",
  "password": "123456"
}
```

Respuesta exitosa:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "id": 1,
  "username": "usuario",
  "email": "usuario@email.com",
  "roles": ["ROLE_USER"]
}
```

### Endpoints de prueba

#### Acceso público
```http
GET /api/test/all
```

#### Acceso para usuarios autenticados
```http
GET /api/test/user
Authorization: Bearer {token}
```

#### Acceso para moderadores
```http
GET /api/test/mod
Authorization: Bearer {token}
```

#### Acceso para administradores
```http
GET /api/test/admin
Authorization: Bearer {token}
```

## Roles disponibles

- **user**: Usuario básico
- **mod**: Moderador (incluye permisos de usuario)
- **admin**: Administrador (incluye todos los permisos)

## Estructura del proyecto

```
src/
├── main/
│   ├── java/com/dashboardai/
│   │   ├── config/          # Configuraciones
│   │   ├── controller/      # Controladores REST
│   │   ├── dto/            # DTOs (Request/Response)
│   │   ├── model/          # Entidades JPA
│   │   ├── repository/     # Repositorios
│   │   ├── security/       # Configuración de seguridad
│   │   └── DashboardBackendApplication.java
│   └── resources/
│       └── application.properties
└── test/
```

## Uso con herramientas de testing

### Postman/Insomnia

1. Registra un usuario con el endpoint `/api/auth/signup`
2. Haz login con `/api/auth/signin` y copia el token
3. Para endpoints protegidos, añade el header:
   ```
   Authorization: Bearer {tu_token_aquí}
   ```

### cURL

```bash
# Registro
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@email.com",
    "password": "123456"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456"
  }'

# Acceso a endpoint protegido
curl -X GET http://localhost:8080/api/test/user \
  -H "Authorization: Bearer {tu_token}"
```

## Configuración adicional

### Cambiar la clave secreta JWT
Modifica la propiedad en `application.properties`:
```properties
app.jwt.secret=tu_nueva_clave_secreta_muy_larga_y_segura
```

### Cambiar tiempo de expiración del token
```properties
app.jwt.expiration=86400000  # 24 horas en milisegundos
```

## Estructura de la base de datos

El sistema crea automáticamente las siguientes tablas:

- `users`: Información de usuarios
- `roles`: Roles del sistema
- `user_roles`: Relación muchos a muchos entre usuarios y roles

## Contribución

1. Fork el proyecto
2. Crea una rama para tu feature
3. Commit tus cambios
4. Push a la rama
5. Abre un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT.
