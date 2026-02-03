package es.clubdama.dao;
import es.clubdama.model.Socio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * - Escritura: transacción begin/commit/rollback
 * - Lectura: sin transacción (read-only)
 * - Cierre: siempre en finally block
 */
public class SocioDao {
    /**
     * Inserta un nuevo socio en la base de datos usando JPA.
     * @throws RuntimeException si hay error de persistencia o constraint
     */
    public void insertar(Socio s) {
        if (s == null) throw new IllegalArgumentException("Socio no puede ser nulo");
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(s);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    /**
     * Busca un socio por su identificador.
     * @return objeto Socio o null si no existe
     */
    public Socio buscarPorId(String id) {
        if (id == null || id.isEmpty()) return null;
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(Socio.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Lista todos los socios.
     * Nota: Las reservas se cargan como lazy (no se cargan aquí).
     * Si necesitas reservas, usa LEFT JOIN FETCH.
     */
    public List<Socio> listarTodos() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Socio> q = em.createQuery("SELECT s FROM Socio s", Socio.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lista todos los socios con sus reservas cargadas.
     * Usa LEFT JOIN FETCH para evitar LazyInitializationException.
     */
    public List<Socio> listarTodosConReservas() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Socio> q = em.createQuery(
                "SELECT DISTINCT s FROM Socio s LEFT JOIN FETCH s.reservas",
                Socio.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    // Nuevo: eliminar socio por id
    public void borrarPorId(String id) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("ID no puede ser nulo o vacío");
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Socio s = em.find(Socio.class, id);
            if (s != null) em.remove(s);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}
