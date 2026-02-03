package es.clubdama.dao;
import es.clubdama.model.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;


public class ReservaDao {
    private static final String CREAR_RESERVA_SP = "{CALL sp_crear_reserva(?,?,?,?,?,?)}";

    /**
     * Crea una reserva delegando la inserción en el procedimiento almacenado sp_crear_reserva.
     * Devuelve el id generado para la reserva.
     * @throws IllegalArgumentException si parámetros son inválidos
     * @throws Exception si hay error SQL
     */
    public String crearReserva(String idSocio, String idPista, LocalDate fecha,
                               LocalTime horaIni, int duracionMin) throws Exception {
        if (idSocio == null || idSocio.isEmpty())
            throw new IllegalArgumentException("ID socio no puede ser nulo/vacío");
        if (idPista == null || idPista.isEmpty())
            throw new IllegalArgumentException("ID pista no puede ser nulo/vacío");
        if (fecha == null)
            throw new IllegalArgumentException("Fecha no puede ser nula");
        if (horaIni == null)
            throw new IllegalArgumentException("Hora inicio no puede ser nula");
        if (duracionMin <= 0)
            throw new IllegalArgumentException("Duración debe ser mayor a 0");

        String idReserva = UUID.randomUUID().toString();
        try (Connection con = JdbcUtil.getConnection();
             CallableStatement cs = con.prepareCall(CREAR_RESERVA_SP)) {
            cs.setString(1, idReserva);
            cs.setString(2, idSocio);
            cs.setString(3, idPista);
            cs.setDate(4, Date.valueOf(fecha));
            cs.setTime(5, Time.valueOf(horaIni));
            cs.setInt(6, duracionMin);
            cs.execute();
            return idReserva;
        } catch (SQLException e) {
            throw new Exception("Error creando reserva: " + e.getMessage(), e);
        }
    }

    /**
     * Lista reservas de una pista en una fecha dada usando JPQL con JOIN FETCH para cargar relaciones.
     */
    public List<Reserva> listarPorPistaYFecha(String idPista, LocalDate fecha) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Reserva> q = em.createQuery("SELECT r FROM Reserva r JOIN FETCH r.socio JOIN FETCH r.pista WHERE r.pista.idPista = :idPista AND r.fecha = :fecha", Reserva.class);
            q.setParameter("idPista", idPista);
            q.setParameter("fecha", fecha);
            return q.getResultList();
        } finally { em.close(); }
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
     * Lista reservas futuras de un socio (usada para validar bajas de socios) usando JPQL con JOIN FETCH.
     */
    public List<Reserva> listarPorSocio(String idSocio) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Reserva> q = em.createQuery("SELECT r FROM Reserva r JOIN FETCH r.pista JOIN FETCH r.socio WHERE r.socio.idSocio = :idSocio AND r.fecha >= CURRENT_DATE", Reserva.class);
            q.setParameter("idSocio", idSocio);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lista TODAS las reservas (para el dashboard).
     * Usa JOIN FETCH para cargar socio y pista antes de cerrar EntityManager.
     */
    public List<Reserva> listarTodas() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Reserva> q = em.createQuery(
                "SELECT r FROM Reserva r JOIN FETCH r.pista JOIN FETCH r.socio",
                Reserva.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Cancela (borra) una reserva por su id.
     * @param idReserva identificador de la reserva a cancelar
     * @throws IllegalArgumentException si idReserva es nulo/vacío
     * @throws RuntimeException si hay error en la transacción
     */
    public void cancelarReserva(String idReserva) {
        if (idReserva == null || idReserva.isEmpty())
            throw new IllegalArgumentException("ID reserva no puede ser nulo/vacío");
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Reserva r = em.find(Reserva.class, idReserva);
            if (r != null) em.remove(r);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally { em.close(); }
    }
}
