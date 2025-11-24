package es.clubdama.dao;
import es.clubdama.model.Pista;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Pista: operaciones CRUD mínimas contra la tabla `pistas`.
 */
public class PistaDao {
    /** Inserta una nueva pista. */
    public void insertar(Pista p) throws SQLException {
        String sql="INSERT INTO pistas(id_pista,deporte,descripcion,disponible) VALUES(?,?,?,?)";
        try(Connection c=JdbcUtil.getConnection(); PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,p.getIdPista());
            ps.setString(2,p.getDeporte());
            ps.setString(3,p.getDescripcion());
            ps.setBoolean(4,p.isDisponible());
            ps.executeUpdate();
        }
    }
    /** Busca una pista por su id. */
    public Pista buscarPorId(String id) throws SQLException {
        String sql="SELECT id_pista,deporte,descripcion,disponible FROM pistas WHERE id_pista=?";
        try(Connection c=JdbcUtil.getConnection(); PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,id);
            try(ResultSet rs=ps.executeQuery()){
                if(rs.next()){
                    Pista p=new Pista();
                    p.setIdPista(rs.getString("id_pista"));
                    p.setDeporte(rs.getString("deporte"));
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setDisponible(rs.getBoolean("disponible"));
                    return p;
                }
            }
        }
        return null;
    }
    /** Lista todas las pistas. */
    public List<Pista> listarTodos() throws SQLException {
        List<Pista> out=new ArrayList<>();
        String sql="SELECT id_pista,deporte,descripcion,disponible FROM pistas";
        try(Connection c=JdbcUtil.getConnection(); PreparedStatement ps=c.prepareStatement(sql);
            ResultSet rs=ps.executeQuery()){
            while(rs.next()){
                Pista p=new Pista();
                p.setIdPista(rs.getString("id_pista"));
                p.setDeporte(rs.getString("deporte"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setDisponible(rs.getBoolean("disponible"));
                out.add(p);
            }
        }
        return out;
    }
    /** Actualiza la disponibilidad de una pista. */
    public void actualizarDisponibilidad(String idPista, boolean disponible) throws SQLException {
        String sql = "UPDATE pistas SET disponible = ? WHERE id_pista = ?";
        try (Connection c = JdbcUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, disponible);
            ps.setString(2, idPista);
            ps.executeUpdate();
        }
    }
}
