package es.clubdama.vista;

import es.clubdama.model.*;
import es.clubdama.service.ClubDeportivo;
import es.clubdama.vista.views.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Aplicación JavaFX principal que muestra las vistas del sistema.
 * La persistencia se realiza directamente en la base de datos mediante los DAOs y el servicio.
 */
public class MainApp extends Application {

    private ClubDeportivo club;
    private BorderPane root;
    private Label status;

    @Override
    public void start(Stage stage)  {
        try {
            club = new ClubDeportivo();
            showInfo("Conectado");
        } catch (Exception e) {
           showError("Error de conexion"+e.getMessage());
           // Si hay error de conexión, inicializamos club vacío para que la UI no falle completamente
           club = new ClubDeportivo();
        }


        root = new BorderPane();
        root.setTop(buildMenuBar());
        status = new Label("Listo");
        status.setPadding(new Insets(4));
        root.setBottom(status);

        // Vista por defecto
        root.setCenter(new DashboardView(club));

        Scene scene = new Scene(root, 960, 640);
        stage.setTitle("Club DAMA Sports");
        stage.setScene(scene);
        stage.show();
    }

    private MenuBar buildMenuBar() {
        MenuBar mb = new MenuBar();

        Menu socios = new Menu("Socios");
        MenuItem altaSocio = new MenuItem("Alta socio");
        altaSocio.setOnAction(e -> root.setCenter(new SocioFormView(club)));
        MenuItem bajaSocio = new MenuItem("Baja socio");
        bajaSocio.setOnAction(e -> root.setCenter(new BajaSocioView(club)));
        socios.getItems().addAll(altaSocio, bajaSocio);

        Menu pistas = new Menu("Pistas");
        MenuItem altaPista = new MenuItem("Alta pista");
        altaPista.setOnAction(e -> root.setCenter(new PistaFormView(club)));
        MenuItem cambiarDisp = new MenuItem("Cambiar disponibilidad");
        cambiarDisp.setOnAction(e -> root.setCenter(new CambiarDisponibilidadView(club)));
        pistas.getItems().addAll(altaPista, cambiarDisp);

        Menu reservas = new Menu("Reservas");
        MenuItem crearReserva = new MenuItem("Crear reserva");
        crearReserva.setOnAction(e -> root.setCenter(new ReservaFormView(club)));
        MenuItem cancelarReserva = new MenuItem("Cancelar reserva");
        cancelarReserva.setOnAction(e -> root.setCenter(new CancelarReservaView(club)));
        reservas.getItems().addAll(crearReserva, cancelarReserva);

        Menu ver = new Menu("Ver");
        MenuItem dashboard = new MenuItem("Dashboard");
        dashboard.setOnAction(e -> root.setCenter(new DashboardView(club)));
        ver.getItems().addAll(dashboard);

        Menu archivo = new Menu("Archivo");
        MenuItem guardar = new MenuItem("Guardar (verificar BD)");
        guardar.setOnAction(e -> {
            try {
                verificarPersistenciaEnBD();
            } catch (Exception ex) {
                showError("Error comprobando BD: " + ex.getMessage());
            }
        });
        MenuItem salir = new MenuItem("Salir");
        salir.setOnAction(e -> {
            // Salimos directamente; todas las vistas persisten en BD
            Platform.exit();
        });
        archivo.getItems().addAll(guardar, new SeparatorMenuItem(), salir);

        mb.getMenus().addAll(archivo, socios, pistas, reservas, ver);
        return mb;
    }



    public void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    public void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }

    @Override
    public void stop() throws Exception {
        // No auto-guardamos a fichero; todo se persiste directamente en BD por las vistas
        super.stop();
    }

    /**
     * Verifica una vez que la BD responde: cuenta filas de tablas clave y muestra resultados.
     */
    private void verificarPersistenciaEnBD() throws Exception {
        if (club == null) throw new IllegalStateException("Servicio no disponible");
        // Simple check: contar filas en tablas y mostrar resultado
        int socios = club.listarSocios().size();
        int pistas = club.listarPistas().size();
        int reservasHoy = club.listarReservasHoy().size();
        showInfo("BD OK: socios="+socios+", pistas="+pistas+", reservasHoy="+reservasHoy);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
