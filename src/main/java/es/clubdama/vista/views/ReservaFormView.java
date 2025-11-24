package es.clubdama.vista.views;

import es.clubdama.model.*;
import es.clubdama.service.ClubDeportivo;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.LocalTime;


/**
 * Formulario para crear reservas. Valida formato de hora localmente y delega
 * la creación y el cálculo del precio a la capa de servicio/DAO (procedimiento + función en BD).
 */
public class ReservaFormView extends GridPane {
    public ReservaFormView(ClubDeportivo club) {
        setPadding(new Insets(12));
        setHgap(8); setVgap(8);

        ComboBox<Socio> idSocio = new ComboBox<>();
        ComboBox<Pista> idPista = new ComboBox<>();
        DatePicker fecha = new DatePicker(LocalDate.now());
        TextField hora = new TextField("10:00");
        Spinner<Integer> duracion = new Spinner<>(30, 300, 60, 30);
        TextField precio = new TextField();
        Button crear = new Button("Reservar");

        addRow(0, new Label("Socio*"), idSocio);
        addRow(1, new Label("Pista*"), idPista);
        addRow(2, new Label("Fecha*"), fecha);
        addRow(3, new Label("Hora inicio* (HH:mm)"), hora);
        addRow(4, new Label("Duración (min)"), duracion);
        addRow(5, new Label("Precio (€)"), precio);
        add(crear, 1, 6);

        try {
            idSocio.getItems().addAll(club.listarSocios());
            idPista.getItems().addAll(club.listarPistas());
        } catch (Exception ex) {
            showError("No se pudieron cargar socios/pistas: " + ex.getMessage());
        }

        // inicializar y actualizar precio al cambiar duración
        try {
            double p0 = club.calcularPrecioReserva(duracion.getValue());
            precio.setText(String.valueOf(p0));
        } catch (Exception ex) {
            precio.setText("0.0");
        }
        duracion.valueProperty().addListener((obs, oldV, newV) -> {
            try {
                double p = club.calcularPrecioReserva(newV);
                precio.setText(String.valueOf(p));
            } catch (Exception ex) {
                // ignorar, mantener precio manual
            }
        });

        crear.setOnAction(e -> {
            try {
                if (idSocio.getValue()==null) throw new IllegalArgumentException("Seleccione socio");
                if (idPista.getValue()==null) throw new IllegalArgumentException("Seleccione pista");
                String horaText = hora.getText();
                if (!club.validarHoraString(horaText)) throw new IllegalArgumentException("Hora inválida (HH:mm)");
                java.time.LocalTime t = java.time.LocalTime.parse(horaText);
                String nuevaId = club.crearReserva(idSocio.getValue().getIdSocio(), idPista.getValue().getIdPista(), fecha.getValue(), t, duracion.getValue());
                showInfo("Reserva creada con id: " + nuevaId);
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }
    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
