package es.clubdama.dao;
import es.clubdama.model.Pista;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * DAO para la entidad Pista: operaciones CRUD mínimas contra la tabla `pistas`.
 */
public class PistaDao {
    /** Inserta una nueva pista. */
    public void insertar(Pista p) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally { em.close(); }
    }
    /** Busca una pista por su id. */
    public Pista buscarPorId(String id) {
        if (id == null || id.isEmpty()) return null;
        EntityManager em = JpaUtil.getEntityManager();
        try { return em.find(Pista.class,id); } finally { em.close(); }
    }
    /** Lista todas las pistas. */
    public List<Pista> listarTodos() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Pista> q = em.createQuery("SELECT p FROM Pista p", Pista.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /** Lista todas las pistas con sus reservas cargadas. */
    public List<Pista> listarTodosConReservas() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Pista> q = em.createQuery(
                "SELECT DISTINCT p FROM Pista p LEFT JOIN FETCH p.reservas",
                Pista.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }
    /** Actualiza la disponibilidad de una pista. */
    public void actualizarDisponibilidad(String idPista, boolean disponible) {
        if (idPista == null || idPista.isEmpty())
            throw new IllegalArgumentException("ID de pista no puede ser nulo o vacío");
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Pista p = em.find(Pista.class, idPista);
            if (p != null) {
                p.setDisponible(disponible);
                em.merge(p);
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally { em.close(); }
    }
}
