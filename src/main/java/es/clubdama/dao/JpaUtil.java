package es.clubdama.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilidad simple para obtener EntityManager desde el persistence unit definido.
 * Registra un shutdown hook para garantizar el cierre del EntityManagerFactory
 * en la terminación de la JVM.
 */
public class JpaUtil {
    private static final EntityManagerFactory emf = buildEntityManagerFactory();

    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            // Leer variables de entorno (si existen) y pasarlas como propiedades a JPA
            Map<String, Object> props = new HashMap<>();
            String dbUrl = System.getenv("DB_URL");
            String dbUser = System.getenv("DB_USER");
            String dbPass = System.getenv("DB_PASS");
            // Valores por defecto (coinciden con persistence.xml)
            String defaultUrl = "jdbc:mysql://localhost:3306/club_dama?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
            String defaultUser = "root";
            String defaultPass = "gatito";

            if (dbUrl == null || dbUrl.isEmpty()) dbUrl = defaultUrl;
            if (dbUser == null || dbUser.isEmpty()) dbUser = defaultUser;
            if (dbPass == null || dbPass.isEmpty()) dbPass = defaultPass;

            props.put("jakarta.persistence.jdbc.url", dbUrl);
            props.put("jakarta.persistence.jdbc.user", dbUser);
            props.put("jakarta.persistence.jdbc.password", dbPass);
            props.put("jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");

            EntityManagerFactory factory = Persistence.createEntityManagerFactory("club-dama-pu", props);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (factory != null && factory.isOpen()) factory.close();
                } catch (Throwable t) { /* ignore */ }
            }));
            return factory;
        } catch (Throwable ex) {
            System.err.println("Initial EntityManagerFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) emf.close();
    }
}
