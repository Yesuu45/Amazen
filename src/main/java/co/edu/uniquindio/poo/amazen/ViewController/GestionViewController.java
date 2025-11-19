package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.Controller.GestionUsuariosController;
import co.edu.uniquindio.poo.amazen.Model.Persona.SesionUsuario;
import co.edu.uniquindio.poo.amazen.Model.Persona.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Controlador de la vista de gestión de perfil de usuario.
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 *     <li>Cargar y mostrar los datos personales (nombre, email, teléfonos, documento)
 *         del usuario actualmente autenticado.</li>
 *     <li>Permitir actualizar la información básica del usuario (excepto el documento).</li>
 *     <li>Gestión de direcciones de envío del usuario: agregar y eliminar.</li>
 *     <li>Delegar la lógica de persistencia y validaciones a
 *         {@link GestionUsuariosController}.</li>
 * </ul>
 *
 * <p>Esta vista está pensada para el rol de {@link Usuario}, usando la sesión
 * actual obtenida a través de {@link SesionUsuario}.</p>
 */
public class GestionViewController {

    /** Controlador de lógica de negocio para gestión de usuarios. */
    private final GestionUsuariosController gestionUsuariosController = new GestionUsuariosController();

    // ========================= FXML: LISTA DE DIRECCIONES =========================

    /** Lista visual de direcciones del usuario. */
    @FXML
    private ListView<String> listaDirecciones;

    // ========================= FXML: CAMPOS DE DATOS PERSONALES =========================

    /** Campo para el nombre del usuario. */
    @FXML
    private TextField txtNombre;

    /** Campo para el celular del usuario. */
    @FXML
    private TextField txtCelular;

    /** Campo para el teléfono fijo del usuario. */
    @FXML
    private TextField txtTelefono;

    /** Campo para el correo electrónico del usuario. */
    @FXML
    private TextField txtEmail;

    /** Campo para el apellido del usuario. */
    @FXML
    private TextField txtApellido;

    /** Campo para el documento del usuario (solo lectura en esta vista). */
    @FXML
    private TextField txtDocumento;

    // ========================= FXML: GESTIÓN DE DIRECCIONES =========================

    /** Campo para escribir una nueva dirección a agregar. */
    @FXML
    private TextField txtNuevaDireccion;

    /** Botón para agregar una nueva dirección a la lista. */
    @FXML
    private Button btnAgregarDireccion;

    /** Botón para eliminar la dirección seleccionada. */
    @FXML
    private Button btnEliminarDireccion;

    /** Botón para confirmar la edición de la información personal. */
    @FXML
    private Button btnEditarInfo;


    // ============================================================
    // INICIALIZAR VISTA
    // ============================================================

    /**
     * Inicializa la vista de gestión de usuario.
     *
     * <p>Se llama automáticamente al cargar el FXML. Realiza:</p>
     * <ul>
     *   <li>Carga los datos del usuario en sesión en los campos de texto.</li>
     *   <li>Carga las direcciones actuales en el {@link ListView}.</li>
     * </ul>
     */
    @FXML
    public void initialize() {
        cargarDatosUsuario();
        cargarDirecciones();
    }


    // ============================================================
    // CARGAR DATOS DEL USUARIO EN SESIÓN
    // ============================================================

    /**
     * Obtiene el {@link Usuario} de la sesión actual y coloca sus datos
     * personales en los campos correspondientes.
     *
     * <p>El documento se marca como no editable para evitar inconsistencias
     * con la clave primaria usada en el sistema.</p>
     */
    private void cargarDatosUsuario() {

        Usuario u = (Usuario) SesionUsuario.instancia().getPersona();

        txtNombre.setText(u.getNombre());
        txtApellido.setText(u.getApellido());
        txtEmail.setText(u.getEmail());
        txtTelefono.setText(u.getTelefono());
        txtCelular.setText(u.getCelular());
        txtDocumento.setText(u.getDocumento());
        txtDocumento.setDisable(true); // ← NO EDITABLE
    }


    // ============================================================
    // CARGAR DIRECCIONES DESDE EL ARCHIVO
    // ============================================================

    /**
     * Carga la lista de direcciones asociadas al usuario en sesión y las
     * muestra en el {@link ListView}.
     *
     * <p>Las direcciones se obtienen a través de
     * {@link GestionUsuariosController#obtenerDirecciones(String)}.</p>
     */
    private void cargarDirecciones() {

        String documento = SesionUsuario.instancia().getPersona().getDocumento();
        List<String> direcciones = gestionUsuariosController.obtenerDirecciones(documento);

        listaDirecciones.getItems().setAll(direcciones);
    }


    // ============================================================
    // BOTÓN: EDITAR INFORMACIÓN
    // ============================================================

    /**
     * Acción del botón "Editar información".
     *
     * <p>Intenta actualizar la información básica del usuario (nombre, apellido,
     * email, teléfonos) usando el {@link GestionUsuariosController} y, si tiene
     * éxito, sincroniza también el objeto {@link Usuario} almacenado en
     * {@link SesionUsuario}.</p>
     *
     * @param event evento de acción generado por el botón (no se usa directamente).
     */
    @FXML
    void editarInformacion(ActionEvent event) {

        String documento = SesionUsuario.instancia().getPersona().getDocumento();

        boolean ok = gestionUsuariosController.actualizarInformacionUsuario(
                documento,
                txtNombre.getText(),
                txtApellido.getText(),
                txtEmail.getText(),
                txtTelefono.getText(),
                txtCelular.getText()
        );

        if (ok) {
            mostrarInfo("Información actualizada correctamente.");

            // Actualizar también los datos en el objeto de sesión
            Usuario u = (Usuario) SesionUsuario.instancia().getPersona();
            u.setNombre(txtNombre.getText());
            u.setApellido(txtApellido.getText());
            u.setEmail(txtEmail.getText());
            u.setTelefono(txtTelefono.getText());
            u.setCelular(txtCelular.getText());

        } else {
            mostrarError("No se pudo actualizar la información.");
        }
    }


    // ============================================================
    // BOTÓN: AGREGAR DIRECCIÓN
    // ============================================================

    /**
     * Acción del botón "Agregar dirección".
     *
     * <p>Valida que el campo de nueva dirección no esté vacío y, si es válido,
     * solicita al {@link GestionUsuariosController} agregarla a la lista
     * persistida de direcciones del usuario. Luego recarga la lista visual.</p>
     *
     * @param event evento de acción generado por el botón (no se usa directamente).
     */
    @FXML
    void agregarDireccion(ActionEvent event) {

        String documento = SesionUsuario.instancia().getPersona().getDocumento();
        String nuevaDireccion = txtNuevaDireccion.getText();

        if (nuevaDireccion.isBlank()) {
            mostrarError("Debe escribir una nueva dirección.");
            return;
        }

        boolean ok = gestionUsuariosController.agregarDireccion(documento, nuevaDireccion);

        if (ok) {
            mostrarInfo("Dirección agregada correctamente.");
            cargarDirecciones();
            txtNuevaDireccion.clear();
        } else {
            mostrarError("Error al agregar la dirección.");
        }
    }


    // ============================================================
    // BOTÓN: ELIMINAR DIRECCIÓN
    // ============================================================

    /**
     * Acción del botón "Eliminar dirección".
     *
     * <p>Verifica que el usuario haya seleccionado una dirección en la lista,
     * y si es así, pide al {@link GestionUsuariosController} que la elimine.
     * Finalmente, recarga la lista de direcciones para reflejar el cambio.</p>
     *
     * @param event evento de acción generado por el botón (no se usa directamente).
     */
    @FXML
    void eliminarDireccion(ActionEvent event) {

        String documento = SesionUsuario.instancia().getPersona().getDocumento();
        String seleccionada = listaDirecciones.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Debe seleccionar una dirección primero.");
            return;
        }

        boolean ok = gestionUsuariosController.eliminarDireccion(documento, seleccionada);

        if (ok) {
            mostrarInfo("Dirección eliminada.");
            cargarDirecciones();
        } else {
            mostrarError("No se pudo eliminar la dirección.");
        }
    }


    // ============================================================
    // ALERTAS
    // ============================================================

    /**
     * Muestra un cuadro de diálogo informativo con el mensaje indicado.
     *
     * @param mensaje texto a mostrar al usuario.
     */
    private void mostrarInfo(String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    /**
     * Muestra un cuadro de diálogo de error con el mensaje indicado.
     *
     * @param mensaje texto a mostrar al usuario.
     */
    private void mostrarError(String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}
