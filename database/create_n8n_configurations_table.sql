-- Tabla para configuraciones de N8n por usuario (PostgreSQL)
CREATE TABLE IF NOT EXISTS n8n_configurations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    n8n_url VARCHAR(512) NOT NULL,
    webhook_base VARCHAR(512) NOT NULL,
    api_base VARCHAR(512),
    api_key VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_test_date TIMESTAMP,
    last_test_status VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Restricciones
    CONSTRAINT fk_n8n_config_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Crear índices separadamente (PostgreSQL)
CREATE INDEX IF NOT EXISTS idx_n8n_config_user_id ON n8n_configurations (user_id);
CREATE INDEX IF NOT EXISTS idx_n8n_config_user_active ON n8n_configurations (user_id, is_active);

-- Función para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger para actualizar updated_at automáticamente
DROP TRIGGER IF EXISTS update_n8n_configurations_updated_at ON n8n_configurations;
CREATE TRIGGER update_n8n_configurations_updated_at
    BEFORE UPDATE ON n8n_configurations
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comentarios para documentación (PostgreSQL)
COMMENT ON TABLE n8n_configurations IS 'Configuraciones de N8n por usuario para conexiones personalizadas';
COMMENT ON COLUMN n8n_configurations.user_id IS 'ID del usuario propietario de la configuración';
COMMENT ON COLUMN n8n_configurations.n8n_url IS 'URL base de la instancia N8n del usuario';
COMMENT ON COLUMN n8n_configurations.webhook_base IS 'URL base para webhooks, calculada automáticamente';
COMMENT ON COLUMN n8n_configurations.api_base IS 'URL base para API REST, calculada automáticamente';
COMMENT ON COLUMN n8n_configurations.api_key IS 'Clave API de N8n (opcional)';
COMMENT ON COLUMN n8n_configurations.is_active IS 'Si esta configuración está activa para el usuario';
COMMENT ON COLUMN n8n_configurations.last_test_date IS 'Fecha del último test de conexión';
COMMENT ON COLUMN n8n_configurations.last_test_status IS 'Resultado del último test: success, failed, etc.';
COMMENT ON COLUMN n8n_configurations.created_at IS 'Fecha de creación';
COMMENT ON COLUMN n8n_configurations.updated_at IS 'Fecha de última actualización';
