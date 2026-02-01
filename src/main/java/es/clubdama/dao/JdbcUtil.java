package es.clubdama.dao;
import java.sql.*;


public class JdbcUtil {

    // Ahora la URL/usuario/clave se leen de variables de entorno con valores por defecto
    // Forzamos parámetros para usar utf8mb4 y una collation de conexión estable (evita mixed collations)
    // Usamos por defecto la base de datos `club_dama` porque los scripts en /data la crean con ese nombre
    private static final String URL = System.getenv().getOrDefault(
        "DB_URL",
        "jdbc:mysql://localhost:3306/club_dama?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&connectionCollation=utf8mb4_0900_ai_ci"
    );
    private static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASS = System.getenv().getOrDefault("DB_PASS", "root");
    // Collation esperada por defecto (configurable vía variable de entorno DB_COLLATION)
    // Coincide con la collation usada en data/init_db.sql
    private static final String EXPECTED_COLLATION = System.getenv().getOrDefault("DB_COLLATION", "utf8mb4_0900_ai_ci");

    // Usar el driver de MySQL
    static { try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch(Exception e){ throw new RuntimeException(e);} }

    /**
     * Obtiene una conexión a la base de datos y configura la sesión para usar utf8mb4
     * y la collation detectada o la esperada.
     * @return conexión JDBC lista para usar
     * @throws SQLException si falla la conexión
     */
    public static Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection(URL, USER, PASS);
        try (Statement st = c.createStatement()) {
            // Intentar leer la collation por defecto de la base de datos y aplicarla a la sesión.
            String dbColl = null;
            try (ResultSet rs = st.executeQuery("SELECT @@collation_database AS coll")) {
                if (rs.next()) dbColl = rs.getString("coll");
            } catch (SQLException ex) {

            }
            // Si no detectamos collation, forzamos la collation esperada
            if (dbColl == null || dbColl.isEmpty()) {
                dbColl = EXPECTED_COLLATION;
            } else if (!dbColl.equalsIgnoreCase(EXPECTED_COLLATION)) {
                // Mostrar advertencia
                System.err.println("[JdbcUtil] WARNING: la collation de la base de datos '" + dbColl + "' difiere de la esperada '" + EXPECTED_COLLATION + "'.");
                System.err.println("[JdbcUtil] Si sigue viendo errores 'Illegal mix of collations', ejecute el script de unificación de collation o cambie DB_COLLATION.");
            }
            // Aplicar la collation detectada o el fallback a la sesión para evitar mezclas
            try {
                // Forzar nombres y sets a utf8mb4
                st.execute("SET NAMES 'utf8mb4' COLLATE '" + dbColl + "'");
                st.execute("SET character_set_client = 'utf8mb4'");
                st.execute("SET character_set_connection = 'utf8mb4'");
                st.execute("SET character_set_results = 'utf8mb4'");
                st.execute("SET collation_connection = '" + dbColl + "'");
            } catch (SQLException ex) {

            }
         } catch (SQLException ex) {

         }
         return c;
     }
 }
