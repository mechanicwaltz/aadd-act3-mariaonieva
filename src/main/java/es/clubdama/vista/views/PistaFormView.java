package es.clubdama.vista.views;

import es.clubdama.model.*;
import es.clubdama.service.ClubDeportivo;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * Formulario para dar de alta una pista.
 * La validación de negocio (unicidad, valores válidos) se delega en ClubDeportivo.
 */
public class PistaFormView extends GridPane {
    public PistaFormView(ClubDeportivo club) {
        setPadding(new Insets(12));
        setHgap(8); setVgap(8);

        TextField id = new TextField();
        ComboBox<String> deporte = new ComboBox<>();
        deporte.getItems().addAll("tenis", "padel", "futbol_sala");
        TextField descripcion = new TextField();
        CheckBox disponible = new CheckBox("Disponible");
        Button crear = new Button("Crear");

        addRow(0, new Label("idPista*"), id);
        addRow(1, new Label("Deporte"), deporte);
        addRow(2, new Label("Descripción"), descripcion);
        addRow(3, new Label("Operativa"), disponible);
        add(crear, 1, 4);

        crear.setOnAction(e -> {
            try {
                es.clubdama.model.Pista p = new es.clubdama.model.Pista();
                p.setIdPista(id.getText());
                p.setDeporte(deporte.getValue());
                p.setDescripcion(descripcion.getText());
                p.setDisponible(disponible.isSelected());
                club.crearPista(p);
                showInfo("Pista insertada correctamente");
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
