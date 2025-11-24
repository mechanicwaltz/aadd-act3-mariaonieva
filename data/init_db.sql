-- Script de inicialización para pruebas locales (MySQL)
-- Crea tablas mínimas, función y procedimiento si no existen.

CREATE DATABASE IF NOT EXISTS club_dama CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE club_dama;

-- Tabla socios
CREATE TABLE IF NOT EXISTS socios (
  id_socio VARCHAR(36) PRIMARY KEY,
  dni VARCHAR(16) NOT NULL UNIQUE,
  nombre VARCHAR(80) NOT NULL,
  apellidos VARCHAR(120) NOT NULL,
  telefono VARCHAR(20),
  email VARCHAR(120) UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabla pistas
CREATE TABLE IF NOT EXISTS pistas (
  id_pista VARCHAR(36) PRIMARY KEY,
  deporte ENUM('tenis','padel','futbol_sala') NOT NULL,
  descripcion VARCHAR(200),
  disponible TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabla reservas
CREATE TABLE IF NOT EXISTS reservas (
  id_reserva VARCHAR(36) PRIMARY KEY,
  id_socio VARCHAR(36) NOT NULL,
  id_pista VARCHAR(36) NOT NULL,
  fecha DATE NOT NULL,
  hora_inicio TIME NOT NULL,
  duracion_min INT NOT NULL,
  precio DECIMAL(8,2) NOT NULL,
  FOREIGN KEY (id_socio) REFERENCES socios(id_socio) ON UPDATE CASCADE,
  FOREIGN KEY (id_pista) REFERENCES pistas(id_pista) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Función: fn_precio_reserva
-- Ejemplo: precio = 5.00 por cada 30 minutos (ajusta según necesidades)
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

-- Procedimiento: sp_crear_reserva
-- Inserta la reserva y calcula precio usando la función fn_precio_reserva
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
  -- Validaciones simples (lanzan error si no cumplen)
  IF p_duracion <= 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duración inválida';
  END IF;
  -- Calcular precio usando la función
  SET p_precio = fn_precio_reserva(p_duracion);
  -- Insertar reserva
  INSERT INTO reservas(id_reserva, id_socio, id_pista, fecha, hora_inicio, duracion_min, precio)
  VALUES (p_id_reserva, p_id_socio, p_id_pista, p_fecha, p_hora_ini, p_duracion, p_precio);
END$$
DELIMITER ;

-- Índices adicionales
CREATE INDEX IF NOT EXISTS ix_res_pista_fecha ON reservas(id_pista, fecha, hora_inicio);
CREATE INDEX IF NOT EXISTS ix_res_socio_fecha ON reservas(id_socio, fecha);

-- Fin del script
