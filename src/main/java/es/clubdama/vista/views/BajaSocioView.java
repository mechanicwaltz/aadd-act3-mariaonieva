package es.clubdama.vista.views;

import es.clubdama.service.ClubDeportivo;
import es.clubdama.model.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.List;

/**
 * Vista para dar de baja a un socio. Pide confirmación y delega la lógica en ClubDeportivo.
 */
public class BajaSocioView extends GridPane {
    public BajaSocioView(ClubDeportivo club) {
        setPadding(new Insets(12));
        setHgap(8); setVgap(8);

        if (club == null) throw new IllegalArgumentException("ClubDeportivo no puede ser nulo");

        ComboBox<Socio> cbSocios = new ComboBox<>();
        Button baja = new Button("Dar de baja");

        // Mostrar socio de forma legible en la lista
        cbSocios.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Socio s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) setText(null);
                else setText(s.getNombre() + " " + s.getApellidos() + " (" + s.getIdSocio() + ")");
            }
        });
        cbSocios.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Socio s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) setText(null);
                else setText(s.getNombre() + " " + s.getApellidos() + " (" + s.getIdSocio() + ")");
            }
        });

        // Desactivar botón si no hay selección
        baja.disableProperty().bind(cbSocios.valueProperty().isNull());

        addRow(0, new Label("Socio"), cbSocios);
        add(baja, 1, 1);

        // Método local para (re)cargar socios desde la capa servicio
        Runnable loadSocios = () -> {
            cbSocios.getItems().clear();
            try {
                List<Socio> socios = club.listarSocios();
                cbSocios.getItems().addAll(socios);
            } catch (Exception e) {
                showError("No se pudieron cargar los socios: " + e.getMessage());
            }
        };

        // Cargar inicialmente
        loadSocios.run();

        baja.setOnAction(e -> {
            Socio sel = cbSocios.getValue();
            if (sel == null) {
                showError("Selecciona un socio");
                return;
            }
            Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "¿Confirmar baja de " + sel.getNombre() + "?", ButtonType.YES, ButtonType.NO);
            conf.setHeaderText(null);
            conf.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    try {
                        club.bajaSocio(sel.getIdSocio());
                        // recargar lista desde la DB para mantener coherencia
                        loadSocios.run();
                        showInfo("Socio dado de baja");
                    } catch (Exception ex) {
                        // mostrar el mensaje tal cual viene del servicio (p. ej. reservas activas)
                        showError("Error dando de baja: " + ex.getMessage());
                    }
                }
            });
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
