package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.Pedido;
import co.edu.uniquindio.poo.amazen.Model.Persona.Repartidor;
import co.edu.uniquindio.poo.amazen.Model.DTO.DtoMapper;
import co.edu.uniquindio.poo.amazen.Service.ExportCsvService;
import co.edu.uniquindio.poo.amazen.Model.ExportarArchivo;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para la vista de administración de envíos.
 * <p>
 * Permite al administrador:
 * <ul>
 *     <li>Listar los pedidos registrados en el sistema.</li>
 *     <li>Asignar o reasignar pedidos a repartidores.</li>
 *     <li>Cambiar el estado del pedido (empaquetado, enviado, entregado).</li>
 *     <li>Registrar incidencias asociadas a un pedido.</li>
 *     <li>Exportar información de pedidos a TXT o CSV.</li>
 * </ul>
 * <p>
 * Los datos se obtienen desde el singleton {@link Amazen} y se muestran en una tabla.
 */
public class AdminEnviosViewController {

    // -------------------------------------------------------------------------
    // UI: Toolbar
    // -------------------------------------------------------------------------

    /** Botón para refrescar la información mostrada. */
    @FXML private Button btnRefrescar;
    /** ComboBox con la lista de repartidores disponibles para asignar pedidos. */
    @FXML private ComboBox<Repartidor> cmbRepartidores;

    // -------------------------------------------------------------------------
    // UI: Tabla de pedidos
    // -------------------------------------------------------------------------

    /** Tabla principal donde se listan los pedidos. */
    @FXML private TableView<Pedido> tblPedidos;
    /** Columna que muestra el identificador del pedido. */
    @FXML private TableColumn<Pedido, String> colId;
    /** Columna que muestra el estado actual del pedido. */
    @FXML private TableColumn<Pedido, String> colEstado;
    /** Columna que muestra el documento del repartidor asignado. */
    @FXML private TableColumn<Pedido, String> colRepartidor;
    /** Columna que muestra el total del pedido. */
    @FXML private TableColumn<Pedido, Number> colTotal;
    /** Columna con la fecha de creación del pedido. */
    @FXML private TableColumn<Pedido, String> colCreacion;
    /** Columna con la fecha de asignación del pedido. */
    @FXML private TableColumn<Pedido, String> colAsignacion;
    /** Columna con la fecha de entrega del pedido. */
    @FXML private TableColumn<Pedido, String> colEntrega;
    /** Columna con el número de incidencias asociadas al pedido. */
    @FXML private TableColumn<Pedido, Number> colIncidencias;

    // -------------------------------------------------------------------------
    // UI: Zona inferior
    // -------------------------------------------------------------------------

    /** Etiqueta de información y mensajes de estado para el usuario. */
    @FXML private Label lblInfo;

    /** Formato de fecha y hora usado en la tabla. */
    private final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Inicializa la vista cuando se carga el FXML.
     * <p>
     * Configura las columnas de la tabla, carga la lista de repartidores y los pedidos,
     * y establece mensajes de UX básicos.
     */
    @FXML
    public void initialize() {
        // ==== columnas ====
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colEstado.setCellValueFactory(c -> {
            var est = c.getValue().getEstado();
            return new SimpleStringProperty(est == null ? "—" : est.toString());
        });

        colRepartidor.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDocumentoRepartidorAsignado() == null
                                ? "—"
                                : c.getValue().getDocumentoRepartidorAsignado()
                )
        );

        colTotal.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().calcularTotal()));

        colCreacion.setCellValueFactory(c -> new SimpleStringProperty(format(c.getValue().getFechaCreacion())));
        colAsignacion.setCellValueFactory(c -> new SimpleStringProperty(format(c.getValue().getFechaAsignacion())));
        colEntrega.setCellValueFactory(c -> new SimpleStringProperty(format(c.getValue().getFechaEntrega())));

        colIncidencias.setCellValueFactory(c -> {
            int n = 0;
            try {
                n = c.getValue().getIncidencias() == null ? 0 : c.getValue().getIncidencias().size();
            } catch (Throwable ignore) {
            }
            return new ReadOnlyObjectWrapper<>(n);
        });

        // ==== datos ====
        cargarRepartidores();
        cargarPedidos();

        // UX
        tblPedidos.setPlaceholder(new Label("No hay pedidos para mostrar"));
        setInfo("Listo.");
    }

    // -------------------------------------------------------------------------
    // Acciones Toolbar
    // -------------------------------------------------------------------------

    /**
     * Acción del botón "Refrescar".
     * <p>
     * Vuelve a cargar la lista de repartidores y la tabla de pedidos,
     * actualizando también el mensaje inferior.
     */
    @FXML
    private void onRefrescar() {
        cargarRepartidores();
        cargarPedidos();
        setInfo("Refrescado.");
    }

    /**
     * Asigna el pedido seleccionado al repartidor escogido en el combo.
     * <p>
     * Llama al método {@code asignarRepartidor()} del pedido. Usa el patrón State
     * ya definido en el modelo para validar la operación.
     */
    @FXML
    private void onAsignar() {
        var sel = seleccionado();
        if (sel == null) return;
        var rep = cmbRepartidores.getValue();
        if (rep == null) {
            warn("Selecciona un repartidor.");
            return;
        }
        try {
            sel.asignarRepartidor(rep.getDocumento());
            tblPedidos.refresh();
            setInfo("Asignado repartidor " + rep.getDocumento() + " a " + sel.getId());
        } catch (Exception e) {
            error("No se pudo asignar: " + e.getMessage());
        }
    }

    /**
     * Reasigna el pedido seleccionado al repartidor actualmente escogido.
     * <p>
     * Para simplificar, reutiliza la misma lógica de {@link #onAsignar()}.
     */
    @FXML
    private void onReasignar() {
        onAsignar();
    }

    /**
     * Marca el pedido seleccionado como "EMPAQUETADO".
     * <p>
     * Invoca {@link Pedido#empaquetar()} que internamente usa el patrón State.
     */
    @FXML
    private void onEstadoEmpaquetado() {
        var sel = seleccionado();
        if (sel == null) return;
        try {
            sel.empaquetar(); // usa tu State
            tblPedidos.refresh();
            setInfo("Pedido " + sel.getId() + " → Empaquetado");
        } catch (Exception e) {
            error("No se pudo cambiar estado: " + e.getMessage());
        }
    }

    /**
     * Marca el pedido seleccionado como "ENVIADO".
     */
    @FXML
    private void onEstadoEnviado() {
        var sel = seleccionado();
        if (sel == null) return;
        try {
            sel.enviar();
            tblPedidos.refresh();
            setInfo("Pedido " + sel.getId() + " → Enviado");
        } catch (Exception e) {
            error("No se pudo cambiar estado: " + e.getMessage());
        }
    }

    /**
     * Marca el pedido seleccionado como "ENTREGADO".
     */
    @FXML
    private void onEstadoEntregado() {
        var sel = seleccionado();
        if (sel == null) return;
        try {
            sel.entregar();
            tblPedidos.refresh();
            setInfo("Pedido " + sel.getId() + " → Entregado");
        } catch (Exception e) {
            error("No se pudo cambiar estado: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Exportaciones
    // -------------------------------------------------------------------------

    /**
     * Exporta la información detallada del pedido seleccionado a un archivo TXT.
     * <p>
     * Utiliza la clase de utilidades {@link ExportarArchivo}.
     */
    @FXML
    private void onExportarTxt() {
        var sel = seleccionado();
        if (sel == null) return;
        try {
            Path out = Path.of("reportes", "pedido_" + sel.getId() + ".txt");
            Files.createDirectories(out.getParent());
            ExportarArchivo.exportarPedido(sel, out.toString());
            info("TXT generado en:\n" + out.toAbsolutePath());
        } catch (Exception e) {
            error("Exportación TXT: " + e.getMessage());
        }
    }

    /**
     * Exporta todos los pedidos mostrados en la tabla a un archivo CSV.
     * <p>
     * Mapea cada {@link Pedido} a su DTO correspondiente usando {@link DtoMapper#toDTO(Pedido)}
     * y luego llama a {@link ExportCsvService#exportPedidos(Path, List)}.
     */
    @FXML
    private void onExportarCsv() {
        try {
            var pedidos = tblPedidos.getItems();
            var dtos = pedidos.stream()
                    .map((Pedido p) -> DtoMapper.toDTO(p))  // evita ambigüedad con DetallePedido
                    .toList();

            Path out = Path.of("reportes", "pedidos.csv");
            Files.createDirectories(out.getParent());
            ExportCsvService.exportPedidos(out, dtos);
            info("CSV generado en:\n" + out.toAbsolutePath());
        } catch (Exception e) {
            error("Exportación CSV: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Incidencias
    // -------------------------------------------------------------------------

    /**
     * Registra una incidencia de texto libre asociada al pedido seleccionado.
     * <p>
     * Crea una instancia de {@code Incidencia} mediante reflexión,
     * intentando primero el constructor (String, LocalDateTime)
     * y luego un constructor (String) como plan de respaldo.
     * Después invoca {@code registrarIncidencia(Incidencia)} sobre el pedido.
     */
    @FXML
    private void onIncidencia() {
        var sel = seleccionado();
        if (sel == null) return;

        TextInputDialog d = new TextInputDialog();
        d.setHeaderText("Registrar incidencia");
        d.setContentText("Describe la incidencia:");
        var txt = d.showAndWait().orElse(null);
        if (txt == null || txt.isBlank()) return;

        try {
            // Intentamos construir co.edu.uniquindio.poo.amazen.Model.Incidencia(String, LocalDateTime)
            // Si tu clase Incidencia tiene otra firma, ajústala o captura y notifica.
            Object inc;
            try {
                var clazz = Class.forName("co.edu.uniquindio.poo.amazen.Model.Incidencia");
                try {
                    var ctor = clazz.getConstructor(String.class, LocalDateTime.class);
                    inc = ctor.newInstance(txt.trim(), LocalDateTime.now());
                } catch (NoSuchMethodException ex) {
                    var ctor2 = clazz.getConstructor(String.class);
                    inc = ctor2.newInstance(txt.trim());
                }
            } catch (Exception reflect) {
                error("No se pudo crear la incidencia. Ajusta el constructor de Incidencia (String [, LocalDateTime]).");
                return;
            }

            // Llama a registrarIncidencia(Incidencia)
            sel.registrarIncidencia((co.edu.uniquindio.poo.amazen.Model.Incidencia) inc);
            tblPedidos.refresh();
            setInfo("Incidencia registrada para " + sel.getId());
        } catch (Exception e) {
            error("No se pudo registrar la incidencia: " + e.getMessage());
        }
    }

    /**
     * Cierra la ventana actual del panel de envíos.
     */
    @FXML
    private void onCerrar() {
        Stage st = (Stage) ((Node) lblInfo).getScene().getWindow();
        st.close();
    }

    // -------------------------------------------------------------------------
    // Utilidades internas
    // -------------------------------------------------------------------------

    /**
     * Carga la lista de pedidos desde {@link Amazen} y la muestra en la tabla.
     * <p>
     * Los pedidos se ordenan de forma descendente por fecha de creación
     * (los más recientes primero).
     */
    private void cargarPedidos() {
        List<Pedido> pedidos;
        try {
            pedidos = Amazen.getInstance().getListaPedidos();
        } catch (Throwable t) {
            pedidos = List.of();
        }
        // orden opcional: recientes primero
        pedidos = pedidos.stream()
                .sorted(Comparator.comparing(Pedido::getFechaCreacion,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());

        tblPedidos.setItems(FXCollections.observableArrayList(pedidos));
        tblPedidos.refresh();
    }

    /**
     * Carga los repartidores registrados en {@link Amazen} y los muestra en el combo.
     * <p>
     * Define además un cell factory para mostrar cada repartidor con el formato
     * "Nombre Apellido (documento)".
     */
    private void cargarRepartidores() {
        try {
            var personas = Amazen.getInstance().getListaPersonas();
            var reps = personas.stream()
                    .filter(p -> p instanceof Repartidor)
                    .map(p -> (Repartidor) p)
                    .sorted(Comparator.comparing(Repartidor::getDocumento))
                    .toList();

            cmbRepartidores.setItems(FXCollections.observableArrayList(reps));
            // cell factory para mostrar Nombre Apellido (doc)
            cmbRepartidores.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Repartidor item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : labelRep(item));
                }
            });
            cmbRepartidores.setCellFactory(list -> new ListCell<>() {
                @Override
                protected void updateItem(Repartidor item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : labelRep(item));
                }
            });
        } catch (Throwable t) {
            cmbRepartidores.setItems(FXCollections.observableArrayList());
        }
    }

    /**
     * Construye la etiqueta legible para un repartidor en el combo.
     *
     * @param r repartidor a formatear
     * @return texto con el formato "Nombre Apellido (documento)"
     */
    private String labelRep(Repartidor r) {
        String nom = r.getNombre() == null ? "" : r.getNombre();
        String ape = r.getApellido() == null ? "" : r.getApellido();
        String doc = r.getDocumento() == null ? "—" : r.getDocumento();
        return (nom + " " + ape + " (" + doc + ")").trim();
    }

    /**
     * Devuelve el pedido actualmente seleccionado en la tabla.
     * <p>
     * Si no hay selección, muestra una advertencia.
     *
     * @return pedido seleccionado o {@code null} si no hay ninguno
     */
    private Pedido seleccionado() {
        var sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) warn("Selecciona un pedido de la tabla.");
        return sel;
    }

    /**
     * Formatea una fecha/hora usando el formato {@link #FMT}.
     *
     * @param dt fecha y hora a formatear
     * @return cadena formateada o cadena vacía si la fecha es {@code null}
     */
    private String format(LocalDateTime dt) {
        return dt == null ? "" : FMT.format(dt);
    }

    /**
     * Actualiza el texto de la etiqueta inferior con el mensaje indicado.
     *
     * @param msg mensaje a mostrar
     */
    private void setInfo(String msg) {
        if (lblInfo != null) lblInfo.setText(msg);
    }

    /**
     * Muestra un cuadro de diálogo informativo.
     *
     * @param msg mensaje a mostrar
     */
    private void info(String msg) {
        alert(Alert.AlertType.INFORMATION, "Información", msg);
    }

    /**
     * Muestra un cuadro de advertencia.
     *
     * @param msg mensaje a mostrar
     */
    private void warn(String msg) {
        alert(Alert.AlertType.WARNING, "Atención", msg);
    }

    /**
     * Muestra un cuadro de error.
     *
     * @param msg mensaje a mostrar
     */
    private void error(String msg) {
        alert(Alert.AlertType.ERROR, "Error", msg);
    }

    /**
     * Crea y muestra una alerta de JavaFX con el tipo, encabezado y mensaje dados.
     *
     * @param t      tipo de alerta
     * @param header texto del encabezado
     * @param msg    contenido del mensaje
     */
    private void alert(Alert.AlertType t, String header, String msg) {
        var a = new Alert(t, msg, ButtonType.OK);
        a.setHeaderText(header);
        a.showAndWait();
    }
}
