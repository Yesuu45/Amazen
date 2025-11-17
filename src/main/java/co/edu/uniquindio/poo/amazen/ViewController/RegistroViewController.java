package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Controller.RegistroController;
import co.edu.uniquindio.poo.amazen.Model.Persona.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegistroViewController {

    @FXML private TextField textFieldNombre;
    @FXML private TextField textFieldApellido;
    @FXML private TextField textFieldEmail;
    @FXML private TextField textFieldTelefono;
    @FXML private TextField textFieldCelular;
    @FXML private TextField textFieldDireccion;
    @FXML private TextField textFieldDocumento;
    @FXML private PasswordField textFieldContrasena;
    @FXML private Button buttonRegistrar;

    private final RegistroController registroController = new RegistroController();

    @FXML
    void buttonRegistrar(ActionEvent event) {

        // Validar campos obligatorios
        if (textFieldNombre.getText().isEmpty() ||
                textFieldApellido.getText().isEmpty() ||
                textFieldEmail.getText().isEmpty() ||
                textFieldDocumento.getText().isEmpty() ||
                textFieldContrasena.getText().isEmpty()) {

            mostrarAlerta("Campos vacíos",
                    "Por favor completa todos los campos obligatorios.",
                    Alert.AlertType.WARNING);
            return;
        }

        // ==========================================
        // Crear lista de direcciones inicial
        // ==========================================
        List<String> direcciones = new ArrayList<>();

        if (!textFieldDireccion.getText().isEmpty()) {
            direcciones.add(textFieldDireccion.getText());
        }

        // ==========================================
        // Crear nuevo usuario con Builder
        // ==========================================
        Usuario nuevoUsuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nombre(textFieldNombre.getText())
                .apellido(textFieldApellido.getText())
                .email(textFieldEmail.getText())
                .telefono(textFieldTelefono.getText())
                .celular(textFieldCelular.getText())
                .direcciones(direcciones)   // ← ahora sí funciona
                .documento(textFieldDocumento.getText())
                .contrasena(textFieldContrasena.getText())
                .build();

        // Registrar usuario
        boolean registrado = registroController.registrarUsuario(nuevoUsuario);

        if (registrado) {
            mostrarAlerta("Registro exitoso",
                    "Tu cuenta ha sido creada correctamente.",
                    Alert.AlertType.INFORMATION);

            volverLogin(null);

        } else {
            mostrarAlerta("Error",
                    "Ya existe un usuario con ese documento.",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    void volverLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) buttonRegistrar.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Inicio de sesión Amazen");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===========================================================
    // MÉTODO PARA MOSTRAR ALERTAS
    // ===========================================================
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
