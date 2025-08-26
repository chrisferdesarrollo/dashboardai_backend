-- Crear la tabla configurations con relación a usuarios
CREATE TABLE IF NOT EXISTS configurations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    config_key VARCHAR(255) NOT NULL,
    config_value TEXT,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Clave única combinada: un usuario solo puede tener una configuración por clave
    UNIQUE(user_id, config_key),
    
    -- Referencia a la tabla users
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Crear índices para optimizar búsquedas
CREATE INDEX IF NOT EXISTS idx_configurations_user_id ON configurations(user_id);
CREATE INDEX IF NOT EXISTS idx_configurations_user_key ON configurations(user_id, config_key);
CREATE INDEX IF NOT EXISTS idx_configurations_key ON configurations(config_key);

-- Función para actualizar automáticamente updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger para actualizar updated_at automáticamente
DROP TRIGGER IF EXISTS update_configurations_updated_at ON configurations;
CREATE TRIGGER update_configurations_updated_at 
    BEFORE UPDATE ON configurations 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Insertar configuraciones por defecto para todos los usuarios existentes
-- (Solo si quieres que todos los usuarios tengan configuraciones iniciales)
INSERT INTO configurations (user_id, config_key, config_value, description)
SELECT 
    u.id,
    'n8n.webhook.url',
    '',
    'URL del webhook de n8n para triggers de workflows'
FROM users u
WHERE NOT EXISTS (
    SELECT 1 FROM configurations c 
    WHERE c.user_id = u.id AND c.config_key = 'n8n.webhook.url'
);

INSERT INTO configurations (user_id, config_key, config_value, description)
SELECT 
    u.id,
    'n8n.api.url',
    '',
    'URL de la API de n8n para gestión de workflows'
FROM users u
WHERE NOT EXISTS (
    SELECT 1 FROM configurations c 
    WHERE c.user_id = u.id AND c.config_key = 'n8n.api.url'
);

INSERT INTO configurations (user_id, config_key, config_value, description)
SELECT 
    u.id,
    'n8n.api.token',
    '',
    'Token de autenticación para la API de n8n'
FROM users u
WHERE NOT EXISTS (
    SELECT 1 FROM configurations c 
    WHERE c.user_id = u.id AND c.config_key = 'n8n.api.token'
);

-- Verificar que se creó correctamente
SELECT 
    table_name, 
    column_name, 
    data_type, 
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_name = 'configurations' 
ORDER BY ordinal_position;

-- Mostrar las configuraciones creadas
SELECT 
    c.id,
    u.email as user_email,
    c.config_key,
    CASE 
        WHEN c.config_key LIKE '%.token' THEN '***HIDDEN***'
        ELSE c.config_value 
    END as config_value,
    c.description,
    c.created_at
FROM configurations c
JOIN users u ON c.user_id = u.id
ORDER BY u.email, c.config_key;
