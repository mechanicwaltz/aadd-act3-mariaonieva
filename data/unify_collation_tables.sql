-- Script: unify_collation_tables.sql
-- Ejecutar primero: crea la BD si no existe, cambia la collation por defecto y convierte las tablas.
-- Haz backup antes.

-- Crear la BD si no existe con la collation deseada
CREATE DATABASE IF NOT EXISTS club_dama CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- Asegurar que la base tiene la collation por defecto correcta
ALTER DATABASE club_dama CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

USE club_dama;

-- Convertir tablas relevantes a utf8mb4_0900_ai_ci (ejecuta una por una si tu BD es grande)
ALTER TABLE socios   CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE pistas   CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE reservas CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- Fin del script de tablas
