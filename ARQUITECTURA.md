# 🏗️ ARQUITECTURA Y DISEÑO - CLUB DAMA SPORTS

## Estructura del Proyecto

```
dam-ads-u2-MariaOnieva/
├── src/
│   ├── main/
│   │   ├── java/es/clubdama/
│   │   │   ├── model/              ← Entidades JPA
│   │   │   │   ├── Socio.java
│   │   │   │   ├── Pista.java
│   │   │   │   └── Reserva.java
│   │   │   │
│   │   │   ├── dao/                ← Capa de Persistencia
│   │   │   │   ├── JpaUtil.java    (Gestión de EntityManagerFactory)
│   │   │   │   ├── JdbcUtil.java   (Conexiones JDBC)
│   │   │   │   ├── SocioDao.java
│   │   │   │   ├── PistaDao.java
│   │   │   │   └── ReservaDao.java
│   │   │   │
│   │   │   ├── service/            ← Capa de Negocio
│   │   │   │   └── ClubDeportivo.java
│   │   │   │
│   │   │   ├── vista/              ← Capa de Presentación (JavaFX)
│   │   │   │   ├── MainApp.java
│   │   │   │   └── views/
│   │   │   │       ├── DashboardView.java
│   │   │   │       ├── SocioFormView.java
│   │   │   │       ├── PistaFormView.java
│   │   │   │       ├── ReservaFormView.java
│   │   │   │       ├── BajaSocioView.java
│   │   │   │       ├── CancelarReservaView.java
│   │   │   │       └── CambiarDisponibilidadView.java
│   │   │   │
│   │   │   └── util/               ← Utilidades
│   │   │       └── ValidationUtil.java
│   │   │
│   │   └── resources/
│   │       └── META-INF/
│   │           └── persistence.xml (Configuración JPA)
│   │
│   └── test/                       ← Tests (Opcional)
│       └── java/es/clubdama/
│
├── data/                           ← Scripts SQL
│   ├── init_db.sql                (Inicialización BD)
│   └── fix_enum_to_varchar.sql    (Migración)
│
├── pom.xml                         ← Configuración Maven
└── documentación/
    ├── REVISION_COMPLETA.md       (Análisis exhaustivo)
    ├── MEJORAS_IMPLEMENTADAS.md   (Cambios realizados)
    ├── RESUMEN_EJECUTIVO.md       (Resumen ejecutivo)
    └── CHECKLIST_FINAL.md         (Validación completa)
```

---

## Diagrama de Relaciones

```
                    ┌─────────────────────────────────────┐
                    │          SOCIO                       │
                    ├─────────────────────────────────────┤
                    │ - id_socio: String (PK)             │
                    │ - dni: String (UK)                  │
                    │ - nombre: String                    │
                    │ - apellidos: String                 │
                    │ - telefono: String                  │
                    │ - email: String                     │
                    │ - reservas: List<Reserva> (FK)     │
                    └──────────────┬──────────────────────┘
                                   │
                                   │ 1:N
                                   │ @OneToMany(mappedBy="socio")
                                   │
        ┌──────────────────────────┴──────────────────────────┐
        │                                                     │
        │          ┌──────────────────────────────────────┐   │
        │          │        RESERVA                       │   │
        │          ├──────────────────────────────────────┤   │
        │          │ - id_reserva: String (PK)           │   │
        │          │ - id_socio: String (FK→Socio)       │   │
        │          │ - id_pista: String (FK→Pista)       │   │
        │          │ - fecha: LocalDate                   │   │
        │          │ - hora_inicio: LocalTime             │   │
        │          │ - duracion_min: int                  │   │
        │          │ - precio: BigDecimal                 │   │
        │          │ - socio: Socio (@ManyToOne)         │   │
        │          │ - pista: Pista (@ManyToOne)         │   │
        │          └──────────────┬───────────────────────┘   │
        │                         │                           │
        │                         │ N:1                       │
        │      ┌──────────────────┴──────────────────┐         │
        │      │                                    │         │
        │      │                                    │         │
        └──────┼────────────────────────────────────┼─────────┘
               │                                    │
               │ @ManyToOne(fetch=LAZY)            │ @ManyToOne(fetch=LAZY)
               │ @JoinColumn(name="id_socio")      │ @JoinColumn(name="id_pista")
               │                                    │
               │                                    │
        ┌──────┴─────────────────────────────────────┴──────────┐
        │          PISTA                                        │
        ├───────────────────────────────────────────────────────┤
        │ - id_pista: String (PK)                              │
        │ - deporte: String (VARCHAR(50))                      │
        │ - descripcion: String                                │
        │ - disponible: boolean                                │
        │ - reservas: List<Reserva> (FK)                       │
        └───────────┬───────────────────────────────────────────┘
                    │
                    │ 1:N
                    │ @OneToMany(mappedBy="pista")
                    │
                    (N:1 en RESERVA)
```

---

## Capas de la Aplicación

### 1. **Capa de Presentación (Vista) - JavaFX**

```
MainApp (Punto de entrada)
    ↓
    Crea instancia de ClubDeportivo
    ↓
    Muestra BorderPane con:
        ├── MenuBar (navegación)
        ├── Centro (vistas intercambiables)
        │   ├── DashboardView (lectura de datos)
        │   ├── SocioFormView (crear socio)
        │   ├── PistaFormView (crear pista)
        │   ├── ReservaFormView (crear reserva)
        │   ├── BajaSocioView (eliminar socio)
        │   ├── CancelarReservaView (eliminar reserva)
        │   └── CambiarDisponibilidadView (actualizar pista)
        └── StatusBar (información)
```

**Responsabilidades:**
- Mostrar UI
- Capturar eventos del usuario
- Delegar operaciones al servicio

---

### 2. **Capa de Negocio (Servicio)**

```
ClubDeportivo
    ├── listarSocios() → SocioDao.listarTodos()
    ├── listarPistas() → PistaDao.listarTodos()
    ├── listarReservasHoy() → ReservaDao.listarPorPistaYFecha()
    ├── listarReservasTodas() → ReservaDao.listarTodas()
    │
    ├── crearSocio(Socio) → Validar + SocioDao.insertar()
    ├── crearPista(Pista) → Validar + PistaDao.insertar()
    ├── crearReserva(...) → Validar + ReservaDao.crearReserva()
    │
    ├── bajaSocio(idSocio) → Validar + SocioDao.borrarPorId()
    ├── cancelarReserva(idReserva) → ReservaDao.cancelarReserva()
    ├── cambiarDisponibilidadPista(...) → PistaDao.actualizarDisponibilidad()
    │
    └── calcularPrecioReserva(minutos) → ReservaDao.calcularPrecio()
```

**Responsabilidades:**
- Lógica de negocio
- Validaciones
- Coordinación entre DAOs

---

### 3. **Capa de Persistencia (DAO) - JPA**

```
┌────────────────────────────────────────────┐
│         EntityManagerFactory               │
│         (Singleton en JpaUtil)             │
└──────────────────┬─────────────────────────┘
                   │
        ┌──────────┴──────────┐
        ↓                     ↓
   SocioDao              PistaDao          ReservaDao
   ├─ insertar()         ├─ insertar()     ├─ crearReserva()
   ├─ buscarPorId()      ├─ buscarPorId()  ├─ listarPorPistaYFecha()
   ├─ listarTodos()      ├─ listarTodos()  ├─ listarPorSocio()
   ├─ listarTodos...()   ├─ listarTodos()  ├─ listarTodas()
   └─ borrarPorId()      └─ actualizar...()└─ cancelarReserva()

        (JPA)                (JPA)          (JPA + JDBC)
        JPQL                 JPQL           Procedimiento almacenado
        TypedQuery           TypedQuery     Función BD
```

**Responsabilidades:**
- Operaciones CRUD contra BD
- Transacciones (JPA)
- Manejo de EntityManager
- Consultas JPQL

---

### 4. **Capa de Datos (Base de Datos)**

```
MySQL/MariaDB
    │
    ├── Tabla: socios
    │   ├── id_socio (VARCHAR 36, PK)
    │   ├── dni (VARCHAR 16, UK)
    │   ├── nombre (VARCHAR 80, NN)
    │   ├── apellidos (VARCHAR 120)
    │   ├── telefono (VARCHAR 20)
    │   └── email (VARCHAR 120, UK)
    │
    ├── Tabla: pistas
    │   ├── id_pista (VARCHAR 36, PK)
    │   ├── deporte (VARCHAR 50, NN)
    │   ├── descripcion (VARCHAR 200)
    │   └── disponible (TINYINT 1)
    │
    ├── Tabla: reservas
    │   ├── id_reserva (VARCHAR 36, PK)
    │   ├── id_socio (VARCHAR 36, FK)
    │   ├── id_pista (VARCHAR 36, FK)
    │   ├── fecha (DATE, NN)
    │   ├── hora_inicio (TIME, NN)
    │   ├── duracion_min (INT, NN)
    │   └── precio (DECIMAL 8,2, NN)
    │
    ├── Función: fn_precio_reserva(minutos)
    │   └── Retorna DECIMAL(8,2)
    │
    └── Procedimiento: sp_crear_reserva(...)
        └── Inserta y calcula precio
```

---

## Flujo de Datos

### Ejemplo: Crear Socio

```
1. Usuario hace clic en "Alta Socio"
   │
   └─→ MainApp muestra SocioFormView
   
2. Usuario rellena formulario y hace clic "Crear"
   │
   └─→ SocioFormView.onCreate()
   
3. SocioFormView crea objeto Socio
   │
   └─→ club.crearSocio(socio)
   
4. ClubDeportivo.crearSocio()
   ├─ Valida: dni, idSocio, email
   ├─ Comprueba: ¿idSocio existe?
   │
   └─→ socioDao.insertar(socio)
   
5. SocioDao.insertar()
   ├─ em = JpaUtil.getEntityManager()
   ├─ em.getTransaction().begin()
   ├─ em.persist(socio)
   ├─ em.getTransaction().commit()
   │
   └─→ EntityManager cierra en finally
   
6. BD: INSERT INTO socios (...)
   
7. SocioFormView muestra "Socio insertado correctamente"
   
8. Campos se limpian
```

---

### Ejemplo: Listar Socios con Reservas

```
1. DashboardView carga
   │
   └─→ club.listarSocios()
   
2. ClubDeportivo.listarSocios()
   │
   └─→ socioDao.listarTodos()
   
3. SocioDao.listarTodos()
   ├─ em = JpaUtil.getEntityManager()
   ├─ TypedQuery<Socio> q = em.createQuery("SELECT s FROM Socio s", Socio.class)
   ├─ List<Socio> result = q.getResultList()
   │
   └─→ EntityManager cierra en finally
   
4. DashboardView muestra socios en tabla
   
5. Si usuario accede a reservas (lazy loading)
   ❌ LazyInitializationException (EM cerrado)
   
6. Solución: Usar listarTodosConReservas()
   │
   └─→ SocioDao.listarTodosConReservas()
   
7. SocioDao.listarTodosConReservas()
   ├─ TypedQuery: "SELECT DISTINCT s FROM Socio s LEFT JOIN FETCH s.reservas"
   └─ ✅ Reservas ya cargadas ANTES de cerrar EM
```

---

## Patrón de Transacciones

### Operación de Lectura (Read-Only)

```java
public List<Socio> listarTodos() {
    EntityManager em = JpaUtil.getEntityManager();
    try {
        // ❌ SIN: em.getTransaction().begin()
        TypedQuery<Socio> q = em.createQuery(...);
        return q.getResultList();
        // ❌ SIN: em.getTransaction().commit()
    } finally {
        em.close();  // ✅ Siempre
    }
}
```

**Ventajas:**
- Más rápido (sin overhead de transacción)
- Consistencia de lectura garantizada por BD
- Ideal para SELECT

---

### Operación de Escritura (Write)

```java
public void insertar(Socio s) {
    if (s == null) throw new IllegalArgumentException("...");
    EntityManager em = JpaUtil.getEntityManager();
    try {
        em.getTransaction().begin();      // ✅ BEGIN
        em.persist(s);
        em.getTransaction().commit();     // ✅ COMMIT
    } catch (RuntimeException ex) {
        if (em.getTransaction().isActive()) 
            em.getTransaction().rollback(); // ✅ ROLLBACK
        throw ex;
    } finally {
        em.close();                       // ✅ Siempre
    }
}
```

**Transacción ACID:**
- **A**tomicity: BEGIN/COMMIT
- **C**onsistency: Validaciones + constraits BD
- **I**solation: Nivel de aislamiento BD
- **D**urability: BD persiste después del COMMIT

---

## Características Clave Implementadas

### 1. ✅ Relaciones Bidireccionales
```java
// En Socio.java
@OneToMany(mappedBy = "socio", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
private List<Reserva> reservas;

// En Reserva.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_socio")
private Socio socio;
```

**Beneficios:**
- Navegación en ambos sentidos
- No crea columna redundante
- Consistencia automática

---

### 2. ✅ FetchType.LAZY
```java
@OneToMany(..., fetch = FetchType.LAZY)
private List<Reserva> reservas;  // No se carga automáticamente
```

**Ventajas:**
- Mejor rendimiento (no carga datos innecesarios)
- Menos consumo de memoria

**Desventaja:**
- LazyInitializationException si accedes fuera de transacción

**Solución:**
- Usar JOIN FETCH en JPQL

---

### 3. ✅ JOIN FETCH
```java
TypedQuery<Socio> q = em.createQuery(
    "SELECT DISTINCT s FROM Socio s LEFT JOIN FETCH s.reservas",
    Socio.class);
```

**Ventajas:**
- Carga relaciones dentro de transacción
- Evita N+1 queries
- Seguro para usar fuera de transacción

---

### 4. ✅ CascadeType.PERSIST
```java
@OneToMany(mappedBy = "socio", cascade = CascadeType.PERSIST)
private List<Reserva> reservas;
```

**Efecto:**
- Si guardas Socio, también guardan sus Reservas automáticamente

---

### 5. ✅ Validación de Parámetros
```java
public void insertar(Socio s) {
    if (s == null) throw new IllegalArgumentException("Socio no puede ser nulo");
    if (s.getIdSocio() == null || s.getIdSocio().isEmpty()) 
        throw new IllegalArgumentException("idSocio requerido");
    // ... operación
}
```

**Beneficios:**
- Previene errores silenciosos
- Mensajes claros para debugging
- Validación temprana

---

### 6. ✅ Cierre Garantizado de Recursos
```java
EntityManager em = JpaUtil.getEntityManager();
try {
    // ... operaciones
} finally {
    em.close();  // ✅ Siempre se ejecuta
}
```

**Garantiza:**
- Sin memory leaks
- Recursos liberados incluso si hay excepciones
- Conexiones devueltas al pool

---

## Configuración de Persistence.xml

### Dialecto
```xml
<property name="hibernate.dialect" value="org.hibernate.dialect.MariaDBDialect"/>
```
- Adapta SQL a sintaxis MariaDB/MySQL
- Genera SQL optimizado

### Validación de Esquema
```xml
<property name="hibernate.hbm2ddl.auto" value="validate"/>
```
- **validate**: Valida sin modificar
- Ideal para producción
- Previene cambios accidentales

### Conexión JDBC
```xml
<property name="jakarta.persistence.jdbc.url" 
    value="jdbc:mysql://localhost:3306/club_dama?useSSL=false&..."/>
```
- `useSSL=false`: Conexión no encriptada (local)
- `serverTimezone=UTC`: Zona horaria
- `characterEncoding=UTF-8`: Soporte Unicode

---

## Procedimientos Almacenados (BD)

### fn_precio_reserva()
```sql
CREATE FUNCTION fn_precio_reserva(minutos INT) RETURNS DECIMAL(8,2)
BEGIN
  DECLARE precio DECIMAL(8,2);
  SET precio = (CEIL(minutos / 30) * 5.00);
  RETURN precio;
END
```

**Lógica:**
- Por cada 30 minutos: 5€
- Ej: 30 min = 5€, 60 min = 10€, 45 min = 10€

---

### sp_crear_reserva()
```sql
CREATE PROCEDURE sp_crear_reserva(
  IN p_id_reserva VARCHAR(36),
  IN p_id_socio VARCHAR(36),
  IN p_id_pista VARCHAR(36),
  IN p_fecha DATE,
  IN p_hora_ini TIME,
  IN p_duracion INT
)
BEGIN
  DECLARE p_precio DECIMAL(8,2);
  IF p_duracion <= 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duración inválida';
  END IF;
  SET p_precio = fn_precio_reserva(p_duracion);
  INSERT INTO reservas(...)
  VALUES (p_id_reserva, p_id_socio, p_id_pista, p_fecha, p_hora_ini, p_duracion, p_precio);
END
```

**Ventajas:**
- Lógica compleja en BD (más segura)
- Transacción ACID garantizada
- Cálculo atómico

---

## ✅ Conclusión

El proyecto implementa una **arquitectura multicapa bien estructurada** con:

- ✅ **Presentación:** JavaFX con vistas intercambiables
- ✅ **Negocio:** ClubDeportivo centraliza lógica
- ✅ **Persistencia:** DAOs con JPA/Hibernate + JDBC para procedimientos
- ✅ **Datos:** MySQL/MariaDB con procedimientos almacenados

**Resultado:** Aplicación robusta, mantenible y escalable


