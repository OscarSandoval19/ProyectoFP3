-- Limpiar tabla por si hay datos previos
DELETE FROM nodes;

-- Insertar la raíz principal
INSERT INTO nodes (id, name, type, parent_id) VALUES ('root_01', 'Documentos', 'FOLDER', NULL);

-- Insertar carpetas de primer nivel
INSERT INTO nodes (id, name, type, parent_id) VALUES ('node_02', 'Universidad', 'FOLDER', 'root_01');
INSERT INTO nodes (id, name, type, parent_id) VALUES ('node_03', 'Personal', 'FOLDER', 'root_01');

-- Insertar carpetas anidadas (Nivel 2)
INSERT INTO nodes (id, name, type, parent_id) VALUES ('node_04', 'Tareas', 'FOLDER', 'node_02');
INSERT INTO nodes (id, name, type, parent_id) VALUES ('node_05', 'Fotos', 'FOLDER', 'node_03');

-- Insertar archivos (Hojas del árbol)
INSERT INTO nodes (id, name, type, parent_id) VALUES ('file_01', 'tarea_mate.pdf', 'FILE', 'node_04');
INSERT INTO nodes (id, name, type, parent_id) VALUES ('file_02', 'proyecto_final.zip', 'FILE', 'node_04');
INSERT INTO nodes (id, name, type, parent_id) VALUES ('file_03', 'vacaciones_playa.jpg', 'FILE', 'node_05');