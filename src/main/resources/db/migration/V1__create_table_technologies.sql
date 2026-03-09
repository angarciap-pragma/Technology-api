-- Crea la tabla principal de tecnologias.
CREATE TABLE IF NOT EXISTS technologies (
    -- Crea la llave primaria autoincremental.
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    -- Crea la columna de nombre con maximo 50 caracteres.
    name VARCHAR(50) NOT NULL,
    -- Crea la columna normalizada para validar unicidad sin mayusculas.
    normalized_name VARCHAR(50) NOT NULL,
    -- Crea la columna de descripcion con maximo 90 caracteres.
    description VARCHAR(90) NOT NULL,
    -- Crea la restriccion unica para nombre normalizado.
    CONSTRAINT uk_technologies_normalized_name UNIQUE (normalized_name)
);

