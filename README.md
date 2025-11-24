# club-dama

Proyecto Java (Maven) que gestiona reservas de pistas. Actualizado para usar MySQL (compatible con MySQL Workbench).

Configuración rápida para ejecutar localmente:

1. Instala MySQL y MySQL Workbench. Crea una base de datos llamada `club_dama` o usa otra y ajusta la URL.
2. Asegúrate de tener Maven y JDK 17 instalados y en tu PATH.
3. Variables de entorno (opcional, el código usa valores por defecto):
   - DB_URL (ej: `jdbc:mysql://localhost:3306/club_dama?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`)
   - DB_USER (ej: `root`)
   - DB_PASS (ej: `root`)

4. Ejecuta en PowerShell:

```powershell
mvn -DskipTests package
mvn -Dtest=es.clubdama.ReservaDaoTest test
```

5. Si usas MySQL Workbench, crea las tablas según `data/schema_from_user.sql`.

Notas:
- `src/main/java/es/clubdama/dao/JdbcUtil.java` ahora usa `com.mysql.cj.jdbc.Driver` y lee la conexión desde variables de entorno con valores por defecto.
- `pom.xml` ahora depende de `mysql:mysql-connector-java:8.0.33`.



