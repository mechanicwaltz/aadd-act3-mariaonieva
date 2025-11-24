package es.clubdama.service;
import es.clubdama.dao.ReservaDao;
import es.clubdama.dao.SocioDao;
import es.clubdama.dao.PistaDao;
import es.clubdama.model.Socio;
import es.clubdama.model.Pista;
import es.clubdama.util.ValidationUtil;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Servicio que implementa la lógica de negocio del club.
 *
 * Nota importante: la creación de reservas delega en el procedimiento almacenado
 * definido en la base de datos (sp_crear_reserva). El cálculo del precio se realiza
 * mediante la función almacenada fn_precio_reserva (llamada desde el procedimiento).
 */
public class ClubDeportivo {
    private final ReservaDao reservaDao;
    private final SocioDao socioDao;
    private final PistaDao pistaDao;

    // Constructor por defecto (usa DAOs concretos)
    public ClubDeportivo() {
        this.reservaDao = new ReservaDao();
        this.socioDao = new SocioDao();
        this.pistaDao = new PistaDao();
    }

    // Constructor para inyección de dependencias (útil para tests)
    public ClubDeportivo(ReservaDao reservaDao, SocioDao socioDao, PistaDao pistaDao) {
        this.reservaDao = reservaDao;
        this.socioDao = socioDao;
        this.pistaDao = pistaDao;
    }

    /**
     * Crea una reserva validadando reglas básicas y delegando la inserción al procedimiento sp_crear_reserva.
     * @return id de la reserva creada (tal como devuelve el DAO/procedimiento)
     */
    public String crearReserva(String idSocio, String idPista, LocalDate fecha, LocalTime hora, int duracion) throws Exception {
        if(duracion<=0) throw new IllegalArgumentException("Duración inválida");
        // optionally check existence
        Socio s = socioDao.buscarPorId(idSocio);
        if(s==null) throw new IllegalArgumentException("Socio inexistente: "+idSocio);
        Pista p = pistaDao.buscarPorId(idPista);
        if(p==null) throw new IllegalArgumentException("Pista inexistente: "+idPista);
        if(!p.isDisponible()) throw new IllegalStateException("Pista no operativa: "+idPista);
        // delegate to stored procedure for overlaps
        return reservaDao.crearReserva(idSocio,idPista,fecha,hora,duracion);
    }

    public List<Pista> listarPistas() throws Exception { return pistaDao.listarTodos(); }
    public List<Socio> listarSocios() throws Exception { return socioDao.listarTodos(); }
    public List<es.clubdama.model.Reserva> reservasPorPistaYFecha(String idPista, LocalDate fecha) throws Exception {
        return reservaDao.listarPorPistaYFecha(idPista, fecha);
    }

    // Nuevo: crear socio
    public void crearSocio(Socio s) throws Exception {
        if (s == null) throw new IllegalArgumentException("Socio nulo");
        if (s.getIdSocio() == null || s.getIdSocio().isEmpty()) throw new IllegalArgumentException("idSocio requerido");
        if (existeSocio(s.getIdSocio())) throw new IllegalArgumentException("idSocio ya existe: " + s.getIdSocio());
        if (s.getEmail() != null && !s.getEmail().isEmpty() && !ValidationUtil.isValidEmail(s.getEmail()))
            throw new IllegalArgumentException("Email inválido");
        socioDao.insertar(s);
    }

    private boolean existeSocio(String id) throws Exception {
        return socioDao.buscarPorId(id) != null;
    }

    // Nuevo: crear pista
    public void crearPista(Pista p) throws Exception {
        if (p == null) throw new IllegalArgumentException("Pista nula");
        if (p.getIdPista() == null || p.getIdPista().isEmpty()) throw new IllegalArgumentException("idPista requerido");
        if (existePista(p.getIdPista())) throw new IllegalArgumentException("idPista ya existe: " + p.getIdPista());
        // Validar deporte: debe estar entre las opciones del ENUM de la BD
        String[] opciones = new String[]{"tenis","padel","futbol_sala"};
        if (p.getDeporte() == null) throw new IllegalArgumentException("deporte requerido");
        boolean ok = false;
        for (String o : opciones) if (o.equals(p.getDeporte())) { ok = true; break; }
        if (!ok) throw new IllegalArgumentException("Deporte inválido. Opciones: tenis, padel, futbol_sala");
        pistaDao.insertar(p);
    }

    private boolean existePista(String id) throws Exception {
        return pistaDao.buscarPorId(id) != null;
    }

    // Nuevo: validar hora (cadena HH:mm)
    public boolean validarHoraString(String hora) {
        return ValidationUtil.isValidHora(hora);
    }

    // Nuevo: cancelar reserva
    public void cancelarReserva(String idReserva) throws Exception {
        reservaDao.cancelarReserva(idReserva);
    }

    // Nuevo: cambiar disponibilidad de pista
    public void cambiarDisponibilidadPista(String idPista, boolean disponible) throws Exception {
        Pista p = pistaDao.buscarPorId(idPista);
        if (p == null) throw new IllegalArgumentException("Pista inexistente: " + idPista);
        // actualizar estado
        String sqlErr = ""; // placeholder
        // No hay método DAO para actualizar disponibilidad; haremos una pequeña operación via insertar/Delete?
        // Mejor añadir un método en PistaDao: actualizarDisponibilidad
        pistaDao.actualizarDisponibilidad(idPista, disponible);
    }

    // Nuevo: calcular precio a través de DAO
    public double calcularPrecioReserva(int minutos) throws Exception {
        return reservaDao.calcularPrecio(minutos);
    }

    // Nuevo: listar reservas de hoy (agregando reservas por pista)
    public java.util.List<es.clubdama.model.Reserva> listarReservasHoy() throws Exception {
        java.util.List<es.clubdama.model.Reserva> out = new java.util.ArrayList<>();
        java.time.LocalDate hoy = java.time.LocalDate.now();
        for (Pista p : pistaDao.listarTodos()) {
            out.addAll(reservaDao.listarPorPistaYFecha(p.getIdPista(), hoy));
        }
        return out;
    }

    // Nuevo: eliminar socio (ya existe bajaSocio) - ya implementado
    public void bajaSocio(String idSocio) throws Exception {
        if (idSocio == null || idSocio.isEmpty()) throw new IllegalArgumentException("idSocio requerido");
        java.util.List<es.clubdama.model.Reserva> reservas = reservaDao.listarPorSocio(idSocio);
        if (reservas != null && !reservas.isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el socio: tiene reservas activas");
        }
        socioDao.borrarPorId(idSocio);
    }
}
