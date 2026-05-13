-- Crear la tabla para el sistema de gestión de estructuras jerárquicas
CREATE TABLE IF NOT EXISTS nodes (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    parent_id VARCHAR(255),
    CONSTRAINT fk_parent_node FOREIGN KEY (parent_id) REFERENCES nodes(id)
);

-- Índice para mejorar la velocidad de búsqueda de hijos
CREATE INDEX IF NOT EXISTS idx_nodes_parent ON nodes(parent_id);