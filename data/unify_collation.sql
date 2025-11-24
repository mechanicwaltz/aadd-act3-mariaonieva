-- Script: unify_collation.sql
-- Objetivo: unificar charset/collation de la base club_dama a utf8mb4_0900_ai_ci
-- IMPORTANTE: Haz backup antes de ejecutar (Data Export en Workbench o mysqldump).

-- 0) (OPCIONAL) Si quieres, descomenta para crear un backup desde Workbench/CMD:
-- mysqldump -u root -proot --databases club_dama > club_dama_backup.sql

-- Crear la BD si no existe con la collation deseada
CREATE DATABASE IF NOT EXISTS club_dama CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- Asegurar collation por defecto
ALTER DATABASE club_dama CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

USE club_dama;

-- Convertir tablas relevantes a utf8mb4_0900_ai_ci (ejecuta una por una si tu BD es grande)
ALTER TABLE socios   CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE pistas   CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE reservas CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- Recomendado: ejecutar después el fichero unify_collation_routines.sql para recrear routines
-- SOURCE data/unify_collation_routines.sql;

-- Fin del script
