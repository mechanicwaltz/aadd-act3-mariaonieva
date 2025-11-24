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


Ejemplos funcionando: 
<img width="1462" height="932" alt="image" src="https://github.com/user-attachments/assets/864e34c2-cbb3-4721-87dc-6137bc9135b3" />
<img width="1196" height="833" alt="image" src="https://github.com/user-attachments/assets/c7928f56-ec46-4d8e-925b-40c215a2b955" />
<img width="1198" height="830" alt="image" src="https://github.com/user-attachments/assets/da5e81a7-d605-4be7-91ad-307da066d931" />
<img width="1193" height="826" alt="image" src="https://github.com/user-attachments/assets/3cb81544-574a-43b9-93fb-ec335f535cbf" />
<img width="1195" height="837" alt="image" src="https://github.com/user-attachments/assets/4c47954d-ccff-4929-8bb2-1e7dd7c40bdf" />










