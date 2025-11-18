package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.Controller.LoginController;
import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Controller.AdministradorController;
import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.Disponibilidad;
import co.edu.uniquindio.poo.amazen.Model.Persona.Administrador;
import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;
import co.edu.uniquindio.poo.amazen.Model.Persona.Repartidor;
import co.edu.uniquindio.poo.amazen.Model.Persona.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdministradorViewController {

    // ======= Campos de formulario =======
    @FXML private ComboBox<String> cmbRol;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion; // múltiples direcciones separadas por coma
    @FXML private TextField txtCelular;
    @FXML private PasswordField txtContrasena;

    // Solo Repartidor
    @FXML private Label lblZona;
    @FXML private TextField txtZona;

    // Sección Disponibilidad (Repartidor)
    @FXML private HBox hbxDisponibilidad;
    @FXML private ComboBox<String> cmbDisponibilidad;
    @FXML private Button btnCambiarDisponibilidad;

    // Tabla y columnas
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

    // Botones CRUD
    @FXML private Button btnCrear;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;

    // Botones extra de la vista
    @FXML private Button btnLoginTest;
    @FXML private Button botonVolver;

    private final AdministradorController controller = new AdministradorController();
    private final LoginController loginController = new LoginController();
    private final ObservableList<Persona> personasView = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        // Inicializar singleton
        Amazen.getInstance();

        // Configurar columnas
        colDocumento.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getDocumento())));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getNombre())));
        colApellido.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getApellido())));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getEmail())));
        colTelefono.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getTelefono())));
        colCelular.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getCelular())));
        colDireccion.setCellValueFactory(c -> {
            Persona p = c.getValue();
            if (p.getDirecciones() != null && !p.getDirecciones().isEmpty()) {
                return new SimpleStringProperty(String.join(", ", p.getDirecciones()));
            } else {
                return new SimpleStringProperty("—");
            }
        });
        colCargo.setCellValueFactory(c -> new SimpleStringProperty(getCargo(c.getValue())));
        colDisponibilidad.setCellValueFactory(c -> {
            Persona p = c.getValue();
            if (p instanceof Repartidor r && r.getDisponibilidad() != null) {
                return new SimpleStringProperty(r.getDisponibilidad().name());
            } else {
                return new SimpleStringProperty("—");
            }
        });

        // Poblar tabla
        personasView.setAll(Amazen.getInstance().getListaPersonas());
        tblPersonas.setItems(personasView);
        tblPersonas.setPlaceholder(new Label("No hay personas para mostrar"));

        // Estado base de secciones
        setDisponibilidadSection(false, null);
        setZonaVisible(false);

        // Listener de selección
        tblPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel == null) {
                setCrudEnabled(true);
                setDisponibilidadSection(false, null);
                setZonaVisible(false);
                cmbRol.getSelectionModel().clearSelection();
                limpiarFormulario();
                return;
            }

            txtDocumento.setText(nvl(sel.getDocumento()));
            txtNombre.setText(nvl(sel.getNombre()));
            txtApellido.setText(nvl(sel.getApellido()));
            txtEmail.setText(nvl(sel.getEmail()));
            txtTelefono.setText(nvl(sel.getTelefono()));
            txtCelular.setText(nvl(sel.getCelular()));
            txtContrasena.clear();
            txtDireccion.setText(String.join(", ", sel.getDirecciones()));

            if (sel instanceof Administrador) {
                cmbRol.setValue("Administrador");
                setCrudEnabled(true);
                setDisponibilidadSection(false, null);
                setZonaVisible(false);

            } else if (sel instanceof Repartidor rep) {
                cmbRol.setValue("Repartidor");
                setCrudEnabled(true);
                setDisponibilidadSection(true, rep);
                setZonaVisible(true);
                txtZona.setText(nvl(rep.getZonaCobertura()));

            } else { // Usuario
                cmbRol.setValue("Usuario");
                setCrudEnabled(true);
                setDisponibilidadSection(false, null);
                setZonaVisible(false);
            }
        });

        // Roles disponibles
        cmbRol.getItems().setAll("Administrador", "Usuario", "Repartidor");
        cmbRol.valueProperty().addListener((obs, oldV, rol) -> {
            boolean isRepartidor = "Repartidor".equals(rol);
            setZonaVisible(isRepartidor);
            setDisponibilidadSection(isRepartidor, null);
            if (isRepartidor && cmbDisponibilidad != null && cmbDisponibilidad.getItems().isEmpty()) {
                cmbDisponibilidad.getItems().setAll("ACTIVO", "INACTIVO", "EN_RUTA");
                cmbDisponibilidad.setValue("INACTIVO");
            }
        });

        setCrudEnabled(true);
    }

    // ======= CRUD =======

    @FXML
    private void onCrear() {
        try {
            String rol = cmbRol.getValue();
            if (rol == null || rol.isBlank()) throw new IllegalArgumentException("Seleccione un rol para crear.");

            switch (rol) {
                case "Administrador" -> {
                    Administrador creado = controller.crearAdministrador(
                            getTxt(txtNombre),
                            getTxt(txtApellido),
                            getTxt(txtEmail),
                            getTxt(txtTelefono),
                            getDirecciones(txtDireccion),
                            getTxt(txtCelular),
                            getTxt(txtDocumento),
                            getTxt(txtContrasena)
                    );
                    info("Creado", "Administrador: " + creado.getNombre());
                }
                case "Usuario" -> {
                    Usuario creado = controller.crearUsuario(
                            getTxt(txtNombre),
                            getTxt(txtApellido),
                            getTxt(txtEmail),
                            getTxt(txtTelefono),
                            getDirecciones(txtDireccion),
                            getTxt(txtCelular),
                            getTxt(txtDocumento),
                            getTxt(txtContrasena)
                    );
                    info("Creado", "Usuario: " + creado.getNombre());
                }
                case "Repartidor" -> {
                    String zona = getTxt(txtZona);
                    Disponibilidad disp = Disponibilidad.valueOf(cmbDisponibilidad.getValue());
                    Repartidor creado = controller.crearRepartidor(
                            getTxt(txtNombre),
                            getTxt(txtApellido),
                            getTxt(txtEmail),
                            getTxt(txtTelefono),
                            getDirecciones(txtDireccion),
                            getTxt(txtCelular),
                            getTxt(txtDocumento),
                            getTxt(txtContrasena),
                            zona,
                            disp
                    );
                    info("Creado", "Repartidor: " + creado.getNombre());
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
            Persona seleccionado = tblPersonas.getSelectionModel().getSelectedItem();
            if (seleccionado == null) return;
            String doc = getTxt(txtDocumento);

            if (seleccionado instanceof Administrador) {
                controller.actualizarAdministrador(doc,
                        nullIfBlank(txtNombre.getText()),
                        nullIfBlank(txtApellido.getText()),
                        nullIfBlank(txtEmail.getText()),
                        nullIfBlank(txtTelefono.getText()),
                        getDirecciones(txtDireccion),
                        nullIfBlank(txtCelular.getText()),
                        nullIfBlank(txtContrasena.getText())
                );
            } else if (seleccionado instanceof Repartidor) {
                String zona = nullIfBlank(txtZona.getText());
                Disponibilidad disp = cmbDisponibilidad.getValue() != null ?
                        Disponibilidad.valueOf(cmbDisponibilidad.getValue()) : null;
                controller.actualizarRepartidor(doc,
                        nullIfBlank(txtNombre.getText()),
                        nullIfBlank(txtApellido.getText()),
                        nullIfBlank(txtEmail.getText()),
                        nullIfBlank(txtTelefono.getText()),
                        getDirecciones(txtDireccion),
                        nullIfBlank(txtCelular.getText()),
                        nullIfBlank(txtContrasena.getText()),
                        zona,
                        disp
                );
            } else { // Usuario
                controller.actualizarPersona(doc,
                        nullIfBlank(txtNombre.getText()),
                        nullIfBlank(txtApellido.getText()),
                        nullIfBlank(txtEmail.getText()),
                        nullIfBlank(txtTelefono.getText()),
                        getDirecciones(txtDireccion),
                        nullIfBlank(txtCelular.getText()),
                        nullIfBlank(txtContrasena.getText())
                );
            }

            personasView.setAll(Amazen.getInstance().getListaPersonas());
            tblPersonas.refresh();
            txtContrasena.clear();

        } catch (Exception e) {
            error("No se pudo actualizar", e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        limpiarFormulario();
    }

    @FXML
    private void onEliminar() {
        Persona seleccionado = tblPersonas.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;
        controller.eliminarPersona(seleccionado.getDocumento());
        personasView.setAll(Amazen.getInstance().getListaPersonas());
        tblPersonas.refresh();
        limpiarFormulario();
    }

    @FXML
    private void limpiarFormulario() {
        txtDocumento.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtEmail.clear();
        txtTelefono.clear();
        txtCelular.clear();
        txtDireccion.clear();
        txtContrasena.clear();
        txtZona.clear();
        cmbRol.getSelectionModel().clearSelection();
        setDisponibilidadSection(false, null);
        setZonaVisible(false);
    }

    // ======= BOTONES EXTRA DE LA VISTA =======

    // Volver al panel principal (amazen.fxml)
    @FXML
    private void onVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("amazen.fxml"));
            Stage stage = (Stage) botonVolver.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            error("Error al volver", "No se pudo volver al panel principal:\n" + e.getMessage());
        }
    }

    // Abrir gestión de envíos en una nueva ventana
    @FXML
    private void onAbrirEnvios() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("admin_envios.fxml"));
            Stage dialog = new Stage();
            dialog.setTitle("Gestionar envíos");
            dialog.initOwner(tblPersonas.getScene().getWindow());
            dialog.initModality(Modality.NONE);
            dialog.setScene(new Scene(loader.load()));
            dialog.show();
        } catch (IOException e) {
            error("Error al abrir envíos", "No se pudo abrir la ventana de envíos:\n" + e.getMessage());
        }
    }

    // Abrir panel de métricas en una nueva ventana
    @FXML
    private void onAbrirDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("admin_dashboard.fxml"));
            Stage dialog = new Stage();
            dialog.setTitle("Panel de métricas");

            dialog.initOwner(tblPersonas.getScene().getWindow());
            dialog.initModality(Modality.NONE);
            dialog.setScene(new Scene(loader.load()));

            // 🔹 Tamaño recomendado
            dialog.setWidth(1100);
            dialog.setHeight(700);

            // 🔹 Centrar en la pantalla
            dialog.centerOnScreen();

            // Si la quieres directamente maximizada, descomenta:
            // dialog.setMaximized(true);

            dialog.show();

        } catch (IOException e) {
            error("Error al abrir métricas", "No se pudo abrir el panel de métricas:\n" + e.getMessage());
        }
    }

    // Cambiar solo la disponibilidad del repartidor seleccionado
    @FXML
    private void onCambiarDisponibilidad() {
        Persona seleccionado = tblPersonas.getSelectionModel().getSelectedItem();
        if (!(seleccionado instanceof Repartidor rep)) {
            error("Operación no válida", "Seleccione un repartidor para cambiar la disponibilidad.");
            return;
        }
        String valor = cmbDisponibilidad.getValue();
        if (valor == null || valor.isBlank()) {
            error("Sin selección", "Seleccione una disponibilidad.");
            return;
        }

        try {
            Disponibilidad nueva = Disponibilidad.valueOf(valor);
            controller.actualizarRepartidor(
                    rep.getDocumento(),
                    null, null, null, null,
                    null, // direcciones
                    null, // celular
                    null, // contraseña
                    rep.getZonaCobertura(),
                    nueva
            );
            personasView.setAll(Amazen.getInstance().getListaPersonas());
            tblPersonas.refresh();
            info("Disponibilidad actualizada", "Nuevo estado: " + nueva);
        } catch (Exception e) {
            error("No se pudo cambiar disponibilidad", e.getMessage());
        }
    }

    // Botón "Probar Login (doc/clave)" – sin romper la lógica real de login
    @FXML
    private void onLoginTest() {
        String doc = txtDocumento.getText();
        String clave = txtContrasena.getText();

        if (doc == null || doc.isBlank() || clave == null || clave.isBlank()) {
            error("Prueba de login", "Ingrese documento y contraseña para probar.");
            return;
        }

        try {
            boolean ok = loginController.iniciarSesion(doc, clave);

            if (ok) {
                Persona p = loginController.getUsuarioActivo();
                String rol = getCargo(p);

                info("Login correcto",
                        "✅ Credenciales válidas\n\n" +
                                "Documento: " + doc +
                                "\nNombre: " + nvl(p.getNombre()) + " " + nvl(p.getApellido()) +
                                "\nRol detectado: " + rol);
            } else {
                error("Login incorrecto",
                        "❌ Documento o contraseña no válidos.\n\n" +
                                "Verifique que el documento y la contraseña coincidan con los registros.");
            }
        } catch (Exception e) {
            error("Error al probar login", "Ocurrió un error al intentar autenticar:\n" + e.getMessage());
        }
    }


    // ======= Helpers =======

    private List<String> getDirecciones(TextField tf) {
        String v = tf.getText() == null ? "" : tf.getText().trim();
        if (v.isBlank()) return new ArrayList<>();
        return Arrays.stream(v.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
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
        if (p instanceof Repartidor) return "Repartidor";
        if (p instanceof Usuario) return "Usuario";
        return "Desconocido";
    }

    private void info(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void error(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void setCrudEnabled(boolean enabled) {
        btnCrear.setDisable(!enabled);
        btnActualizar.setDisable(!enabled);
        btnEliminar.setDisable(!enabled);
    }

    private void setZonaVisible(boolean visible) {
        lblZona.setVisible(visible);
        lblZona.setManaged(visible);
        txtZona.setVisible(visible);
        txtZona.setManaged(visible);
    }

    private void setDisponibilidadSection(boolean visible, Repartidor rep) {
        hbxDisponibilidad.setVisible(visible);
        hbxDisponibilidad.setManaged(visible);
        if (visible) {
            if (cmbDisponibilidad.getItems().isEmpty()) {
                cmbDisponibilidad.getItems().setAll("ACTIVO", "INACTIVO", "EN_RUTA");
            }
            if (rep != null && rep.getDisponibilidad() != null) {
                cmbDisponibilidad.setValue(rep.getDisponibilidad().name());
            } else if (cmbDisponibilidad.getValue() == null) {
                cmbDisponibilidad.setValue("INACTIVO");
            }
        }
    }
}
