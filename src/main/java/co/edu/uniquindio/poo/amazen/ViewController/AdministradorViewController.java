package co.edu.uniquindio.poo.amazen.ViewController;

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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AdministradorViewController {

    // ======= Campos de formulario =======
    @FXML private ComboBox<String> cmbRol;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCelular;
    @FXML private PasswordField txtContrasena;

    // Solo Repartidor
    @FXML private Label lblZona;
    @FXML private TextField txtZona;

    // ======= Sección Disponibilidad (Repartidor) =======
    @FXML private HBox hbxDisponibilidad;
    @FXML private ComboBox<String> cmbDisponibilidad;
    @FXML private Button btnCambiarDisponibilidad;
    @FXML private Button botonVolver;

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
    @FXML private TableColumn<Persona, String> colDisponibilidad;

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

        // 4) Estado base de secciones
        setDisponibilidadSection(false, null);
        setZonaVisible(false);

        // 5) Listener de selección
        tblPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel == null) {
                setCrudEnabled(true);               // modo crear
                setDisponibilidadSection(false, null);
                setZonaVisible(false);
                cmbRol.getSelectionModel().clearSelection();
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
                cmbRol.setValue("Administrador");
                setCrudEnabled(true);
                btnCrear.setDisable(true);               // no crear duplicado desde selección
                txtDocumento.setEditable(false);
                setDisponibilidadSection(false, null);
                setZonaVisible(false);

            } else if (sel instanceof Repartidor rep) {
                cmbRol.setValue("Repartidor");
                btnCrear.setDisable(true);
                btnActualizar.setDisable(false);
                btnEliminar.setDisable(false);
                txtDocumento.setEditable(false);
                setDisponibilidadSection(true, rep);
                setZonaVisible(true);
                txtZona.setText(nvl(rep.getZonaCobertura()));     // asumiendo getZona() existe

            } else { // Usuario
                cmbRol.setValue("Usuario");
                btnCrear.setDisable(true);
                btnActualizar.setDisable(false);
                btnEliminar.setDisable(false);
                txtDocumento.setEditable(false);
                setDisponibilidadSection(false, null);
                setZonaVisible(false);
            }
        });

        // 6) Roles disponibles para creación
        cmbRol.getItems().setAll("Administrador", "Usuario", "Repartidor");
        cmbRol.valueProperty().addListener((obs, oldV, rol) -> {
            boolean isRepartidor = "Repartidor".equals(rol);
            setZonaVisible(isRepartidor);
            // Para alta de repartidor, mostrar combo disponibilidad (valor por defecto)
            setDisponibilidadSection(isRepartidor, null);
            if (isRepartidor && cmbDisponibilidad != null) {
                if (cmbDisponibilidad.getItems().isEmpty()) {
                    cmbDisponibilidad.getItems().setAll("ACTIVO", "INACTIVO", "EN_RUTA");
                }
                cmbDisponibilidad.setValue("INACTIVO");
            }
        });

        // 7) Estado inicial: permitir crear
        setCrudEnabled(true);
    }

    // ======= Acciones =======

    @FXML
    private void onCrear() {
        try {
            String rol = cmbRol.getValue();
            if (rol == null || rol.isBlank()) {
                throw new IllegalArgumentException("Seleccione un rol para crear.");
            }

            switch (rol) {
                case "Administrador" -> {
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
                }
                case "Usuario" -> {
                    Usuario creado = controller.crearUsuario(
                            getTxt(txtNombre),
                            getTxt(txtApellido),
                            getTxt(txtEmail),
                            getTxt(txtTelefono),
                            getTxt(txtDireccion),
                            getTxt(txtCelular),
                            getTxt(txtDocumento),
                            getTxt(txtContrasena)
                    );
                    info("Creado", "Usuario: " + creado.getNombre() + " " + creado.getApellido());
                }
                case "Repartidor" -> {
                    String zona = getTxt(txtZona);
                    String dispStr = (cmbDisponibilidad != null && cmbDisponibilidad.getValue() != null)
                            ? cmbDisponibilidad.getValue() : "INACTIVO";
                    Disponibilidad disp = Disponibilidad.valueOf(dispStr);
                    Repartidor creado = controller.crearRepartidor(
                            getTxt(txtNombre),
                            getTxt(txtApellido),
                            getTxt(txtEmail),
                            getTxt(txtTelefono),
                            getTxt(txtDireccion),
                            getTxt(txtCelular),
                            getTxt(txtDocumento),
                            getTxt(txtContrasena),
                            zona,
                            disp
                    );
                    info("Creado", "Repartidor: " + creado.getNombre() + " (" + creado.getZonaCobertura() + ")");
                }
                default -> throw new IllegalArgumentException("Rol no soportado: " + rol);
            }

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

            if (seleccionado instanceof Administrador) {
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
            } else if (seleccionado instanceof Repartidor) {
                String zona = nullIfBlank(txtZona.getText());
                String dispStr = (cmbDisponibilidad != null) ? cmbDisponibilidad.getValue() : null;
                Disponibilidad disp = (dispStr == null || dispStr.isBlank()) ? null : Disponibilidad.valueOf(dispStr);

                boolean ok = controller.actualizarRepartidor(
                        doc,
                        nullIfBlank(txtNombre.getText()),
                        nullIfBlank(txtApellido.getText()),
                        nullIfBlank(txtEmail.getText()),
                        nullIfBlank(txtTelefono.getText()),
                        nullIfBlank(txtDireccion.getText()),
                        nullIfBlank(txtCelular.getText()),
                        nullIfBlank(txtContrasena.getText()),
                        zona,
                        disp
                );
                if (ok) info("Actualizado", "Repartidor actualizado.");
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
                if (ok) info("Actualizado", "Usuario actualizado.");
            }

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

    private void setZonaVisible(boolean show) {
        if (lblZona != null) { lblZona.setVisible(show); lblZona.setManaged(show); }
        if (txtZona != null) { txtZona.setVisible(show); txtZona.setManaged(show); }
    }

    private void limpiarFormulario() {
        cmbRol.getSelectionModel().clearSelection();
        txtDocumento.clear(); txtNombre.clear(); txtApellido.clear();
        txtEmail.clear(); txtTelefono.clear(); txtDireccion.clear();
        txtCelular.clear(); txtContrasena.clear(); txtZona.clear();
        tblPersonas.getSelectionModel().clearSelection();
        setCrudEnabled(true);
        setDisponibilidadSection(false, null);
        setZonaVisible(false);
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

            AmazenViewController avc = loader.getController();
            Persona persona = SesionUsuario.instancia().getPersona();
            EstrategiaVista estrategia = estrategiaPara(persona);
            avc.setEstrategiaVista(estrategia);

            Stage stage = (Stage) botonVolver.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Amazen");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver a la pantalla principal.");
        }
    }

    private EstrategiaVista estrategiaPara(Persona persona) {
        if (persona instanceof Administrador) return new EstrategiaAdmin();
        else if (persona instanceof Repartidor) return new EstrategiaRepartidor();
        else return new EsteategiaUsuario();
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    @FXML
    private void onAbrirEnvios(ActionEvent e) {
        final String FXML = "/co/edu/uniquindio/poo/amazen/admin_envios.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Gestión de Envíos (Asignación, Estados, Incidencias)");
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR, "No se pudo abrir la vista de envíos", ButtonType.OK);
            a.setHeaderText("Error"); a.showAndWait();
        }
    }

    @FXML
    private void onAbrirDashboard() {
        final String FXML = "/co/edu/uniquindio/poo/amazen/admin_dashboard.fxml";
        try {
            var loader = new javafx.fxml.FXMLLoader(getClass().getResource(FXML));
            var scene = new javafx.scene.Scene(loader.load());
            var stage = new javafx.stage.Stage();
            stage.setTitle("Panel de Métricas");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            var a = new Alert(Alert.AlertType.ERROR, "No se pudo abrir el panel de métricas", ButtonType.OK);
            a.setHeaderText("Error"); a.showAndWait();
        }
    }

}
