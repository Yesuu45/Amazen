package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Controller.LoginController;
import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;
import co.edu.uniquindio.poo.amazen.Model.TiendaSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controlador de la vista de login.
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 *     <li>Recibir las credenciales del usuario (documento y contraseña).</li>
 *     <li>Delegar la autenticación en {@link LoginController}.</li>
 *     <li>Guardar el usuario autenticado en la sesión compartida {@link TiendaSession}.</li>
 *     <li>Redirigir a la vista principal {@code amazen.fxml} cuando el login es correcto.</li>
 *     <li>Abrir la ventana de registro de usuario cuando se pulsa el enlace correspondiente.</li>
 * </ul>
 *
 * <p>Este controlador no conoce los detalles de cómo se validan las credenciales ni
 * cómo se almacenan los usuarios; solo coordina la interacción entre la vista
 * y la capa de lógica {@link LoginController}.</p>
 */
public class LoginViewController {

    // ================== FXML: CAMPOS DE ENTRADA ==================

    /** Campo de texto para el documento/ID del usuario. */
    @FXML private TextField textFieldID;

    /** Campo de texto para la contraseña del usuario. */
    @FXML private PasswordField textFieldPassword;

    // ================== FXML: BOTONES / ENLACES ==================

    /** Botón para intentar iniciar sesión con las credenciales ingresadas. */
    @FXML private Button buttonIngresar;

    /** Enlace para abrir la vista de registro de nuevo usuario. */
    @FXML private Hyperlink hyperlinkRegistrar;

    // ================== CONTROLADOR DE LÓGICA ==================

    /** Controlador que encapsula la lógica de autenticación. */
    private final LoginController loginController = new LoginController();

    // ================== ACCIONES DE LA VISTA ==================

    /**
     * Acción asociada al botón "Ingresar".
     *
     * <p>Flujo:</p>
     * <ol>
     *     <li>Lee documento y contraseña desde los campos de texto.</li>
     *     <li>Valida que no estén vacíos.</li>
     *     <li>Invoca {@link LoginController#iniciarSesion(String, String)}.</li>
     *     <li>Si las credenciales son válidas:
     *         <ul>
     *             <li>Obtiene el {@link Persona} autenticado desde {@link LoginController#getUsuarioActivo()}.</li>
     *             <li>Lo almacena en {@link TiendaSession} como persona activa.</li>
     *             <li>Muestra un mensaje de bienvenida.</li>
     *             <li>Abre el panel principal (Amazen) llamando a {@link #abrirAmazen()}.</li>
     *         </ul>
     *     </li>
     *     <li>En caso de error, muestra las alertas correspondientes (campos vacíos, credenciales inválidas, etc.).</li>
     * </ol>
     *
     * @param event evento de acción generado por el botón.
     */
    @FXML
    void buttonIngresar(ActionEvent event) {
        String documento  = textFieldID.getText() == null ? "" : textFieldID.getText().trim();
        String contrasena = textFieldPassword.getText() == null ? "" : textFieldPassword.getText().trim();

        if (documento.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor completa todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        boolean exito = loginController.iniciarSesion(documento, contrasena);

        if (!exito) {
            mostrarAlerta("Error", "Credenciales incorrectas. Verifique su ID o contraseña.", Alert.AlertType.ERROR);
            return;
        }

        Persona persona = loginController.getUsuarioActivo();
        if (persona == null) {
            mostrarAlerta("Error", "No se pudo recuperar la información del usuario.", Alert.AlertType.ERROR);
            return;
        }

        // Guardar usuario en la sesión compartida
        TiendaSession.getInstance().setPersonaActiva(persona);

        mostrarAlerta("Bienvenido", "Sesión iniciada correctamente para " + persona.getNombre(), Alert.AlertType.INFORMATION);
        abrirAmazen();
    }

    /**
     * Abre el panel principal (amazen.fxml) reutilizando la misma ventana del login.
     *
     * <p>La vista {@code amazen.fxml} está controlada por {@link AmazenViewController},
     * que se encargará de configurar la interfaz según el rol del usuario autenticado
     * (Administrador, Usuario, Repartidor) mediante el patrón Strategy.</p>
     */
    private void abrirAmazen() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("amazen.fxml"));
            Scene scene = new Scene(loader.load());

            // Reutilizar el stage actual
            Stage stage = (Stage) buttonIngresar.getScene().getWindow();
            stage.setTitle("Amazen - Panel principal");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el panel de Amazen.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Acción asociada al enlace "Registrar".
     *
     * <p>Abre una nueva ventana con la vista de registro de usuario
     * ({@code RegistroView.fxml}). No cierra la ventana de login actual.</p>
     *
     * @param event evento de acción generado por el clic en el hyperlink.
     */
    @FXML
    void hyperlinkRegistrar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("RegistroView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Registro de Usuario");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de registro.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Muestra una alerta simple con el título, mensaje y tipo especificados.
     *
     * @param titulo  título de la ventana de alerta.
     * @param mensaje contenido del mensaje a mostrar.
     * @param tipo    tipo de alerta (INFORMATION, WARNING, ERROR, etc.).
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
