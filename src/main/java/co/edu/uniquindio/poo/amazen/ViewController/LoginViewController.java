package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.Controller.LoginController;
import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginViewController {

    @FXML
    private TextField textFieldID;

    @FXML
    private PasswordField textFieldPassword;

    @FXML
    private Button buttonIngresar;

    @FXML
    private Hyperlink hyperlinkRegistrar;

    private final LoginController loginController = new LoginController();

    /**
     * Acción del botón "Ingresar"
     */
    @FXML
    void buttonIngresar(ActionEvent event) {
        String documento = textFieldID.getText().trim();
        String contrasena = textFieldPassword.getText().trim();

        if (documento.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor, complete todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        boolean exito = loginController.iniciarSesion(documento, contrasena);

        if (exito) {
            Persona persona = loginController.getPersonaActiva();
            mostrarAlerta("Bienvenido", "Sesión iniciada correctamente para " + persona.getNombre(), Alert.AlertType.INFORMATION);

            // 👉 Abrir la ventana principal (ejemplo: MainView.fxml)
            abrirVentanaPrincipal();

            // Cerrar la ventana de login
            Stage stage = (Stage) buttonIngresar.getScene().getWindow();
            stage.close();

        } else {
            mostrarAlerta("Error", "Credenciales incorrectas. Verifique su ID o contraseña.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Acción del enlace "Registrar"
     */
    @FXML
    void hyperlinkRegistrar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/poo/amazen/View/RegistroView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Registro de Usuario");
            stage.setScene(scene);
            stage.show();

            // Cerrar ventana actual
            Stage currentStage = (Stage) hyperlinkRegistrar.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de registro.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar para abrir la ventana principal después del login
     */
    private void abrirVentanaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/poo/amazen/View/MainView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Panel Principal - Amazén");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana principal.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Métodos opcionales para efectos visuales del botón
     */
    @FXML
    void hoverButton() {
        buttonIngresar.setStyle("-fx-background-color: #005fa3; -fx-text-fill: white; -fx-background-radius: 10;");
    }

    @FXML
    void exitHover() {
        buttonIngresar.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-background-radius: 10;");
    }

    /**
     * Método para mostrar alertas de JavaFX
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
