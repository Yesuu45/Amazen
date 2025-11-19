package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Controller.GestorEstadosController;
import co.edu.uniquindio.poo.amazen.Model.Pedido;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador de la vista de estados del pedido.
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 *   <li>Mostrar en una tabla los pedidos y su estado actual.</li>
 *   <li>Permitir al usuario cambiar el estado del pedido usando botones dedicados:
 *       verificación de pago, empaquetado, enviado y entregado.</li>
 *   <li>Comunicar la acción al {@link GestorEstadosController}, que encapsula
 *       la lógica de negocio y el patrón State de {@link Pedido}.</li>
 *   <li>Notificar al usuario los resultados (éxito o error) mediante cuadros de diálogo.</li>
 * </ul>
 *
 * <p>Patrones utilizados:</p>
 * <ul>
 *   <li><b>State</b>: Cada {@link Pedido} tiene un estado que define qué
 *       acciones son válidas. Este controlador no implementa State, sino que
 *       delega en {@link GestorEstadosController} la transición entre estados.</li>
 * </ul>
 */
public class EstadoViewController {

    /** Botón para volver al panel principal (amazen.fxml). */
    @FXML
    private Button botonVolver;
    /** Botón para llevar el pedido a estado de verificación de pago. */
    @FXML private Button botonVerificarPago;
    /** Botón para marcar el pedido como empaquetado. */
    @FXML private Button botonEmpaquetado;
    /** Botón para marcar el pedido como enviado. */
    @FXML private Button botonEnviado;
    /** Botón para marcar el pedido como entregado. */
    @FXML private Button botonEntregado;

    /** Tabla que muestra los pedidos sobre los que se puede operar. */
    @FXML private TableView<Pedido> tablaPedidos;
    /** Columna con el identificador del pedido. */
    @FXML private TableColumn<Pedido, String> columnaId;
    /** Columna con el estado actual del pedido (toString() del estado). */
    @FXML private TableColumn<Pedido, String> columnaEstado;
    /** Columna con el total calculado del pedido. */
    @FXML private TableColumn<Pedido, Double> columnaTotal;

    /** Lista observable que respalda la tabla de pedidos. */
    private ObservableList<Pedido> datos;

    /** Controlador de negocio responsable de gestionar los cambios de estado. */
    private GestorEstadosController gestorEstadosController;

    /**
     * Inicializa la vista de estados.
     *
     * <p>Se ejecuta automáticamente al cargar el FXML y realiza:</p>
     * <ul>
     *   <li>Instancia el {@link GestorEstadosController}.</li>
     *   <li>Configura las columnas de la tabla para mostrar id, estado y total.</li>
     *   <li>Carga la lista de pedidos desde el gestor y la asocia a la tabla.</li>
     * </ul>
     */
    @FXML
    private void initialize() {
        gestorEstadosController = new GestorEstadosController();

        // Configura las columnas de la tabla
        columnaId.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getId()));
        columnaEstado.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getEstado().toString())
        );
        columnaTotal.setCellValueFactory(param ->
                new SimpleDoubleProperty(param.getValue().calcularTotal()).asObject()
        );

        // Carga inicial de pedidos
        datos = FXCollections.observableArrayList(gestorEstadosController.obtenerPedidos());
        tablaPedidos.setItems(datos);
    }

    /**
     * Obtiene el pedido actualmente seleccionado en la tabla.
     *
     * @return pedido seleccionado o {@code null} si no hay selección. En ese caso
     *         también muestra una alerta informando al usuario.
     */
    private Pedido getPedidoSeleccionado() {
        Pedido pedido = tablaPedidos.getSelectionModel().getSelectedItem();
        if (pedido == null) {
            mostrarAlerta("Selecciona un pedido", "Debes elegir un pedido de la tabla.");
        }
        return pedido;
    }

    // 🔹 Métodos para cambiar estado según el botón

    /**
     * Acción del botón "Verificar pago".
     * <p>
     * Intenta pasar el pedido a estado de verificación de pago usando la acción
     * lógica <code>"verificacionpago"</code>, que debe coincidir con la
     * implementación del patrón State en el pedido.
     * </p>
     */
    @FXML
    void verificarPago() {
        cambiarEstadoPedido("verificacionpago"); // coincide con ejecutarAccion en EstadoVerificarPago
    }

    /**
     * Acción del botón "Empaquetado".
     * Intenta pasar el pedido al estado "EMPAQUETADO".
     */
    @FXML
    void empaquetado() {
        cambiarEstadoPedido("empaquetado");
    }

    /**
     * Acción del botón "Enviado".
     * Intenta pasar el pedido al estado "ENVIADO".
     */
    @FXML
    void enviado() {
        cambiarEstadoPedido("enviado");
    }

    /**
     * Acción del botón "Entregado".
     * Intenta pasar el pedido al estado "ENTREGADO".
     */
    @FXML
    void entregado() {
        cambiarEstadoPedido("entregado");
    }

    /**
     * Encapsula la lógica común para solicitar un cambio de estado de un pedido.
     *
     * <p>Pasos que realiza:</p>
     * <ol>
     *   <li>Obtiene el pedido seleccionado de la tabla.</li>
     *   <li>Configura el pedido en el {@link GestorEstadosController}.</li>
     *   <li>Llama a {@link GestorEstadosController#cambiarEstado(String)} con la acción
     *       indicada (por ejemplo "empaquetado", "enviado").</li>
     *   <li>Si la acción fue exitosa:
     *     <ul>
     *       <li>Refresca la tabla para mostrar el nuevo estado.</li>
     *       <li>Muestra un mensaje de éxito amigable con emojis.</li>
     *     </ul>
     *   </li>
     *   <li>Si la acción falla (por ejemplo, no es válida para el estado actual):
     *     <ul>
     *       <li>Muestra una alerta de acción inválida.</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param accion cadena que identifica la acción lógica asociada al patrón State
     *               (ej. "verificacionpago", "empaquetado", "enviado", "entregado").
     */
    private void cambiarEstadoPedido(String accion) {
        Pedido pedidoSeleccionado = getPedidoSeleccionado();
        if (pedidoSeleccionado != null) {
            gestorEstadosController.setPedido(pedidoSeleccionado);

            boolean exito = gestorEstadosController.cambiarEstado(accion);

            if (exito) {
                // Refrescar la tabla para reflejar el nuevo estado
                tablaPedidos.refresh();

                String mensaje = switch (accion) {
                    case "verificacionpago" -> "💳 El pedido está en VERIFICACIÓN DE PAGO.";
                    case "empaquetado" -> "📦 El pedido fue EMPAQUETADO correctamente.";
                    case "enviado" -> "🚚 El pedido fue marcado como ENVIADO.";
                    case "entregado" -> "🎉 El pedido fue ENTREGADO al cliente.";
                    default -> "✅ Acción realizada correctamente.";
                };

                mostrarInfo("Estado actualizado", mensaje);

            } else {
                mostrarAlerta("Acción inválida",
                        "🚫 No se pudo realizar la acción '" + accion + "' sobre el pedido seleccionado.\n" +
                                "Verifica que el estado actual lo permita.");
            }
        }
    }

    /**
     * Muestra un cuadro de diálogo informativo.
     *
     * @param titulo título de la ventana.
     * @param msg    mensaje a mostrar al usuario.
     */
    private void mostrarInfo(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    /**
     * Muestra un cuadro de diálogo de advertencia (warning).
     *
     * @param titulo título de la ventana.
     * @param msg    mensaje a mostrar al usuario.
     */
    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    /**
     * Acción del botón "Volver".
     *
     * <p>Regresa al panel principal de Amazen cargando {@code amazen.fxml}
     * en la misma ventana (Stage) actual.</p>
     */
    @FXML
    void onVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("amazen.fxml"));
            AnchorPane root = loader.load();
            Stage stage = (Stage) botonVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
