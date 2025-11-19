package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Model.HistorialPedido;
import co.edu.uniquindio.poo.amazen.Model.Pedido;
import co.edu.uniquindio.poo.amazen.Model.Persona.Administrador;
import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;
import co.edu.uniquindio.poo.amazen.Model.Persona.Repartidor;
import co.edu.uniquindio.poo.amazen.Model.Persona.SesionUsuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de la vista de historial de pedidos.
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 *     <li>Configurar y poblar la tabla de pedidos históricos.</li>
 *     <li>Filtrar los pedidos visibles según el rol del usuario en sesión:
 *         <ul>
 *             <li><b>Administrador</b> / <b>Repartidor</b>: pueden ver todos los pedidos.</li>
 *             <li><b>Usuario</b> final: solo ve los pedidos asociados a su documento.</li>
 *         </ul>
 *     </li>
 *     <li>Permitir la cancelación de un pedido desde la interfaz, delegando la lógica
 *         al patrón State implementado en {@link Pedido#cancelar()}.</li>
 *     <li>Permitir volver al panel principal (amazen.fxml).</li>
 * </ul>
 *
 * <p>Este controlador se apoya en el singleton {@link HistorialPedido} como fuente
 * centralizada de pedidos registrados.</p>
 */
public class HistorialViewController {

    // ================== FXML: TABLA Y COLUMNAS ==================

    /** Tabla que muestra los pedidos del historial. */
    @FXML private TableView<Pedido> tablaPedidos;

    /** Columna que muestra el identificador único del pedido. */
    @FXML private TableColumn<Pedido, String> colId;

    /** Columna que muestra el estado actual del pedido (EstadoPedido.toString()). */
    @FXML private TableColumn<Pedido, String> colEstado;

    /** Columna que muestra el total a pagar del pedido (formateado sin decimales). */
    @FXML private TableColumn<Pedido, String> colTotal;

    /** Columna que muestra la fecha y hora de creación del pedido. */
    @FXML private TableColumn<Pedido, String> colCreacion;

    /** Columna que muestra la fecha y hora de asignación de repartidor (si aplica). */
    @FXML private TableColumn<Pedido, String> colAsignacion;

    /** Columna que muestra la fecha y hora de entrega al cliente (si aplica). */
    @FXML private TableColumn<Pedido, String> colEntrega;

    /** Columna que muestra el documento del repartidor asignado (si aplica). */
    @FXML private TableColumn<Pedido, String> colRepartidor;

    // ================== FXML: CONTROLES DE ACCIÓN ==================

    /** Botón para volver al panel principal. */
    @FXML private Button btnVolver;

    /** Botón para solicitar cancelación del pedido seleccionado. */
    @FXML private Button btnCancelar;

    /** Etiqueta que muestra información breve del usuario en sesión. */
    @FXML private Label  lblUsuario;

    /** Etiqueta que muestra un resumen de la cantidad de pedidos listados. */
    @FXML private Label  lblResumen;

    // ================== MODELO Y FORMATOS ==================

    /** Fuente de datos de historial (singleton compartido). */
    private final HistorialPedido historial = HistorialPedido.getInstance();

    /** Lista observable que alimenta la tabla de pedidos. */
    private final ObservableList<Pedido> datos = FXCollections.observableArrayList();

    /** Formato de fecha y hora usado para mostrar marcas temporales. */
    private final DateTimeFormatter fmtFechaHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ================== CICLO DE VIDA DE LA VISTA ==================

    /**
     * Inicializa la vista de historial.
     *
     * <p>Se encarga de:</p>
     * <ul>
     *     <li>Configurar las columnas de la tabla y sus {@code cellValueFactory}.</li>
     *     <li>Asignar la lista observable {@link #datos} como items de la tabla.</li>
     *     <li>Cargar los pedidos visibles para el usuario actual mediante
     *         {@link #cargarPedidosUsuarioActual()}.</li>
     * </ul>
     */
    @FXML
    private void initialize() {
        // Configurar columnas
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));

        colEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstado() == null ? "—" : c.getValue().getEstado().toString()
        ));

        colTotal.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("%.0f", c.getValue().calcularTotal())
        ));

        colCreacion.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaCreacion() == null
                        ? "—"
                        : c.getValue().getFechaCreacion().format(fmtFechaHora)
        ));

        colAsignacion.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaAsignacion() == null
                        ? "—"
                        : c.getValue().getFechaAsignacion().format(fmtFechaHora)
        ));

        colEntrega.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaEntrega() == null
                        ? "—"
                        : c.getValue().getFechaEntrega().format(fmtFechaHora)
        ));

        colRepartidor.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDocumentoRepartidorAsignado() == null
                        ? "—"
                        : c.getValue().getDocumentoRepartidorAsignado()
        ));

        tablaPedidos.setItems(datos);

        cargarPedidosUsuarioActual();
    }

    // ================== CARGA / FILTRO DE PEDIDOS ==================

    /**
     * Carga en la tabla los pedidos visibles para la persona actualmente en sesión.
     *
     * <p>Lógica de filtrado:</p>
     * <ul>
     *     <li>Si no hay sesión activa: no se muestran pedidos y se deshabilita el botón cancelar.</li>
     *     <li>Si el usuario es {@link Administrador} o {@link Repartidor}:
     *         se cargan todos los pedidos.</li>
     *     <li>Si es un {@link co.edu.uniquindio.poo.amazen.Model.Persona.Usuario Usuario}:
     *         solo se cargan aquellos cuyo documento de cliente coincide con el suyo.</li>
     * </ul>
     */
    private void cargarPedidosUsuarioActual() {
        Persona persona = SesionUsuario.instancia().getPersona();

        if (persona == null) {
            lblUsuario.setText("Usuario: (sin sesión)");
            datos.clear();
            lblResumen.setText("No hay sesión activa.");
            btnCancelar.setDisable(true);
            return;
        }

        lblUsuario.setText("Usuario: " + persona.getNombre() + " (" + persona.getDocumento() + ")");

        List<Pedido> todos = historial.getPedidos();
        List<Pedido> filtrados;

        // Admin o repartidor ven todo; usuario ve solo sus pedidos
        if (persona instanceof Administrador || persona instanceof Repartidor) {
            filtrados = todos;
        } else {
            filtrados = todos.stream()
                    .filter(p -> persona.getDocumento().equalsIgnoreCase(p.getDocumentoCliente()))
                    .collect(Collectors.toList());
        }

        datos.setAll(filtrados);
        lblResumen.setText(filtrados.size() + " pedido(s) encontrado(s).");
        btnCancelar.setDisable(datos.isEmpty());
    }

    // ================== CANCELAR PEDIDO ==================

    /**
     * Devuelve el pedido actualmente seleccionado en la tabla.
     *
     * <p>Si no hay selección, muestra una advertencia y retorna {@code null}.</p>
     *
     * @return pedido seleccionado o {@code null} si no se seleccionó ninguno.
     */
    private Pedido pedidoSeleccionado() {
        Pedido p = tablaPedidos.getSelectionModel().getSelectedItem();
        if (p == null) {
            alerta(Alert.AlertType.WARNING, "Selección requerida",
                    "Debes seleccionar un pedido de la tabla.");
        }
        return p;
    }

    /**
     * Acción del botón "Cancelar pedido".
     *
     * <p>Pasos:</p>
     * <ol>
     *     <li>Obtiene el pedido mediante {@link #pedidoSeleccionado()}.</li>
     *     <li>Muestra un cuadro de confirmación al usuario.</li>
     *     <li>Si confirma, invoca {@link Pedido#cancelar()} para delegar en el patrón State.</li>
     *     <li>Refresca la tabla y actualiza el resumen.</li>
     *     <li>Muestra un mensaje de éxito o error según corresponda.</li>
     * </ol>
     *
     * <p>La lógica de validación (si es posible cancelar o no) reside en
     * la implementación del estado del {@link Pedido}.</p>
     */
    @FXML
    private void onCancelarPedido() {
        Pedido pedido = pedidoSeleccionado();
        if (pedido == null) return;

        // Confirmación
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Seguro que deseas cancelar el pedido " + pedido.getId() + "?\n" +
                        "Solo se puede cancelar si aún no ha sido empaquetado/enviado.",
                ButtonType.OK, ButtonType.CANCEL);
        conf.setHeaderText("Confirmar cancelación");
        if (conf.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            // Usa el patrón State (EstadoPagado / EstadoVerificarPago, etc.)
            pedido.cancelar();

            tablaPedidos.refresh();
            lblResumen.setText(datos.size() + " pedido(s) encontrado(s).");

            alerta(Alert.AlertType.INFORMATION, "Pedido cancelado",
                    "El pedido " + pedido.getId() + " ha sido cancelado correctamente.");
        } catch (Exception e) {
            alerta(Alert.AlertType.ERROR, "No se pudo cancelar",
                    "No fue posible cancelar el pedido:\n" + e.getMessage());
        }
    }

    // ================== NAVEGACIÓN: VOLVER ==================

    /**
     * Acción del botón "Volver".
     *
     * <p>Cambia la escena actual para volver al panel principal de la aplicación
     * (amazen.fxml), reutilizando la ventana actual ({@link Stage}).</p>
     */
    @FXML
    private void onVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("amazen.fxml"));
            AnchorPane root = loader.load();
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            alerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo volver al panel principal.");
        }
    }

    // ================== UTILIDADES: ALERTAS ==================

    /**
     * Muestra un cuadro de diálogo simple (información, advertencia o error).
     *
     * @param tipo    tipo de alerta (INFORMATION, WARNING, ERROR, etc.).
     * @param titulo  título de la ventana de diálogo.
     * @param mensaje contenido principal a mostrar al usuario.
     */
    private void alerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo, mensaje, ButtonType.OK);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
