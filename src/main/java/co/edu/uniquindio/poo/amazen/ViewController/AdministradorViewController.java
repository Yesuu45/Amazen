package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Model.Persona.SesionUsuario;
import co.edu.uniquindio.poo.amazen.Controller.AdministradorController;
import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.Disponibilidad;
import co.edu.uniquindio.poo.amazen.Model.Persona.Administrador;
import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;
import co.edu.uniquindio.poo.amazen.Model.Persona.Repartidor;
import co.edu.uniquindio.poo.amazen.Model.Persona.Usuario;
import co.edu.uniquindio.poo.amazen.Model.Strategy.EsteategiaUsuario;
import co.edu.uniquindio.poo.amazen.Model.Strategy.EstrategiaAdmin;
import co.edu.uniquindio.poo.amazen.Model.Strategy.EstrategiaRepartidor;
import co.edu.uniquindio.poo.amazen.Model.Strategy.EstrategiaVista;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AdministradorViewController {

    // ======= Campos de formulario =======
    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCelular;
    @FXML private PasswordField txtContrasena;

    // ======= Sección Disponibilidad (Repartidor) =======
    @FXML private HBox hbxDisponibilidad;                // Contenedor visible/managed
    @FXML private ComboBox<String> cmbDisponibilidad;     // ACTIVO / INACTIVO / EN_RUTA
    @FXML private Button btnCambiarDisponibilidad;
    @FXML private Button botonVolver;// Guardar

    // ======= Tabla y columnas =======
    @FXML private TableView<Persona> tblPersonas;
    @FXML private TableColumn<Persona, String> colDocumento;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, String> colApellido;
    @FXML private TableColumn<Persona, String> colEmail;
    @FXML private TableColumn<Persona, String> colTelefono;
    @FXML private TableColumn<Persona, String> colCelular;
    @FXML private TableColumn<Persona, String> colDireccion;
    @FXML private TableColumn<Persona, String> colCargo;
    @FXML private TableColumn<Persona, String> colDisponibilidad; // NUEVA

    // ======= Botones =======
    @FXML private Button btnCrear;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnLoginTest;

    private final AdministradorController controller = new AdministradorController();
    private final ObservableList<Persona> personasView = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1) Forzar carga del singleton (usuarios demo)
        Amazen.getInstance();

        // 2) Configurar columnas
        colDocumento.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getDocumento())));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getNombre())));
        colApellido.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getApellido())));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getEmail())));
        colTelefono.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getTelefono())));
        colCelular.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getCelular())));
        colDireccion.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getDireccion())));
        colCargo.setCellValueFactory(c -> new SimpleStringProperty(getCargo(c.getValue())));
        // NUEVA: Disponibilidad
        colDisponibilidad.setCellValueFactory(c -> {
            Persona p = c.getValue();
            if (p instanceof Repartidor r && r.getDisponibilidad() != null) {
                return new SimpleStringProperty(r.getDisponibilidad().name());
            } else {
                return new SimpleStringProperty("—");
            }
        });

        // 3) Poblar tabla
        personasView.setAll(Amazen.getInstance().getListaPersonas());
        tblPersonas.setItems(personasView);
        tblPersonas.setPlaceholder(new Label("No hay personas para mostrar"));

        // 4) Estado base de la sección de disponibilidad
        setDisponibilidadSection(false, null);

        // 5) Listener de selección
        tblPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel == null) {
                setCrudEnabled(true);               // modo crear admin
                setDisponibilidadSection(false, null);
                return;
            }

            // Rellenar campos comunes
            txtDocumento.setText(nvl(sel.getDocumento()));
            txtNombre.setText(nvl(sel.getNombre()));
            txtApellido.setText(nvl(sel.getApellido()));
            txtEmail.setText(nvl(sel.getEmail()));
            txtTelefono.setText(nvl(sel.getTelefono()));
            txtDireccion.setText(nvl(sel.getDireccion()));
            txtCelular.setText(nvl(sel.getCelular()));
            txtContrasena.clear();

            if (sel instanceof Administrador) {
                // CRUD completo
                setCrudEnabled(true);
                btnCrear.setDisable(false);
                txtDocumento.setEditable(true);
                setDisponibilidadSection(false, null);
            } else if (sel instanceof Repartidor rep) {
                // Actualizar/eliminar, NO crear; mostrar disponibilidad
                btnCrear.setDisable(true);
                btnActualizar.setDisable(false);
                btnEliminar.setDisable(false);
                txtDocumento.setEditable(false);
                setDisponibilidadSection(true, rep);
            } else { // Usuario
                btnCrear.setDisable(true);
                btnActualizar.setDisable(false);
                btnEliminar.setDisable(false);
                txtDocumento.setEditable(false);
                setDisponibilidadSection(false, null);
            }
        });

        // 6) Estado inicial: permitir crear admin
        setCrudEnabled(true);
    }

    // ======= Acciones =======

    @FXML
    private void onCrear() {
        try {
            Administrador creado = controller.crearAdministrador(
                    getTxt(txtNombre),
                    getTxt(txtApellido),
                    getTxt(txtEmail),
                    getTxt(txtTelefono),
                    getTxt(txtDireccion),
                    getTxt(txtCelular),
                    getTxt(txtDocumento),
                    getTxt(txtContrasena)
            );
            info("Creado", "Administrador: " + creado.getNombre() + " " + creado.getApellido());
            personasView.setAll(Amazen.getInstance().getListaPersonas());
            tblPersonas.refresh();
            limpiarFormulario();
        } catch (Exception e) {
            error("No se pudo crear", e.getMessage());
        }
    }

    @FXML
    private void onActualizar() {
        try {
            var seleccionado = tblPersonas.getSelectionModel().getSelectedItem();
            String doc = getTxt(txtDocumento);

            if (seleccionado == null) {
                boolean ok = controller.actualizarAdministrador(
                        doc,
                        nullIfBlank(txtNombre.getText()),
                        nullIfBlank(txtApellido.getText()),
                        nullIfBlank(txtEmail.getText()),
                        nullIfBlank(txtTelefono.getText()),
                        nullIfBlank(txtDireccion.getText()),
                        nullIfBlank(txtCelular.getText()),
                        nullIfBlank(txtContrasena.getText())
                );
                if (ok) info("Actualizado", "Datos actualizados.");
            } else if (seleccionado instanceof Administrador) {
                boolean ok = controller.actualizarAdministrador(
                        doc,
                        nullIfBlank(txtNombre.getText()),
                        nullIfBlank(txtApellido.getText()),
                        nullIfBlank(txtEmail.getText()),
                        nullIfBlank(txtTelefono.getText()),
                        nullIfBlank(txtDireccion.getText()),
                        nullIfBlank(txtCelular.getText()),
                        nullIfBlank(txtContrasena.getText())
                );
                if (ok) info("Actualizado", "Administrador actualizado.");
            } else {
                boolean ok = controller.actualizarPersona(
                        doc,
                        nullIfBlank(txtNombre.getText()),
                        nullIfBlank(txtApellido.getText()),
                        nullIfBlank(txtEmail.getText()),
                        nullIfBlank(txtTelefono.getText()),
                        nullIfBlank(txtDireccion.getText()),
                        nullIfBlank(txtCelular.getText()),
                        nullIfBlank(txtContrasena.getText())
                );
                if (ok) info("Actualizado", "Datos actualizados.");
            }

            // refrescar todo (incluye columna Disponibilidad)
            personasView.setAll(Amazen.getInstance().getListaPersonas());
            tblPersonas.refresh();
            txtContrasena.clear();

        } catch (Exception e) {
            error("No se pudo actualizar", e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        try {
            var seleccionado = tblPersonas.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                error("Sin selección", "Selecciona primero una persona de la tabla.");
                return;
            }

            String doc = getTxt(txtDocumento);
            if (!confirm("Eliminar", "¿Eliminar a " + seleccionado.getNombre() + " (" + seleccionado.getDocumento() + ")?")) {
                return;
            }

            if (seleccionado instanceof Administrador) {
                controller.eliminarAdministrador(doc);
            } else {
                controller.eliminarPersona(doc);
            }

            personasView.setAll(Amazen.getInstance().getListaPersonas());
            tblPersonas.refresh();
            info("Eliminado", "Registro eliminado.");
            limpiarFormulario();

        } catch (Exception e) {
            error("No se pudo eliminar", e.getMessage());
        }
    }

    @FXML private void onLimpiar() { limpiarFormulario(); }

    @FXML
    private void onLoginTest() {
        TextInputDialog d1 = new TextInputDialog();
        d1.setHeaderText("Prueba Login - Documento");
        d1.setContentText("Documento:");
        String doc = d1.showAndWait().orElse(null);
        if (doc == null) return;

        TextInputDialog d2 = new TextInputDialog();
        d2.setHeaderText("Prueba Login - Contraseña");
        d2.setContentText("Contraseña:");
        String pass = d2.showAndWait().orElse(null);
        if (pass == null) return;

        var ok = controller.login(doc, pass);
        if (ok.isPresent()) info("Login OK", "Bienvenido/a " + ok.get().getNombre());
        else error("Login fallido", "Credenciales inválidas.");
    }

    // ======= Cambiar disponibilidad (Repartidor) =======
    @FXML
    private void onCambiarDisponibilidad() {
        var seleccionado = tblPersonas.getSelectionModel().getSelectedItem();
        if (!(seleccionado instanceof Repartidor rep)) {
            error("Acción inválida", "Debes seleccionar un repartidor.");
            return;
        }
        String nueva = (cmbDisponibilidad != null) ? cmbDisponibilidad.getValue() : null;
        if (nueva == null || nueva.isBlank()) {
            error("Campo vacío", "Selecciona una disponibilidad.");
            return;
        }
        try {
            controller.cambiarDisponibilidad(rep.getDocumento(), Disponibilidad.valueOf(nueva));
            info("Éxito", "Disponibilidad actualizada a " + nueva + ".");
            // refresca para que la columna muestre el valor actualizado
            tblPersonas.refresh();
        } catch (Exception e) {
            error("No se pudo guardar", e.getMessage());
        }
    }

    // ======= Helpers =======

    private void setCrudEnabled(boolean enabled) {
        btnCrear.setDisable(!enabled);
        btnActualizar.setDisable(!enabled);
        btnEliminar.setDisable(!enabled);
        txtDocumento.setEditable(enabled);
    }

    private void setDisponibilidadSection(boolean show, Repartidor rep) {
        if (hbxDisponibilidad != null) {
            hbxDisponibilidad.setVisible(show);
            hbxDisponibilidad.setManaged(show);
        }
        if (!show) {
            if (cmbDisponibilidad != null) {
                cmbDisponibilidad.setDisable(true);
                cmbDisponibilidad.setValue(null);
            }
            if (btnCambiarDisponibilidad != null) btnCambiarDisponibilidad.setDisable(true);
            return;
        }
        if (cmbDisponibilidad != null) {
            if (cmbDisponibilidad.getItems().isEmpty()) {
                cmbDisponibilidad.getItems().setAll("ACTIVO", "INACTIVO", "EN_RUTA");
            }
            String value = (rep != null && rep.getDisponibilidad() != null)
                    ? rep.getDisponibilidad().name()
                    : "INACTIVO";
            cmbDisponibilidad.setValue(value);
            cmbDisponibilidad.setDisable(false);
        }
        if (btnCambiarDisponibilidad != null) btnCambiarDisponibilidad.setDisable(false);
    }

    private void limpiarFormulario() {
        txtDocumento.clear(); txtNombre.clear(); txtApellido.clear();
        txtEmail.clear(); txtTelefono.clear(); txtDireccion.clear();
        txtCelular.clear(); txtContrasena.clear();
        tblPersonas.getSelectionModel().clearSelection();
        setCrudEnabled(true);                      // modo crear admin por defecto
        setDisponibilidadSection(false, null);     // ocultar sección
    }

    private static String getTxt(TextField t) {
        String v = t.getText() == null ? "" : t.getText().trim();
        if (v.isBlank()) throw new IllegalArgumentException("Campo requerido: " + t.getPromptText());
        return v;
    }

    private static String nullIfBlank(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isBlank() ? null : t;
    }

    private static String nvl(String s) { return s == null ? "" : s; }

    private String getCargo(Persona p) {
        if (p instanceof Administrador) return "Administrador";
        if (p instanceof Repartidor)    return "Repartidor";
        if (p instanceof Usuario)       return "Usuario";
        return "Desconocido";
    }

    private void info(String title, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
        a.setHeaderText(title); a.showAndWait();
    }

    private void error(String title, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        a.setHeaderText(title); a.showAndWait();
    }

    private boolean confirm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        var result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    @FXML
    private void onVolver() {
        final String FXML_AMAZEN = "/co/edu/uniquindio/poo/amazen/amazen.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_AMAZEN));
            Scene scene = new Scene(loader.load());

            // Obtener el controlador y la persona logueada
            AmazenViewController avc = loader.getController();
            Persona persona = SesionUsuario.instancia().getPersona();

            // Aplicar la estrategia correspondiente
            if (persona != null) {
                EstrategiaVista estrategia = estrategiaPara(persona);
                avc.setEstrategiaVista(estrategia);
            }

            // Volver a la ventana principal
            Stage stage = (Stage) botonVolver.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Amazen");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver a la pantalla principal.");
        }
    }

    /** Devuelve la estrategia visual según el tipo de usuario logueado */
    private EstrategiaVista estrategiaPara(Persona persona) {
        if (persona instanceof Administrador) {
            return new EstrategiaAdmin();
        } else if (persona instanceof Repartidor) {
            return new EstrategiaRepartidor();
        } else {
            return new EsteategiaUsuario();
        }
    }







    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarInfo(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}
