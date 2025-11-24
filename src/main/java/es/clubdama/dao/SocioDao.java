package es.clubdama.dao;
import es.clubdama.model.Socio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Socio: operaciones CRUD mínimas contra la tabla `socios`.
 */
public class SocioDao {
    /**
     * Inserta un nuevo socio en la base de datos.
     */
    public void insertar(Socio s) throws SQLException {
        String sql = "INSERT INTO socios(id_socio,dni,nombre,apellidos,telefono,email) VALUES(?,?,?,?,?,?)";
        try(Connection c=JdbcUtil.getConnection(); PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,s.getIdSocio());
            ps.setString(2,s.getDni());
            ps.setString(3,s.getNombre());
            ps.setString(4,s.getApellidos());
            ps.setString(5,s.getTelefono());
            ps.setString(6,s.getEmail());
            ps.executeUpdate();
        }
    }

    /**
     * Busca un socio por su identificador.
     * @return objeto Socio o null si no existe
     */
    public Socio buscarPorId(String id) throws SQLException {
        String sql="SELECT id_socio,dni,nombre,apellidos,telefono,email FROM socios WHERE id_socio=?";
        try(Connection c=JdbcUtil.getConnection(); PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,id);
            try(ResultSet rs=ps.executeQuery()){
                if(rs.next()){
                    Socio s=new Socio();
                    s.setIdSocio(rs.getString("id_socio"));
                    s.setDni(rs.getString("dni"));
                    s.setNombre(rs.getString("nombre"));
                    s.setApellidos(rs.getString("apellidos"));
                    s.setTelefono(rs.getString("telefono"));
                    s.setEmail(rs.getString("email"));
                    return s;
                }
            }
        }
        return null;
    }

    /**
     * Lista todos los socios.
     */
    public List<Socio> listarTodos() throws SQLException {
        List<Socio> out=new ArrayList<>();
        String sql="SELECT id_socio,dni,nombre,apellidos,telefono,email FROM socios";
        try(Connection c=JdbcUtil.getConnection(); PreparedStatement ps=c.prepareStatement(sql);
            ResultSet rs=ps.executeQuery()){
            while(rs.next()){
                Socio s=new Socio();
                s.setIdSocio(rs.getString("id_socio"));
                s.setDni(rs.getString("dni"));
                s.setNombre(rs.getString("nombre"));
                s.setApellidos(rs.getString("apellidos"));
                s.setTelefono(rs.getString("telefono"));
                s.setEmail(rs.getString("email"));
                out.add(s);
            }
        }
        return out;
    }

    // Nuevo: eliminar socio por id
    public void borrarPorId(String id) throws SQLException {
        String sql = "DELETE FROM socios WHERE id_socio = ?";
        try (Connection c = JdbcUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }
}
