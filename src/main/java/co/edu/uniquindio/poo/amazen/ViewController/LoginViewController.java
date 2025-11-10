package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Controller.LoginController;
import co.edu.uniquindio.poo.amazen.Model.Persona.*;
import co.edu.uniquindio.poo.amazen.Model.Strategy.*;
import co.edu.uniquindio.poo.amazen.Model.TiendaSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginViewController {

    @FXML private TextField textFieldID;
    @FXML private PasswordField textFieldPassword;
    @FXML private Button buttonIngresar;
    @FXML private Hyperlink hyperlinkRegistrar;

    private final LoginController loginController = new LoginController();

    @FXML
    void buttonIngresar(ActionEvent event) {
        String documento = textFieldID.getText().trim();
        String contrasena = textFieldPassword.getText().trim();

        if(documento.isEmpty() || contrasena.isEmpty()){
            mostrarAlerta("Campos vacíos", "Por favor completa todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        boolean exito = loginController.iniciarSesion(documento, contrasena);

        if(exito){
            Persona persona = loginController.getPersonaActiva();
            TiendaSession.getInstance().setPersonaActiva(persona);

            mostrarAlerta("Bienvenido", "Sesión iniciada correctamente para " + persona.getNombre(), Alert.AlertType.INFORMATION);
            abrirAmazen();

        } else {

            // 🔹 Guardar el usuario logueado globalmente
            co.edu.uniquindio.poo.amazen.Model.Persona.SesionUsuario
                    .instancia().iniciarSesion(persona);

            mostrarAlerta("Bienvenido", "Sesión iniciada correctamente para " + persona.getNombre(), Alert.AlertType.INFORMATION);
            abrirVentanaPrincipal(persona);
            ((Stage) buttonIngresar.getScene().getWindow()).close();
        }
        else {
            mostrarAlerta("Error", "Credenciales incorrectas. Verifique su ID o contraseña.", Alert.AlertType.ERROR);
        }
    }

    private void abrirAmazen() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("amazen.fxml"));
            Scene scene = new Scene(loader.load());

            AmazenViewController controller = loader.getController();

            // Estrategia según tipo de usuario
            Persona persona = TiendaSession.getInstance().getPersonaActiva();
            if (persona instanceof Administrador) {
                controller.setEstrategiaVista(new EstrategiaAdmin());
            } else if (persona instanceof Usuario) {
                controller.setEstrategiaVista(new EsteategiaUsuario());
            } else if (persona instanceof Repartidor) {
                controller.setEstrategiaVista(new EstrategiaRepartidor());
            }

            Stage stage = (Stage) buttonIngresar.getScene().getWindow();
            stage.setTitle("Panel Amazen");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el panel de Amazen.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void hyperlinkRegistrar(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(App.class.getResource("RegistroView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Registro de Usuario");
            stage.setScene(scene);
            stage.show();
        } catch(Exception e){
            mostrarAlerta("Error", "No se pudo abrir la ventana de registro.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo){
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
