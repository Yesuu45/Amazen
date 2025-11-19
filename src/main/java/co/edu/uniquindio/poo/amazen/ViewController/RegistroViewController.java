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

/**
 * Controlador de la vista de registro de usuarios.
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 *     <li>Leer los datos del formulario de registro.</li>
 *     <li>Validar que los campos obligatorios estén diligenciados.</li>
 *     <li>Construir un objeto {@link Usuario} usando el patrón Builder.</li>
 *     <li>Delegar en {@link RegistroController} el proceso de registro.</li>
 *     <li>Notificar al usuario con alertas de éxito o error.</li>
 *     <li>Redirigir a la pantalla de login una vez completado el registro.</li>
 * </ul>
 */
public class RegistroViewController {

    // ================== CAMPOS DE FORMULARIO ==================

    /** Campo para el nombre del nuevo usuario. */
    @FXML private TextField textFieldNombre;

    /** Campo para el apellido del nuevo usuario. */
    @FXML private TextField textFieldApellido;

    /** Campo para el correo electrónico del nuevo usuario. */
    @FXML private TextField textFieldEmail;

    /** Campo para el teléfono fijo del nuevo usuario. */
    @FXML private TextField textFieldTelefono;

    /** Campo para el teléfono celular del nuevo usuario. */
    @FXML private TextField textFieldCelular;

    /** Campo para una dirección inicial (se guardará en la lista de direcciones). */
    @FXML private TextField textFieldDireccion;

    /** Campo para el documento de identidad (actúa como identificador lógico). */
    @FXML private TextField textFieldDocumento;

    /** Campo para la contraseña del nuevo usuario. */
    @FXML private PasswordField textFieldContrasena;

    /** Botón principal para ejecutar la acción de registro. */
    @FXML private Button buttonRegistrar;

    // ================== CONTROLADOR DE NEGOCIO ==================

    /**
     * Controlador de capa lógica encargado de registrar el usuario
     * en el modelo/persistencia.
     */
    private final RegistroController registroController = new RegistroController();

    // ================== ACCIONES DE LA VISTA ==================

    /**
     * Acción del botón "Registrar".
     *
     * <p>Flujo:</p>
     * <ol>
     *     <li>Valida que los campos obligatorios no estén vacíos
     *         (nombre, apellido, email, documento, contraseña).</li>
     *     <li>Construye una lista de direcciones inicial (si se diligenció el campo).</li>
     *     <li>Crea un {@link Usuario} usando el builder, generando un {@link UUID} como id.</li>
     *     <li>Llama a {@link RegistroController#registrarUsuario(Usuario)}.</li>
     *     <li>Muestra una alerta de éxito o de error según el resultado del registro.</li>
     *     <li>En caso de registro exitoso, vuelve a la pantalla de login.</li>
     * </ol>
     *
     * @param event evento de acción disparado por el botón (no se usa directamente).
     */
    @FXML
    void buttonRegistrar(ActionEvent event) {

        // 1) Validar campos obligatorios
        if (textFieldNombre.getText().isEmpty() ||
                textFieldApellido.getText().isEmpty() ||
                textFieldEmail.getText().isEmpty() ||
                textFieldDocumento.getText().isEmpty() ||
                textFieldContrasena.getText().isEmpty()) {

            mostrarAlerta(
                    "Campos vacíos",
                    "Por favor completa todos los campos obligatorios.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        // 2) Crear lista de direcciones inicial
        List<String> direcciones = new ArrayList<>();
        if (!textFieldDireccion.getText().isEmpty()) {
            direcciones.add(textFieldDireccion.getText());
        }

        // 3) Construir nuevo usuario con Builder
        Usuario nuevoUsuario = Usuario.builder()
                .id(UUID.randomUUID())                       // Identificador interno único
                .nombre(textFieldNombre.getText())
                .apellido(textFieldApellido.getText())
                .email(textFieldEmail.getText())
                .telefono(textFieldTelefono.getText())
                .celular(textFieldCelular.getText())
                .direcciones(direcciones)                    // Lista inicial de direcciones
                .documento(textFieldDocumento.getText())
                .contrasena(textFieldContrasena.getText())
                .build();

        // 4) Registrar usuario en la capa de negocio
        boolean registrado = registroController.registrarUsuario(nuevoUsuario);

        // 5) Notificar resultado
        if (registrado) {
            mostrarAlerta(
                    "Registro exitoso",
                    "Tu cuenta ha sido creada correctamente.",
                    Alert.AlertType.INFORMATION
            );

            // Volver automáticamente a la ventana de login
            volverLogin(null);

        } else {
            mostrarAlerta(
                    "Error",
                    "Ya existe un usuario con ese documento.",
                    Alert.AlertType.ERROR
            );
        }
    }

    /**
     * Acción para volver a la ventana de login.
     *
     * <p>Reutiliza la misma ventana del registro para cargar {@code login.fxml},
     * cambiando simplemente la escena y el título.</p>
     *
     * @param event evento de acción, puede ser {@code null} cuando se llama
     *              internamente después de un registro exitoso.
     */
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

    // ================== UTILIDADES (ALERTAS) ==================

    /**
     * Muestra una alerta genérica con el título, mensaje y tipo indicados.
     *
     * @param titulo  título de la ventana de alerta.
     * @param mensaje contenido principal a mostrar.
     * @param tipo    tipo de alerta (información, advertencia, error, etc.).
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
