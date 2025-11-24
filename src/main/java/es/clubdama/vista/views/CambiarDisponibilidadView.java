package es.clubdama.vista.views;

import es.clubdama.service.ClubDeportivo;
import es.clubdama.model.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * Vista para cambiar la disponibilidad operativa de una pista.
 * Actualiza la BD a través del servicio y mantiene la lista sincronizada.
 */
public class CambiarDisponibilidadView extends GridPane {
    public CambiarDisponibilidadView(ClubDeportivo club) {
        setPadding(new Insets(12));
        setHgap(8); setVgap(8);

        ComboBox<Pista> id = new ComboBox<>();
        CheckBox disponible = new CheckBox("Disponible");
        Button cambiar = new Button("Aplicar");

        addRow(0, new Label("idPista"), id);
        addRow(1, new Label("Estado"), disponible);
        add(cambiar, 1, 2);

        try {
            id.getItems().addAll(club.listarPistas());
        } catch (Exception ex) {
            showError("No se pudieron cargar las pistas: " + ex.getMessage());
        }

        // sincronizar checkbox con la pista seleccionada
        id.setOnAction(e -> {
            Pista sel = id.getValue();
            if (sel != null) {
                disponible.setSelected(sel.isDisponible());
            }
        });

        cambiar.setOnAction(e -> {
            Pista sel = id.getValue();
            if (sel == null) { showError("Selecciona una pista"); return; }
            try {
                club.cambiarDisponibilidadPista(sel.getIdPista(), disponible.isSelected());
                // actualizar el objeto en la lista para reflejar el cambio en la UI
                int idx = id.getItems().indexOf(sel);
                if (idx >= 0) {
                    sel.setDisponible(disponible.isSelected());
                    id.getItems().set(idx, sel);
                }
                showInfo("Disponibilidad actualizada");
            } catch (Exception ex) {
                showError("Error actualizando disponibilidad: " + ex.getMessage());
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
