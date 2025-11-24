-- fix_collation_club_dama.sql
-- Script seguro para unificar charset y collation de la base `club_dama` a utf8mb4_0900_ai_ci
-- Antes de ejecutar: HAZ UN BACKUP COMPLETO de la base de datos (mysqldump o Workbench Data Export).
-- Ejemplo de backup (PowerShell):
-- mysqldump -u root -p --databases club_dama > C:\path\to\backup\club_dama_backup.sql

-- 1) Comprobar estado actual
SELECT @@character_set_database AS character_set_database, @@collation_database AS collation_database;
SELECT TABLE_NAME, COLUMN_NAME, CHARACTER_SET_NAME, COLLATION_NAME
  FROM information_schema.COLUMNS
 WHERE TABLE_SCHEMA = 'club_dama'
   AND (CHARACTER_SET_NAME IS NOT NULL OR COLLATION_NAME IS NOT NULL)
 ORDER BY TABLE_NAME, COLUMN_NAME;

-- 2) Asegurar que trabajamos sobre la BD correcta
USE `club_dama`;

-- 3) Establecer la collation por defecto de la base a utf8mb4_0900_ai_ci
ALTER DATABASE `club_dama` CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- 4) Convertir tablas a utf8mb4_0900_ai_ci
ALTER TABLE `socios`   CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE `pistas`   CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE `reservas` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- 5) Normalizar posibles valores acentuados en pistas.deporte
-- Si en alguna migración anterior se usaron 'pádel' o 'fútbol sala', los normalizamos
UPDATE `pistas` SET `deporte`='padel' WHERE `deporte` IN ('pádel','Pádel','PADÃ‰L','PADÉL');
UPDATE `pistas` SET `deporte`='futbol_sala' WHERE `deporte` IN ('fútbol sala','Fútbol sala','futbol sala','Futbol sala');
-- Nota: las variantes raras (codificación dañada) pueden requerir correcciones manuales.

-- 6) Asegurar definición correcta del ENUM en `pistas.deporte` con la collation adecuada
ALTER TABLE `pistas`
  MODIFY `deporte` ENUM('tenis','padel','futbol_sala')
  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL;

-- 7) Recrear funciones y procedimientos (routines) con la collation correcta
-- Eliminar y volver a crear la función y el procedimiento asegura que su código no arrastre diferencias
DROP FUNCTION IF EXISTS fn_precio_reserva;
DELIMITER $$
CREATE FUNCTION fn_precio_reserva(minutos INT) RETURNS DECIMAL(8,2)
DETERMINISTIC
BEGIN
  DECLARE precio DECIMAL(8,2);
  SET precio = (CEIL(minutos / 30) * 5.00);
  RETURN precio;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_crear_reserva;
DELIMITER $$
CREATE PROCEDURE sp_crear_reserva(
  IN p_id_reserva VARCHAR(36),
  IN p_id_socio VARCHAR(36),
  IN p_id_pista VARCHAR(36),
  IN p_fecha DATE,
  IN p_hora_ini TIME,
  IN p_duracion INT
)
BEGIN
  DECLARE p_precio DECIMAL(8,2);
  IF p_duracion <= 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duración inválida';
  END IF;
  SET p_precio = fn_precio_reserva(p_duracion);
  INSERT INTO reservas(id_reserva, id_socio, id_pista, fecha, hora_inicio, duracion_min, precio)
  VALUES (p_id_reserva, p_id_socio, p_id_pista, p_fecha, p_hora_ini, p_duracion, p_precio);
END$$
DELIMITER ;

-- 8) Comprobaciones finales: listar collations actuales por tabla/columna
SELECT TABLE_NAME, COLUMN_NAME, CHARACTER_SET_NAME, COLLATION_NAME
  FROM information_schema.COLUMNS
 WHERE TABLE_SCHEMA = 'club_dama'
 ORDER BY TABLE_NAME, COLUMN_NAME;

SELECT @@character_set_database AS character_set_database, @@collation_database AS collation_database;

-- Fin del script. Si observas valores extraños, revisa el backup y arregla manualmente los valores individuales.

