-- Script: unify_collation_routines.sql
-- Ejecutar después de convertir tablas: recrea función y procedimiento.
-- Haz backup antes.

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

-- Fin del script de rutinas

