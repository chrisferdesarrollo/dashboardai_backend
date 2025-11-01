-- Crear tabla de templates de agentes
CREATE TABLE IF NOT EXISTS agent_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    system_prompt TEXT NOT NULL,
    knowledge_base_id VARCHAR(255),
    knowledge_base_name VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    usage_count INTEGER DEFAULT 0,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_agent_templates_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_agent_templates_user_id ON agent_templates(user_id);
CREATE INDEX IF NOT EXISTS idx_agent_templates_is_active ON agent_templates(is_active);
CREATE INDEX IF NOT EXISTS idx_agent_templates_created_at ON agent_templates(created_at DESC);

-- Comentarios para documentación
COMMENT ON TABLE agent_templates IS 'Plantillas de agentes predeterminados para crear agentes de Telegram y WhatsApp';
COMMENT ON COLUMN agent_templates.name IS 'Nombre del template de agente';
COMMENT ON COLUMN agent_templates.description IS 'Descripción del propósito del agente';
COMMENT ON COLUMN agent_templates.system_prompt IS 'Prompt del sistema que define el comportamiento del agente';
COMMENT ON COLUMN agent_templates.knowledge_base_id IS 'ID de la base de conocimiento en n8n';
COMMENT ON COLUMN agent_templates.knowledge_base_name IS 'Nombre descriptivo de la base de conocimiento';
COMMENT ON COLUMN agent_templates.is_active IS 'Indica si el template está activo';
COMMENT ON COLUMN agent_templates.usage_count IS 'Número de veces que se ha usado este template';
