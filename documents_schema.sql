-- Script SQL para crear la tabla documents y documents_vectors
-- Este script debe ejecutarse en la base de datos PostgreSQL

-- Crear extensión vector si no existe (ya debe estar creada según los requerimientos)
-- create extension if not exists vector;

-- Crear tabla documents para almacenar metadatos de documentos subidos
CREATE TABLE IF NOT EXISTS documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    file_type VARCHAR(50) NOT NULL,
    tags TEXT[], -- Array de tags para organización
    agent_id UUID, -- ID del agente específico para entrenar (opcional)
    upload_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    processed BOOLEAN DEFAULT FALSE, -- Indica si ya fue procesado por N8N
    processing_status VARCHAR(50) DEFAULT 'pending', -- pending, processing, completed, failed
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para optimizar consultas
CREATE INDEX IF NOT EXISTS idx_documents_agent_id ON documents(agent_id);
CREATE INDEX IF NOT EXISTS idx_documents_processed ON documents(processed);
CREATE INDEX IF NOT EXISTS idx_documents_processing_status ON documents(processing_status);
CREATE INDEX IF NOT EXISTS idx_documents_upload_date ON documents(upload_date);
CREATE INDEX IF NOT EXISTS idx_documents_tags ON documents USING GIN(tags);

-- Crear función para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Crear trigger para updated_at
DROP TRIGGER IF EXISTS update_documents_updated_at ON documents;
CREATE TRIGGER update_documents_updated_at
    BEFORE UPDATE ON documents
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Verificar que la tabla documents_vectors ya existe (según requerimientos)
-- Si no existe, la creamos:
CREATE TABLE IF NOT EXISTS documents_vectors (
    id BIGSERIAL PRIMARY KEY,
    content TEXT, -- corresponds to Document.pageContent
    metadata JSONB, -- corresponds to Document.metadata
    embedding VECTOR(1536), -- 1536 works for OpenAI embeddings, change if needed
    document_id UUID REFERENCES documents(id) ON DELETE CASCADE, -- Relación con documents
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para documents_vectors
CREATE INDEX IF NOT EXISTS idx_documents_vectors_document_id ON documents_vectors(document_id);
CREATE INDEX IF NOT EXISTS idx_documents_vectors_embedding ON documents_vectors USING ivfflat (embedding vector_cosine_ops);

-- Verificar que la función match_documents existe (según requerimientos)
-- Si no existe, la creamos:
CREATE OR REPLACE FUNCTION match_documents (
    query_embedding VECTOR(1536),
    match_count INT DEFAULT NULL,
    filter JSONB DEFAULT '{}'
) RETURNS TABLE (
    id BIGINT,
    content TEXT,
    metadata JSONB,
    similarity FLOAT
)
LANGUAGE plpgsql
AS $$
#variable_conflict use_column
BEGIN
    RETURN QUERY
    SELECT
        documents_vectors.id,
        documents_vectors.content,
        documents_vectors.metadata,
        1 - (documents_vectors.embedding <=> query_embedding) AS similarity
    FROM documents_vectors
    WHERE documents_vectors.metadata @> filter
    ORDER BY documents_vectors.embedding <=> query_embedding
    LIMIT match_count;
END;
$$;

-- Comentarios sobre el diseño:
-- 1. La tabla documents almacena los metadatos de los archivos subidos
-- 2. La tabla documents_vectors almacena los vectores generados por N8N
-- 3. Se mantiene la relación entre ambas tablas mediante document_id
-- 4. Los índices optimizan las búsquedas por agente, estado de procesamiento y similitud vectorial
-- 5. Los triggers mantienen automáticamente la fecha de actualización