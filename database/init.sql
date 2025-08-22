-- Crear base de datos
CREATE DATABASE dashboard_ai_db;

-- Conectarse a la base de datos
\c dashboard_ai_db;

-- Las tablas se crearán automáticamente por Hibernate/JPA
-- cuando ejecutes la aplicación Spring Boot

-- Verificar que las tablas se crearon correctamente (ejecutar después de iniciar la app)
-- \dt

-- Consultar usuarios
-- SELECT * FROM users;

-- Consultar roles
-- SELECT * FROM roles;

-- Consultar relación usuarios-roles
-- SELECT * FROM user_roles;
