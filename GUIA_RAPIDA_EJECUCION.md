# 🚀 GUÍA RÁPIDA DE EJECUCIÓN

## Prerequisitos

### ✅ Lo que TIENES:
- ✓ Java Runtime Environment (JRE) - para ejecutar
- ✓ Maven - para compilar y empaquetar
- ✓ MySQL/MariaDB - base de datos
- ✓ Código fuente - completamente revisado

### ❌ Lo que NECESITAS:
- ✗ Java Development Kit (JDK) - **OBLIGATORIO para compilar**

---

## Paso 1: Instalar JDK (CRÍTICO)

### En Windows con Chocolatey:
```powershell
choco install openjdk17
```

### O descargar manualmente:
```
https://adoptopenjdk.net/  → Descargar JDK 17
O usar la versión de Oracle:
https://www.oracle.com/java/technologies/downloads/
```

### Verificar instalación:
```powershell
java -version
javac -version
```

---

## Paso 2: Preparar Base de Datos

### Abre MySQL y ejecuta:

```sql
-- Opción A: Ejecutar script de migración
mysql -u root -p < data/fix_enum_to_varchar.sql

-- O manualmente en MySQL Workbench o CLI:
USE club_dama;
ALTER TABLE pistas MODIFY COLUMN deporte VARCHAR(50) NOT NULL;
ALTER TABLE reservas MODIFY COLUMN precio DECIMAL(8,2) NOT NULL;
```

### O si es primera vez, crear BD:
```bash
mysql -u root -p < data/init_db.sql
```

---

## Paso 3: Compilar el Proyecto

```powershell
cd C:\Users\mmoni\Desktop\cole\dam-ads-u2-MariaOnieva

# Opción A: Usando script Maven de PowerShell
.\mvn-idea.ps1 clean compile

# Opción B: Usando Maven directamente
mvn clean compile
```

### Resultado esperado:
```
[INFO] BUILD SUCCESS
[INFO] Total time:  XX.XXX s
```

---

## Paso 4: Ejecutar la Aplicación

```powershell
# Opción A: Usando script PowerShell
.\mvn-idea.ps1 javafx:run

# Opción B: Usando Maven directamente
mvn javafx:run
```

### Resultado esperado:
```
[INFO] --- javafx:0.0.8:run (default-cli) @ club-dama ---
(Ventana de aplicación se abre)
```

---

## Solución de Problemas

### ❌ Error: "No compiler is provided"
```
❌ Síntoma: "Perhaps you are running on a JRE rather than a JDK?"

✅ Solución: Instalar JDK (ver Paso 1)
```

### ❌ Error: "Connection refused"
```
❌ Síntoma: "Could not open connection to localhost:3306"

✅ Solución:
   1. Verificar que MySQL/MariaDB está ejecutándose
   2. Verificar usuario y contraseña en persistence.xml
   3. Verificar que BD existe: CREATE DATABASE IF NOT EXISTS club_dama;
```

### ❌ Error: "Schema-validation: wrong column type"
```
❌ Síntoma: "found [enum], but expecting [varchar(50)]"

✅ Solución: Ejecutar script de migración (Paso 2)
```

### ❌ Error: "LazyInitializationException"
```
❌ Síntoma: "could not initialize proxy – no Session"

✅ Solución: Está usando método incorrecto
   ✓ Cambiar: listarTodos() → listarTodosConReservas()
   ✓ O acceder a relaciones dentro de transacción
```

---

## Comandos Útiles

### Compilar sin ejecutar:
```powershell
.\mvn-idea.ps1 clean compile
```

### Ejecutar solo tests:
```powershell
.\mvn-idea.ps1 test
```

### Crear JAR ejecutable:
```powershell
.\mvn-idea.ps1 package
```

### Limpiar archivos compilados:
```powershell
.\mvn-idea.ps1 clean
```

### Ver dependencias:
```powershell
.\mvn-idea.ps1 dependency:tree
```

---

## Estructura de Carpetas después de Compilar

```
target/
├── classes/                    ← Archivos compilados
│   └── es/clubdama/...
├── maven-status/              ← Estado de Maven
└── generated-sources/         ← Código generado
```

---

## Acceder a la Aplicación

Una vez ejecutada, la ventana mostrará:

```
┌─────────────────────────────────────────────┐
│           Club DAMA Sports                   │
├─────────────────────────────────────────────┤
│ Archivo  Socios  Pistas  Reservas  Ver     │
├─────────────────────────────────────────────┤
│                                             │
│      [Dashboard con tablas de datos]       │
│                                             │
├─────────────────────────────────────────────┤
│ Listo                                       │
└─────────────────────────────────────────────┘
```

### Funcionalidades:

**Archivo:**
- ✓ Guardar (verificar BD)
- ✓ Salir

**Socios:**
- ✓ Alta socio
- ✓ Baja socio

**Pistas:**
- ✓ Alta pista
- ✓ Cambiar disponibilidad

**Reservas:**
- ✓ Crear reserva
- ✓ Cancelar reserva

**Ver:**
- ✓ Dashboard (resumen)

---

## Flujo de Uso Recomendado

### 1️⃣ Dar de Alta Socios
```
Menú → Socios → Alta socio
Rellenar: ID, DNI, Nombre, Apellidos, Teléfono, Email
Hacer clic "Crear"
```

### 2️⃣ Dar de Alta Pistas
```
Menú → Pistas → Alta pista
Rellenar: ID, Deporte (tenis/padel/futbol_sala), Descripción, Disponible
Hacer clic "Crear"
```

### 3️⃣ Crear Reservas
```
Menú → Reservas → Crear reserva
Seleccionar: Socio, Pista, Fecha, Hora, Duración
El precio se calcula automáticamente
Hacer clic "Reservar"
```

### 4️⃣ Ver Dashboard
```
Menú → Ver → Dashboard
Muestra: Socios, Pistas, Reservas de hoy
Botón "Refrescar" para actualizar
```

---

## Validaciones Implementadas

La aplicación valida automáticamente:

✅ **Socios:**
- ID único (no duplicados)
- DNI válido (no duplicados)
- Email válido (opcional)

✅ **Pistas:**
- ID único (no duplicados)
- Deporte válido (tenis, padel, futbol_sala)
- Solo disponibles para reservar si están operativas

✅ **Reservas:**
- Socio debe existir
- Pista debe existir
- Duración > 0
- No se puede borrar socio con reservas activas

---

## Información de Conexión

### Archivo: `src/main/resources/META-INF/persistence.xml`

```xml
<!-- URL de BD -->
jdbc:mysql://localhost:3306/club_dama?useSSL=false&...

<!-- Usuario -->
root

<!-- Contraseña -->
root

<!-- Driver -->
com.mysql.cj.jdbc.Driver
```

### Si necesitas cambiar:
Edita `persistence.xml` antes de compilar

O usa variables de entorno:
```powershell
$env:DB_URL = "jdbc:mysql://otro-servidor:3306/otra_bd"
$env:DB_USER = "nuevo_usuario"
$env:DB_PASS = "nueva_contraseña"
```

---

## Información de Base de Datos

### Tablas creadas:

```
socios
├── id_socio (VARCHAR 36, PK)
├── dni (VARCHAR 16, UK)
├── nombre (VARCHAR 80, NN)
├── apellidos (VARCHAR 120)
├── telefono (VARCHAR 20)
└── email (VARCHAR 120, UK)

pistas
├── id_pista (VARCHAR 36, PK)
├── deporte (VARCHAR 50, NN)
├── descripcion (VARCHAR 200)
└── disponible (TINYINT 1)

reservas
├── id_reserva (VARCHAR 36, PK)
├── id_socio (VARCHAR 36, FK)
├── id_pista (VARCHAR 36, FK)
├── fecha (DATE, NN)
├── hora_inicio (TIME, NN)
├── duracion_min (INT, NN)
└── precio (DECIMAL 8,2, NN)
```

### Procedimientos y Funciones:

```
fn_precio_reserva(minutos)
└── Calcula precio: CEIL(minutos / 30) * 5.00 €

sp_crear_reserva(...)
└── Crea reserva con precio calculado
```

---

## Archivos Importantes

```
Ejecutables:
├── mvn-idea.ps1          ← Script PowerShell para Maven
├── mvn-idea.cmd          ← Script CMD para Maven
├── mvnw                  ← Maven wrapper (Linux/Mac)
└── mvnw.cmd              ← Maven wrapper (Windows)

Configuración:
├── pom.xml               ← Dependencias Maven
└── persistence.xml       ← Configuración JPA

Base de Datos:
├── init_db.sql           ← Crear BD desde cero
├── fix_enum_to_varchar.sql ← Migración
└── schema_from_user.sql  ← Utilidad

Código:
├── src/main/java/es/clubdama/
│   ├── model/            ← Entidades JPA
│   ├── dao/              ← Capa de persistencia
│   ├── service/          ← Lógica de negocio
│   ├── vista/            ← Interfaz gráfica
│   └── util/             ← Utilidades
└── src/test/             ← Tests (opcional)

Documentación:
├── RESUMEN_EJECUTIVO.md  ← Resumen ejecutivo
├── REVISION_COMPLETA.md  ← Análisis detallado
├── MEJORAS_IMPLEMENTADAS.md ← Cambios realizados
├── CHECKLIST_FINAL.md    ← Validación de requisitos
├── ARQUITECTURA.md       ← Diseño del sistema
└── README.md             ← Este documento
```

---

## Dependencias Principales

```xml
<dependency>
  <groupId>jakarta.persistence</groupId>
  <artifactId>jakarta.persistence-api</artifactId>
  <version>3.0.0</version>
</dependency>

<dependency>
  <groupId>org.hibernate</groupId>
  <artifactId>hibernate-core</artifactId>
  <version>6.2.7.Final</version>
</dependency>

<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <version>8.0.33</version>
</dependency>

<dependency>
  <groupId>org.openjfx</groupId>
  <artifactId>javafx-controls</artifactId>
  <version>17.0.2</version>
</dependency>
```

---

## Checklist Antes de Ejecutar

- [ ] JDK 17 instalado y en PATH
- [ ] MySQL/MariaDB corriendo
- [ ] BD `club_dama` creada
- [ ] Script de migración ejecutado (si es actualización)
- [ ] usuario/contraseña correctos en persistence.xml
- [ ] Maven instalado o usando mvnw
- [ ] Archivos fuente sin cambios accidentales

---

## Soporte

Si encuentras problemas:

1. **Revisa RESUMEN_EJECUTIVO.md** - Soluciones comunes
2. **Revisa ARQUITECTURA.md** - Entiende el diseño
3. **Revisa CHECKLIST_FINAL.md** - Verifica requisitos
4. **Logs de Maven** - Busca mensajes de error

---

## Tiempo Estimado

```
Instalar JDK:              10-15 min
Preparar BD:               5 min
Compilar primer vez:       30-60 seg (descarga dependencias)
Compilar posterior:        5-10 seg
Ejecutar:                  instantáneo
```

---

**¡Listo para comenzar!** 🚀

Si tienes dudas, consulta la documentación generada:
- `RESUMEN_EJECUTIVO.md` para overview
- `ARQUITECTURA.md` para entender el diseño
- `REVISION_COMPLETA.md` para análisis detallado


