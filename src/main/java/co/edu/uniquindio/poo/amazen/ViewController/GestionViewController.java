package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.Controller.GestionUsuariosController;
import co.edu.uniquindio.poo.amazen.Model.Persona.SesionUsuario;
import co.edu.uniquindio.poo.amazen.Model.Persona.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class GestionViewController {

    private final GestionUsuariosController gestionUsuariosController = new GestionUsuariosController();

    @FXML
    private ListView<String> listaDirecciones;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtCelular;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtDocumento;

    @FXML
    private TextField txtNuevaDireccion;

    @FXML
    private Button btnAgregarDireccion;

    @FXML
    private Button btnEliminarDireccion;

    @FXML
    private Button btnEditarInfo;


    // ============================================================
    // INICIALIZAR VISTA
    // ============================================================
    @FXML
    public void initialize() {
        cargarDatosUsuario();
        cargarDirecciones();
    }


    // ============================================================
    // CARGAR DATOS DEL USUARIO EN SESIÓN
    // ============================================================
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
    private void cargarDirecciones() {

        String documento = SesionUsuario.instancia().getPersona().getDocumento();
        List<String> direcciones = gestionUsuariosController.obtenerDirecciones(documento);

        listaDirecciones.getItems().setAll(direcciones);
    }


    // ============================================================
    // BOTÓN: EDITAR INFORMACIÓN
    // ============================================================
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
    private void mostrarInfo(String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}

