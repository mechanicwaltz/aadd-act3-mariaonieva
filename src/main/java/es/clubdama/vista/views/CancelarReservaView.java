package es.clubdama.vista.views;
import es.clubdama.model.*;
import es.clubdama.service.ClubDeportivo;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * Vista para cancelar reservas (muestra las reservas de hoy y permite borrarlas).
 */
public class CancelarReservaView extends GridPane {
    public CancelarReservaView(ClubDeportivo club) {
        setPadding(new Insets(12));
        setHgap(8); setVgap(8);

        ComboBox<Reserva> id = new ComboBox<>();
        Button cancelar = new Button("Cancelar reserva");

        addRow(0, new Label("Reserva"), id);
        add(cancelar, 1, 1);

        try {
            id.getItems().addAll(club.listarReservasHoy());
        } catch (Exception ex) {
            showError("No se pudieron cargar las reservas: " + ex.getMessage());
        }

        cancelar.setOnAction(e -> {
            Reserva sel = id.getValue();
            if (sel == null) { showError("Selecciona una reserva"); return; }
            try {
                club.cancelarReserva(sel.getIdReserva());
                id.getItems().remove(sel);
                showInfo("Reserva cancelada");
            } catch (Exception ex) {
                showError("Error cancelando reserva: " + ex.getMessage());
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
