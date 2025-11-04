package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.Controller.LoginController;
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


        } else {
            mostrarAlerta("Error", "Credenciales incorrectas. Verifique su ID o contraseña.", Alert.AlertType.ERROR);
        }
    }

        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
