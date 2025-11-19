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

/**
 * Controlador JavaFX para la vista de administración de personas.
 * <p>
 * Desde este panel el administrador puede:
 * <ul>
 *     <li>Crear, actualizar y eliminar {@link Administrador}, {@link Usuario} y {@link Repartidor}.</li>
 *     <li>Gestionar la disponibilidad de los repartidores.</li>
 *     <li>Probar el inicio de sesión con documento/contraseña para validar credenciales.</li>
 *     <li>Abrir el panel de gestión de envíos y el panel de métricas (dashboard).</li>
 * </ul>
 * Los datos se obtienen y persisten a través del modelo principal {@link Amazen}
 * y del controlador de dominio {@link AdministradorController}.
 */
public class AdministradorViewController {

    // ======= Campos de formulario =======

    /** Combo para seleccionar el rol de la persona a crear/editar (Administrador, Usuario, Repartidor). */
    @FXML private ComboBox<String> cmbRol;
    /** Documento de la persona. Actúa como identificador principal. */
    @FXML private TextField txtDocumento;
    /** Nombre de la persona. */
    @FXML private TextField txtNombre;
    /** Apellido de la persona. */
    @FXML private TextField txtApellido;
    /** Correo electrónico de contacto. */
    @FXML private TextField txtEmail;
    /** Teléfono fijo de contacto. */
    @FXML private TextField txtTelefono;
    /** Direcciones (una o varias, separadas por comas). */
    @FXML private TextField txtDireccion; // múltiples direcciones separadas por coma
    /** Número de celular de contacto. */
    @FXML private TextField txtCelular;
    /** Contraseña para autenticación en el sistema. */
    @FXML private PasswordField txtContrasena;

    // Solo Repartidor

    /** Etiqueta "Zona" (visible solo cuando el rol es Repartidor). */
    @FXML private Label lblZona;
    /** Zona de cobertura del repartidor. */
    @FXML private TextField txtZona;

    // Sección Disponibilidad (Repartidor)

    /** Contenedor HBox de la sección de disponibilidad (solo repartidores). */
    @FXML private HBox hbxDisponibilidad;
    /** Combo para seleccionar la disponibilidad de un repartidor. */
    @FXML private ComboBox<String> cmbDisponibilidad;
    /** Botón para aplicar un cambio de disponibilidad a un repartidor seleccionado. */
    @FXML private Button btnCambiarDisponibilidad;

    // Tabla y columnas

    /** Tabla que lista todas las personas registradas en el sistema. */
    @FXML private TableView<Persona> tblPersonas;
    /** Columna: documento de la persona. */
    @FXML private TableColumn<Persona, String> colDocumento;
    /** Columna: nombre de la persona. */
    @FXML private TableColumn<Persona, String> colNombre;
    /** Columna: apellido de la persona. */
    @FXML private TableColumn<Persona, String> colApellido;
    /** Columna: correo electrónico de la persona. */
    @FXML private TableColumn<Persona, String> colEmail;
    /** Columna: teléfono fijo de la persona. */
    @FXML private TableColumn<Persona, String> colTelefono;
    /** Columna: celular de la persona. */
    @FXML private TableColumn<Persona, String> colCelular;
    /** Columna: direcciones de la persona en formato de texto. */
    @FXML private TableColumn<Persona, String> colDireccion;
    /** Columna: cargo/rol de la persona (Administrador, Usuario, Repartidor). */
    @FXML private TableColumn<Persona, String> colCargo;
    /** Columna: disponibilidad actual (solo aplica a repartidores). */
    @FXML private TableColumn<Persona, String> colDisponibilidad;

    // Botones CRUD

    /** Botón para crear una nueva persona. */
    @FXML private Button btnCrear;
    /** Botón para actualizar los datos de la persona seleccionada. */
    @FXML private Button btnActualizar;
    /** Botón para eliminar la persona seleccionada. */
    @FXML private Button btnEliminar;
    /** Botón para limpiar el formulario. */
    @FXML private Button btnLimpiar;

    // Botones extra de la vista

    /** Botón para probar el login con documento/contraseña. */
    @FXML private Button btnLoginTest;
    /** Botón para volver al panel principal de la aplicación. */
    @FXML private Button botonVolver;

    /** Controlador de dominio específico para las operaciones de administración. */
    private final AdministradorController controller = new AdministradorController();
    /** Controlador de login, usado en la prueba de inicio de sesión. */
    private final LoginController loginController = new LoginController();
    /** Lista observable que respalda la tabla de personas. */
    private final ObservableList<Persona> personasView = FXCollections.observableArrayList();


    /**
     * Inicializa la vista una vez cargado el FXML.
     * <p>
     * Configura las columnas de la tabla, inicializa el singleton {@link Amazen},
     * carga las personas existentes, configura los roles disponibles y
     * gestiona el comportamiento de selección de filas.
     */
    @FXML
    public void initialize() {
        // Inicializar singleton
        Amazen.getInstance();

        // Configurar columnas de la tabla
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

        // Poblar tabla con las personas del modelo
        personasView.setAll(Amazen.getInstance().getListaPersonas());
        tblPersonas.setItems(personasView);
        tblPersonas.setPlaceholder(new Label("No hay personas para mostrar"));

        // Estado base de secciones (zona/disponibilidad ocultas)
        setDisponibilidadSection(false, null);
        setZonaVisible(false);

        // Listener de selección de filas en la tabla
        tblPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel == null) {
                setCrudEnabled(true);
                setDisponibilidadSection(false, null);
                setZonaVisible(false);
                cmbRol.getSelectionModel().clearSelection();
                limpiarFormulario();
                return;
            }

            // Cargar datos de la persona seleccionada en el formulario
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

        // Roles disponibles en el combo
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

    /**
     * Acción del botón "Crear".
     * <p>
     * Según el rol seleccionado en {@link #cmbRol}, crea un nuevo Administrador,
     * Usuario o Repartidor usando el {@link AdministradorController}. Valida
     * los campos requeridos y actualiza la tabla tras la creación.
     */
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

            // Refrescar tabla y limpiar formulario
            personasView.setAll(Amazen.getInstance().getListaPersonas());
            tblPersonas.refresh();
            limpiarFormulario();

        } catch (Exception e) {
            error("No se pudo crear", e.getMessage());
        }
    }

    /**
     * Acción del botón "Actualizar".
     * <p>
     * Actualiza parcialmente los datos de la persona seleccionada. Sólo se envían
     * al controlador los campos que no están en blanco (nullIfBlank).
     */
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

    /**
     * Acción del botón "Limpiar".
     * <p>
     * Limpia todos los campos del formulario y devuelve la vista al estado inicial.
     */
    @FXML
    private void onLimpiar() {
        limpiarFormulario();
    }

    /**
     * Acción del botón "Eliminar".
     * <p>
     * Elimina la persona actualmente seleccionada en la tabla usando el
     * {@link AdministradorController}, refrescando luego la vista.
     */
    @FXML
    private void onEliminar() {
        Persona seleccionado = tblPersonas.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;
        controller.eliminarPersona(seleccionado.getDocumento());
        personasView.setAll(Amazen.getInstance().getListaPersonas());
        tblPersonas.refresh();
        limpiarFormulario();
    }

    /**
     * Limpia los campos del formulario y oculta secciones específicas de repartidor.
     */
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

    /**
     * Vuelve al panel principal (amazen.fxml) en la misma ventana.
     */
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

    /**
     * Abre la ventana de gestión de envíos (admin_envios.fxml) en un nuevo {@link Stage}.
     */
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

    /**
     * Abre el panel de métricas (admin_dashboard.fxml) en una nueva ventana.
     * <p>
     * Configura un tamaño recomendado para visualizar adecuadamente las gráficas
     * y centra la ventana en pantalla.
     */
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

    /**
     * Cambia únicamente la disponibilidad del repartidor seleccionado en la tabla,
     * utilizando el valor actual del combo {@link #cmbDisponibilidad}.
     */
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

    /**
     * Prueba el inicio de sesión usando el documento y la contraseña ingresados
     * en el formulario, sin alterar el flujo normal de login de la aplicación.
     * <p>
     * Útil para que el administrador verifique si las credenciales almacenadas
     * para una persona son correctas.
     */
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

    /**
     * Convierte el contenido del {@link TextField} de direcciones en una lista
     * de cadenas, separadas por comas.
     *
     * @param tf campo de texto con direcciones separadas por comas
     * @return lista de direcciones limpias (sin espacios sobrantes)
     */
    private List<String> getDirecciones(TextField tf) {
        String v = tf.getText() == null ? "" : tf.getText().trim();
        if (v.isBlank()) return new ArrayList<>();
        return Arrays.stream(v.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /**
     * Obtiene el texto de un {@link TextField} y lanza una excepción si está vacío.
     *
     * @param t campo de texto
     * @return contenido sin espacios al inicio/final
     * @throws IllegalArgumentException si el campo está vacío
     */
    private static String getTxt(TextField t) {
        String v = t.getText() == null ? "" : t.getText().trim();
        if (v.isBlank()) throw new IllegalArgumentException("Campo requerido: " + t.getPromptText());
        return v;
    }

    /**
     * Devuelve {@code null} si la cadena es nula o está en blanco; de lo contrario la cadena recortada.
     *
     * @param s cadena a evaluar
     * @return {@code null} o cadena recortada
     */
    private static String nullIfBlank(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isBlank() ? null : t;
    }

    /**
     * Devuelve una cadena vacía si el valor es {@code null}, o el valor original en caso contrario.
     */
    private static String nvl(String s) { return s == null ? "" : s; }

    /**
     * Determina el rol/cargo de una {@link Persona} según su tipo concreto.
     *
     * @param p persona
     * @return "Administrador", "Repartidor", "Usuario" o "Desconocido"
     */
    private String getCargo(Persona p) {
        if (p instanceof Administrador) return "Administrador";
        if (p instanceof Repartidor) return "Repartidor";
        if (p instanceof Usuario) return "Usuario";
        return "Desconocido";
    }

    /**
     * Muestra un cuadro de diálogo de información.
     *
     * @param titulo   título de la ventana de diálogo
     * @param mensaje  mensaje a mostrar
     */
    private void info(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /**
     * Muestra un cuadro de diálogo de error.
     *
     * @param titulo   título de la ventana de diálogo
     * @param mensaje  mensaje a mostrar
     */
    private void error(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Habilita o deshabilita los botones CRUD (crear, actualizar, eliminar).
     *
     * @param enabled {@code true} para habilitar, {@code false} para deshabilitar
     */
    private void setCrudEnabled(boolean enabled) {
        btnCrear.setDisable(!enabled);
        btnActualizar.setDisable(!enabled);
        btnEliminar.setDisable(!enabled);
    }

    /**
     * Muestra u oculta los campos relacionados con la zona de cobertura del repartidor.
     *
     * @param visible {@code true} para mostrar, {@code false} para ocultar
     */
    private void setZonaVisible(boolean visible) {
        lblZona.setVisible(visible);
        lblZona.setManaged(visible);
        txtZona.setVisible(visible);
        txtZona.setManaged(visible);
    }

    /**
     * Muestra u oculta la sección de disponibilidad para repartidores,
     * y opcionalmente carga la disponibilidad actual de un repartidor concreto.
     *
     * @param visible indica si la sección debe estar visible
     * @param rep     repartidor del cual se debe cargar la disponibilidad (puede ser {@code null})
     */
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
