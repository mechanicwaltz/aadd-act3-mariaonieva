# 📋 REVISIÓN COMPLETA DEL PROYECTO - JPA E INTEGRACIÓN

## ✅ ASPECTOS CUMPLIDOS

### 1. **Entidades Anotadas con @Entity y @Table**

#### ✅ `Socio.java`
- `@Entity` ✓
- `@Table(name = "socios")` ✓
- Campos anotados con `@Column` con propiedades correctas ✓
- `@Id` en idSocio ✓
- `@OneToMany(mappedBy = "socio")` con relación bidireccional ✓
- `CascadeType.PERSIST` ✓

#### ✅ `Pista.java`
- `@Entity` ✓
- `@Table(name = "pistas")` ✓
- Campos anotados con `@Column` ✓
- `@Id` en idPista ✓
- `@OneToMany(mappedBy = "pista")` con relación bidireccional ✓

#### ✅ `Reserva.java`
- `@Entity` ✓
- `@Table(name = "reservas")` ✓
- `@Id` en idReserva ✓
- **FIX APLICADO**: Campo `precio` cambió de `double` a `BigDecimal` ✓
- `@ManyToOne` hacia Socio y Pista ✓
- `@JoinColumn` correctamente referenciado ✓
- `FetchType.LAZY` en relaciones ✓

---

### 2. **Configuración de Persistencia (persistence.xml)**

#### ✅ Dialecto Correcto
```xml
<property name="hibernate.dialect" value="org.hibernate.dialect.MariaDBDialect"/>
```
✓ MariaDB/MySQL correctamente configurado

#### ✅ Modo de Validación
```xml
<property name="hibernate.hbm2ddl.auto" value="validate"/>
```
✓ Valida esquema sin modificarlo (ideal para producción)

#### ✅ Propiedades JDBC
✓ URL correcta con parámetros `useSSL=false`, `serverTimezone=UTC`, `useUnicode=true`, `characterEncoding=UTF-8`
✓ Usuario y contraseña configurables

#### ✅ Configuración de Pool
✓ `connection.provider_disables_autocommit=true`

---

### 3. **Transacciones en DAOs**

#### ✅ `SocioDao.java`

**Insertar:**
```java
public void insertar(Socio s) {
    EntityManager em = JpaUtil.getEntityManager();
    try {
        em.getTransaction().begin();
        em.persist(s);
        em.getTransaction().commit();      // ✅ COMMIT
    } catch (RuntimeException ex) {
        if (em.getTransaction().isActive()) 
            em.getTransaction().rollback();  // ✅ ROLLBACK
        throw ex;
    } finally {
        em.close();                          // ✅ CIERRE
    }
}
```
✓ Begin/Commit/Rollback correctamente implementado
✓ EntityManager cerrado en finally

**Búsqueda por ID:**
```java
public Socio buscarPorId(String id) {
    EntityManager em = JpaUtil.getEntityManager();
    try {
        return em.find(Socio.class, id);  // ✅ Sin transacción (read-only)
    } finally {
        em.close();                        // ✅ Cierre
    }
}
```
✓ Lectura sin transacción (correcto)
✓ EntityManager cerrado

**Listado:**
```java
public List<Socio> listarTodos() {
    EntityManager em = JpaUtil.getEntityManager();
    try {
        TypedQuery<Socio> q = em.createQuery("SELECT s FROM Socio s", Socio.class);
        return q.getResultList();          // ✅ JPQL
    } finally {
        em.close();                        // ✅ Cierre
    }
}
```
✓ Usa `TypedQuery` (JPQL tipado)
✓ EntityManager cerrado

**Borrar:**
```java
public void borrarPorId(String id) {
    EntityManager em = JpaUtil.getEntityManager();
    try {
        em.getTransaction().begin();
        Socio s = em.find(Socio.class, id);
        if (s != null) em.remove(s);
        em.getTransaction().commit();      // ✅ COMMIT
    } catch (RuntimeException ex) {
        if (em.getTransaction().isActive()) 
            em.getTransaction().rollback();  // ✅ ROLLBACK
        throw ex;
    } finally {
        em.close();                        // ✅ Cierre
    }
}
```
✓ Transacción correcta

---

#### ✅ `PistaDao.java`

**Insertar, Búsqueda, Listado:** Mismo patrón que SocioDao ✓

**Actualizar Disponibilidad:**
```java
public void actualizarDisponibilidad(String idPista, boolean disponible) {
    EntityManager em = JpaUtil.getEntityManager();
    try {
        em.getTransaction().begin();
        Pista p = em.find(Pista.class, idPista);
        if (p != null) {
            p.setDisponible(disponible);
            em.merge(p);                  // ✅ MERGE para update
        }
        em.getTransaction().commit();     // ✅ COMMIT
    } catch (RuntimeException ex) {
        if (em.getTransaction().isActive()) 
            em.getTransaction().rollback();  // ✅ ROLLBACK
        throw ex;
    } finally {
        em.close();                       // ✅ Cierre
    }
}
```
✓ Usa `em.merge()` correctamente
✓ Transacción correcta

---

#### ✅ `ReservaDao.java`

**Crear Reserva (vía Procedimiento Almacenado):**
```java
public String crearReserva(String idSocio, String idPista, LocalDate fecha,
                           LocalTime horaIni, int duracionMin) throws Exception {
    String idReserva = UUID.randomUUID().toString();
    try (Connection con = JdbcUtil.getConnection(); 
         CallableStatement cs = con.prepareCall(CREAR_RESERVA_SP)) {
        cs.setString(1, idReserva);
        // ... más parámetros
        cs.execute();                     // ✅ Ejecución
        return idReserva;
    } catch (SQLException e) {
        throw new Exception("Error creando reserva: " + e.getMessage(), e);
    }
}
```
✓ Usa procedimiento almacenado (correcto para lógica compleja)
✓ Try-with-resources para cierre automático
✓ Gestión de excepciones

**Listar por Pista y Fecha (con JOIN FETCH):**
```java
public List<Reserva> listarPorPistaYFecha(String idPista, LocalDate fecha) {
    EntityManager em = JpaUtil.getEntityManager();
    try {
        TypedQuery<Reserva> q = em.createQuery(
            "SELECT r FROM Reserva r JOIN FETCH r.socio JOIN FETCH r.pista " +
            "WHERE r.pista.idPista = :idPista AND r.fecha = :fecha", 
            Reserva.class);
        q.setParameter("idPista", idPista);
        q.setParameter("fecha", fecha);
        return q.getResultList();         // ✅ JOIN FETCH
    } finally {
        em.close();                       // ✅ Cierre
    }
}
```
✅ **EXCELENTE**: Usa `JOIN FETCH` para cargar relaciones (evita lazy loading)
✅ Consulta tipada
✅ Cierre de recursos

**Calcular Precio:**
```java
public double calcularPrecio(int minutos) throws SQLException {
    String sql = "SELECT fn_precio_reserva(?) AS precio";
    try (Connection c = JdbcUtil.getConnection(); 
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, minutos);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("precio"); // ✅ Función BD
            }
        }
    }
    throw new SQLException("No se obtuvo precio");
}
```
✓ Usa función almacenada en BD
✓ Try-with-resources

**Cancelar Reserva:**
```java
public void cancelarReserva(String idReserva) {
    EntityManager em = JpaUtil.getEntityManager();
    try {
        em.getTransaction().begin();
        Reserva r = em.find(Reserva.class, idReserva);
        if (r != null) em.remove(r);
        em.getTransaction().commit();     // ✅ COMMIT
    } catch (RuntimeException ex) {
        if (em.getTransaction().isActive()) 
            em.getTransaction().rollback();  // ✅ ROLLBACK
        throw ex;
    } finally {
        em.close();                       // ✅ Cierre
    }
}
```
✓ Transacción correcta

---

### 4. **JpaUtil: Gestión del EntityManagerFactory**

```java
public class JpaUtil {
    private static final EntityManagerFactory emf = buildEntityManagerFactory();

    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            // Leer variables de entorno para configuración
            Map<String, Object> props = new HashMap<>();
            String dbUrl = System.getenv("DB_URL");
            // ... fallback a defaults
            
            EntityManagerFactory factory = 
                Persistence.createEntityManagerFactory("club-dama-pu", props);
            
            // ✅ SHUTDOWN HOOK
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (factory != null && factory.isOpen()) 
                        factory.close();
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
        if (emf != null && emf.isOpen()) 
            emf.close();  // ✅ Método para cierre manual
    }
}
```

✅ EntityManagerFactory como singleton estático
✅ Shutdown hook registrado para cierre automático
✅ Método `close()` público para cierre explícito
✅ Manejo de excepciones en inicialización

---

### 5. **Cierre de Recursos en Aplicación**

#### ✅ `MainApp.java`
```java
@Override
public void stop() throws Exception {
    // Cerrar EntityManagerFactory para liberar recursos al salir
    try {
        JpaUtil.close();               // ✅ CIERRE EXPLÍCITO
    } finally {
        super.stop();
    }
}
```

✅ EntityManagerFactory cerrado cuando la aplicación termina
✅ En bloque finally (garantizado)

---

### 6. **Consultas JPQL y JOIN FETCH**

#### ✅ Consultas Presentes

1. **SocioDao.listarTodos():**
   ```java
   SELECT s FROM Socio s
   ```
   ✅ JPQL simple

2. **PistaDao.listarTodos():**
   ```java
   SELECT p FROM Pista p
   ```
   ✅ JPQL simple

3. **ReservaDao.listarPorPistaYFecha():**
   ```java
   SELECT r FROM Reserva r JOIN FETCH r.socio JOIN FETCH r.pista
   WHERE r.pista.idPista = :idPista AND r.fecha = :fecha
   ```
   ✅ **EXCELENTE**: JOIN FETCH para cargar relaciones
   ✅ Parámetros nombrados

4. **ReservaDao.listarPorSocio():**
   ```java
   SELECT r FROM Reserva r JOIN FETCH r.pista JOIN FETCH r.socio
   WHERE r.socio.idSocio = :idSocio AND r.fecha >= CURRENT_DATE
   ```
   ✅ JOIN FETCH
   ✅ Condición de fecha

---

## ⚠️ ASPECTOS A MEJORAR

### 1. **EntityManager en Lecturas Sin Transacción**

**Problema:** En `listarTodos()` y `buscarPorId()`, el EntityManager se cierra inmediatamente después de la consulta. Si la entidad tiene lazy loading, acceder a relaciones después del cierre causará `LazyInitializationException`.

**Ejemplo problemático en `DashboardView.java`:**
```java
loadSocios(tablaSocios, club);  // Obtiene socios
// En TableView, si accedemos a reservas (lazy), fallará después de cerrar EM
```

**Solución recomendada:**

Opción A: Usar `JOIN FETCH` en todas las consultas donde se necesiten relaciones:
```java
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
```

Opción B: Inicializar relaciones explícitamente:
```java
public Socio buscarPorId(String id) {
    EntityManager em = JpaUtil.getEntityManager();
    try {
        Socio s = em.find(Socio.class, id);
        if (s != null) {
            // Forzar carga de lazy collections ANTES de cerrar EM
            s.getReservas().size();
        }
        return s;
    } finally {
        em.close();
    }
}
```

---

### 2. **Transacciones en Lectura**

**Actualización:**
```java
public List<Socio> listarTodos() {
    EntityManager em = JpaUtil.getEntityManager();
    try {
        // Opcionalmente: transacción de solo lectura
        em.getTransaction().begin();
        TypedQuery<Socio> q = em.createQuery("SELECT s FROM Socio s", Socio.class);
        List<Socio> result = q.getResultList();
        em.getTransaction().commit();
        return result;
    } catch (RuntimeException ex) {
        if (em.getTransaction().isActive()) 
            em.getTransaction().rollback();
        throw ex;
    } finally {
        em.close();
    }
}
```

**Nota:** En la mayoría de BD, no es necesario, pero es más consistente.

---

### 3. **ClubDeportivo: Manejo de Excepciones**

**Actual:**
```java
public void crearSocio(Socio s) throws Exception {
    socioDao.insertar(s);  // Si falla, lanza exception sin detalles
}
```

**Mejorado:**
```java
public void crearSocio(Socio s) throws Exception {
    if (s == null) 
        throw new IllegalArgumentException("Socio no puede ser nulo");
    if (s.getIdSocio() == null || s.getIdSocio().isEmpty()) 
        throw new IllegalArgumentException("ID de socio requerido");
    
    try {
        socioDao.insertar(s);
    } catch (RuntimeException ex) {
        // Capturar excepción específica de constraint único
        if (ex.getCause() != null && 
            ex.getCause().getMessage().contains("Duplicate entry")) {
            throw new IllegalArgumentException("ID de socio ya existe");
        }
        throw new Exception("Error insertando socio: " + ex.getMessage(), ex);
    }
}
```

✅ **YA IMPLEMENTADO PARCIALMENTE** en ClubDeportivo

---

### 4. **Validación de Restricciones BD**

**Actual:**
```sql
CREATE TABLE IF NOT EXISTS socios (
  id_socio VARCHAR(36) PRIMARY KEY,
  dni VARCHAR(16) NOT NULL UNIQUE,
  nombre VARCHAR(80) NOT NULL,
  ...
) ENGINE=InnoDB;
```

✅ Restricciones presentes en BD

**Mejora (opcional):**
```sql
CREATE TABLE IF NOT EXISTS socios (
  id_socio VARCHAR(36) PRIMARY KEY,
  dni VARCHAR(16) NOT NULL UNIQUE KEY,
  nombre VARCHAR(80) NOT NULL,
  email VARCHAR(120) UNIQUE KEY,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  ...
) ENGINE=InnoDB;
```

---

## 📊 MATRIZ DE CUMPLIMIENTO

| Requisito | Estado | Detalles |
|-----------|--------|----------|
| ✅ Entidades @Entity/@Table | ✅ 100% | Socio, Pista, Reserva correctamente anotadas |
| ✅ Relaciones (@OneToMany, @ManyToOne) | ✅ 100% | Bidireccionales con mappedBy |
| ✅ @JoinColumn | ✅ 100% | Correctamente referenciadas |
| ✅ FetchType.LAZY | ✅ 100% | Implementado en todas las relaciones |
| ✅ CascadeType.PERSIST | ✅ 100% | En relaciones OneToMany |
| ✅ Transacciones (begin/commit/rollback) | ✅ 95% | Implementadas, mejorable en lecturas |
| ✅ JOIN FETCH | ✅ 100% | En ReservaDao |
| ✅ TypedQuery JPQL | ✅ 100% | Todas las consultas tipadas |
| ✅ persistence.xml | ✅ 100% | Dialecto, propiedades correctas |
| ✅ EntityManagerFactory | ✅ 100% | Singleton, shutdown hook |
| ✅ EntityManager.close() | ✅ 100% | En finally block |
| ✅ Cierre en aplicación | ✅ 100% | MainApp.stop() |
| ⚠️ Lazy loading en UI | ⚠️ 60% | Posible LazyInitializationException |

---

## 🔧 RECOMENDACIONES FINALES

1. **Implementar DTO (Data Transfer Objects)** para evitar lazy loading en UI:
   ```java
   public class SocioDTO {
       public String idSocio;
       public String nombre;
       public String apellidos;
   }
   ```

2. **Usar `@Transactional` (Spring) o patrón similar** para simplificar código:
   ```java
   @Transactional
   public void crearSocio(Socio s) {
       socioDao.insertar(s);
   }
   ```

3. **Implementar logging** con SLF4J:
   ```java
   private static final Logger log = LoggerFactory.getLogger(SocioDao.class);
   
   public void insertar(Socio s) {
       log.info("Insertando socio: {}", s.getIdSocio());
       // ...
   }
   ```

4. **Unit tests** para DAOs:
   ```java
   @Test
   public void testInsertar() {
       Socio s = new Socio("1", "12345678A", "Juan", "Pérez", null, null);
       socioDao.insertar(s);
       assertTrue(socioDao.buscarPorId("1") != null);
   }
   ```

---

## ✨ CONCLUSIÓN

**El proyecto cumple con ~95% de los requisitos de JPA e integración.**

**Fortalezas:**
- ✅ Entidades correctamente anotadas
- ✅ Relaciones bidireccionales implementadas
- ✅ Transacciones en operaciones de modificación
- ✅ JOIN FETCH para evitar N+1 queries
- ✅ EntityManagerFactory correctamente gestado
- ✅ Cierre de recursos garantizado

**Áreas de mejora:**
- ⚠️ Potencial LazyInitializationException en UI (usar JOIN FETCH o DTO)
- ⚠️ Transacciones opcionales en lecturas (mejorable para consistencia)
- ⚠️ Falta de logging en DAOs
- ⚠️ Falta de unit tests

**Prioridad:** Implementar JOIN FETCH en `listarTodos()` cuando se necesiten relaciones.


