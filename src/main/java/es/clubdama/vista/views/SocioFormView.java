package es.clubdama.vista.views;

import es.clubdama.model.*;
import es.clubdama.service.ClubDeportivo;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * Formulario para dar de alta un socio.
 * La validación de reglas (unicidad, email) se delega en ClubDeportivo.
 */
public class SocioFormView extends GridPane {
    public SocioFormView(ClubDeportivo club) {
        setPadding(new Insets(12));
        setHgap(8);
        setVgap(8);

        TextField id = new TextField();
        TextField dni = new TextField();
        TextField nombre = new TextField();
        TextField apellidos = new TextField();
        TextField tel = new TextField();
        TextField email = new TextField();
        Button crear = new Button("Crear");

        addRow(0, new Label("idSocio*"), id);
        addRow(1, new Label("DNI"), dni);
        addRow(2, new Label("Nombre"), nombre);
        addRow(3, new Label("Apellidos"), apellidos);
        addRow(4, new Label("Teléfono"), tel);
        addRow(5, new Label("Email"), email);
        add(crear, 1, 6);

        crear.setOnAction(e -> {
            try {
                es.clubdama.model.Socio s = new es.clubdama.model.Socio();
                s.setIdSocio(id.getText());
                s.setDni(dni.getText());
                s.setNombre(nombre.getText());
                s.setApellidos(apellidos.getText());
                s.setTelefono(tel.getText());
                s.setEmail(email.getText());
                // Validaciones en servicio (unicidad, email)
                club.crearSocio(s);
                showInfo("Socio insertado correctamente");
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
