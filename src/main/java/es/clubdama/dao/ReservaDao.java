package es.clubdama.dao;
import es.clubdama.model.Reserva;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO para reservas. Contiene operaciones que usan la tabla `reservas` y las
 * rutinas (procedimiento sp_crear_reserva y función fn_precio_reserva).
 */
public class ReservaDao {
    /**
     * Crea una reserva delegando la inserción en el procedimiento almacenado sp_crear_reserva.
     * Devuelve el id generado para la reserva.
     */
    public String crearReserva(String idSocio,String idPista,LocalDate fecha,
                               LocalTime horaIni,int duracionMin) throws Exception {
        String idReserva = UUID.randomUUID().toString();
        String sql = "{CALL sp_crear_reserva(?,?,?,?,?,?)}";
        try(Connection con=JdbcUtil.getConnection(); CallableStatement cs=con.prepareCall(sql)){
            cs.setString(1,idReserva);
            cs.setString(2,idSocio);
            cs.setString(3,idPista);
            cs.setDate(4,Date.valueOf(fecha));
            cs.setTime(5,Time.valueOf(horaIni));
            cs.setInt(6,duracionMin);
            cs.execute();
            return idReserva;
        } catch(SQLException e){
            throw new Exception("Error creando reserva: "+e.getMessage(),e);
        }
    }
    /**
     * Lista reservas de una pista en una fecha dada.
     */
    public List<Reserva> listarPorPistaYFecha(String idPista, LocalDate fecha) throws SQLException {
        String sql="SELECT id_reserva,id_socio,id_pista,fecha,hora_inicio,duracion_min,precio FROM reservas WHERE id_pista=? AND fecha=?";
        List<Reserva> out=new ArrayList<>();
        try(Connection c=JdbcUtil.getConnection(); PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,idPista);
            ps.setDate(2,Date.valueOf(fecha));
            try(ResultSet rs=ps.executeQuery()){
                while(rs.next()){
                    Reserva r=new Reserva();
                    r.setIdReserva(rs.getString("id_reserva"));
                    r.setIdSocio(rs.getString("id_socio"));
                    r.setIdPista(rs.getString("id_pista"));
                    r.setFecha(rs.getDate("fecha").toLocalDate());
                    r.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
                    r.setDuracionMin(rs.getInt("duracion_min"));
                    r.setPrecio(rs.getDouble("precio"));
                    out.add(r);
                }
            }
        }
        return out;
    }

    /**
     * Invoca la función almacenada fn_precio_reserva para calcular el precio.
     */
    public double calcularPrecio(int minutos) throws SQLException {
        String sql = "SELECT fn_precio_reserva(?) AS precio";
        try (Connection c = JdbcUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, minutos);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("precio");
                }
            }
        }
        throw new SQLException("No se obtuvo precio de la función almacenada fn_precio_reserva");
    }

    /**
     * Lista reservas futuras de un socio (usada para validar bajas de socios).
     */
    public List<Reserva> listarPorSocio(String idSocio) throws SQLException {
        String sql = "SELECT id_reserva,id_socio,id_pista,fecha,hora_inicio,duracion_min,precio FROM reservas WHERE id_socio = ? AND fecha >= CURDATE()";
        List<Reserva> out = new ArrayList<>();
        try (Connection c = JdbcUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, idSocio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reserva r = new Reserva();
                    r.setIdReserva(rs.getString("id_reserva"));
                    r.setIdSocio(rs.getString("id_socio"));
                    r.setIdPista(rs.getString("id_pista"));
                    r.setFecha(rs.getDate("fecha").toLocalDate());
                    r.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
                    r.setDuracionMin(rs.getInt("duracion_min"));
                    r.setPrecio(rs.getDouble("precio"));
                    out.add(r);
                }
            }
        }
        return out;
    }

    /**
     * Cancela (borra) una reserva por su id.
     */
    public void cancelarReserva(String idReserva) throws SQLException {
        String sql = "DELETE FROM reservas WHERE id_reserva = ?";
        try (Connection c = JdbcUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, idReserva);
            ps.executeUpdate();
        }
    }
}
