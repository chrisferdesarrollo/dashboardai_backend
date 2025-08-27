-- Script para limpiar y recrear la tabla workflow correctamente
-- Ejecutar en la base de datos PostgreSQL

-- 1. Eliminar tabla workflow existente y dependencias
DROP TABLE IF EXISTS workflow_tags CASCADE;
DROP TABLE IF EXISTS workflow CASCADE;

-- 2. Crear la tabla workflow con la estructura correcta
CREATE TABLE workflow (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    n8n_workflow_id VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    workflow_data JSONB NOT NULL,
    user_id BIGINT NOT NULL,
    node_count INTEGER DEFAULT 0,
    tags TEXT[],
    active BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT pk_workflow PRIMARY KEY (id),
    CONSTRAINT fk_workflow_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Crear índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_workflow_user_id ON workflow(user_id);
CREATE INDEX IF NOT EXISTS idx_workflow_n8n_id ON workflow(n8n_workflow_id);
CREATE INDEX IF NOT EXISTS idx_workflow_active ON workflow(active);
CREATE INDEX IF NOT EXISTS idx_workflow_deleted ON workflow(is_deleted);
CREATE INDEX IF NOT EXISTS idx_workflow_user_not_deleted ON workflow(user_id, is_deleted);

-- 4. Crear índice para búsqueda de texto en nombre y descripción
CREATE INDEX IF NOT EXISTS idx_workflow_search ON workflow USING gin(to_tsvector('spanish', name || ' ' || COALESCE(description, '')));

-- 5. Trigger para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_workflow_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_workflow_updated_at
    BEFORE UPDATE ON workflow
    FOR EACH ROW
    EXECUTE FUNCTION update_workflow_updated_at();

-- 6. Insertar datos de prueba (solo si existe un usuario)
-- Verificar si existe al menos un usuario antes de insertar workflow de prueba
DO $$
BEGIN
    -- Solo insertar workflow de prueba si existe al menos un usuario
    IF EXISTS (SELECT 1 FROM users LIMIT 1) THEN
        -- Obtener el ID del primer usuario disponible
        INSERT INTO workflow (n8n_workflow_id, name, description, workflow_data, user_id, node_count, tags, active)
        SELECT 
            'test-workflow-1', 
            'Workflow de Prueba', 
            'Workflow de ejemplo para testing', 
            '{"nodes": [], "connections": {}}', 
            users.id, 
            0, 
            ARRAY['test', 'ejemplo'], 
            false
        FROM users 
        LIMIT 1
        ON CONFLICT (n8n_workflow_id) DO NOTHING;
        
        RAISE NOTICE 'Workflow de prueba insertado correctamente';
    ELSE
        RAISE NOTICE 'No se insertó workflow de prueba - no hay usuarios en la tabla users';
    END IF;
END $$;

-- 7. Comentarios para documentar la tabla
COMMENT ON TABLE workflow IS 'Tabla para almacenar workflows de n8n asociados a usuarios';
COMMENT ON COLUMN workflow.id IS 'Identificador único UUID del workflow en la base de datos';
COMMENT ON COLUMN workflow.n8n_workflow_id IS 'Identificador del workflow en n8n (debe ser único)';
COMMENT ON COLUMN workflow.name IS 'Nombre del workflow';
COMMENT ON COLUMN workflow.description IS 'Descripción del workflow';
COMMENT ON COLUMN workflow.workflow_data IS 'Datos completos del workflow en formato JSON';
COMMENT ON COLUMN workflow.user_id IS 'ID del usuario propietario del workflow';
COMMENT ON COLUMN workflow.node_count IS 'Número de nodos en el workflow';
COMMENT ON COLUMN workflow.tags IS 'Array de tags asociados al workflow';
COMMENT ON COLUMN workflow.active IS 'Indica si el workflow está activo en n8n';
COMMENT ON COLUMN workflow.is_deleted IS 'Borrado lógico del workflow';
COMMENT ON COLUMN workflow.created_at IS 'Fecha y hora de creación';
COMMENT ON COLUMN workflow.updated_at IS 'Fecha y hora de última actualización';

-- Verificar la estructura de la tabla
SELECT 
    column_name, 
    data_type, 
    is_nullable, 
    column_default
FROM information_schema.columns 
WHERE table_name = 'workflow' 
ORDER BY ordinal_position;

-- Verificar usuarios existentes en la base de datos
SELECT 
    id, 
    username, 
    email,
    created_at
FROM users 
ORDER BY id 
LIMIT 5;
