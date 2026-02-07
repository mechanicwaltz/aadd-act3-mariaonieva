-- Script para corregir la tabla pistas y reservas existentes
-- 1. Convierte la columna deporte de ENUM a VARCHAR(50) en pistas
-- 2. Asegura que precio sea DECIMAL(8,2) en reservas

USE club_dama;

-- Cambiar el tipo de columna deporte de ENUM a VARCHAR(50)
ALTER TABLE pistas MODIFY COLUMN deporte VARCHAR(50) NOT NULL;

-- Cambiar el tipo de columna precio a DECIMAL(8,2) si está como otro tipo
-- (En MySQL, esto es seguro incluso si ya es DECIMAL)
ALTER TABLE reservas MODIFY COLUMN precio DECIMAL(8,2) NOT NULL;

-- Verificar los cambios
DESCRIBE pistas;
DESCRIBE reservas;

