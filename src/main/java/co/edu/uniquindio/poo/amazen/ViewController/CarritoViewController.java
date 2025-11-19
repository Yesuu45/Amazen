package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Controller.GestorPedidosController;
import co.edu.uniquindio.poo.amazen.Controller.HistorialController;
import co.edu.uniquindio.poo.amazen.Model.CarritoDeCompras;
import co.edu.uniquindio.poo.amazen.Model.DetallePedido;
import co.edu.uniquindio.poo.amazen.Model.Pago.*;
import co.edu.uniquindio.poo.amazen.Model.Pedido;
import co.edu.uniquindio.poo.amazen.Model.TiendaSession;

// NUEVO: para RF-003
import co.edu.uniquindio.poo.amazen.Model.PrioridadEnvio;
import co.edu.uniquindio.poo.amazen.Service.CotizadorEnvioService;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Controlador de la vista del carrito de compras.
 * <p>
 * Responsabilidades principales:
 * <ul>
 *     <li>Mostrar los productos agregados al carrito ({@link DetallePedido}).</li>
 *     <li>Permitir actualizar cantidades y eliminar ítems.</li>
 *     <li>Crear el {@link Pedido} a partir del carrito y registrarlo en el historial.</li>
 *     <li>Gestionar el proceso de pago utilizando el patrón Strategy para los distintos métodos.</li>
 *     <li>RF-003: Cotizar el valor del envío según origen, destino, peso, volumen y prioridad.</li>
 * </ul>
 * Colabora con:
 * <ul>
 *     <li>{@link GestorPedidosController}: creación de pedidos desde el carrito.</li>
 *     <li>{@link HistorialController}: registro del pedido en el historial.</li>
 *     <li>{@link TiendaSession}: obtiene el carrito actual y el cliente activo.</li>
 * </ul>
 */
public class CarritoViewController {

    // ===================== COMPONENTES DE LA VISTA =====================

    /** Tabla principal que muestra los detalles del carrito. */
    @FXML
    private TableView<DetallePedido> tablaCarrito;
    /** Columna que muestra el nombre del producto. */
    @FXML private TableColumn<DetallePedido, String>  columnaProducto;
    /** Columna que muestra la cantidad solicitada. */
    @FXML private TableColumn<DetallePedido, Integer> columnaCantidad;
    /** Columna que muestra el subtotal (cantidad x precio). */
    @FXML private TableColumn<DetallePedido, Double>  columnaSubtotal;

    /** Etiqueta donde se muestra el total acumulado del carrito. */
    @FXML private Label totalLabel;
    /** Botón para confirmar la compra y generar el pedido. */
    @FXML private Button botonRealizarPedido;
    /** Botón para volver al panel principal Amazen. */
    @FXML private Button botonVolver;
    /** Botón para eliminar un producto seleccionado del carrito. */
    @FXML private Button botonEliminar;
    /** Botón para actualizar la cantidad del ítem seleccionado. */
    @FXML private Button botonActualizar;
    /** Campo de texto para digitar la nueva cantidad del producto. */
    @FXML private TextField campoNuevaCantidad;

    /**
     * Botón para cotizar el envío (puede ser opcional según el FXML).
     * Implementa RF-003 a nivel de interfaz.
     */
    @FXML private Button botonCotizarEnvio;

    // ===================== MODELO Y CONTROLADORES =====================

    /** Carrito de compras asociado a la sesión actual. */
    private CarritoDeCompras carrito;
    /** Componente de negocio encargado de crear pedidos. */
    private GestorPedidosController gestor;
    /** Controlador de historial para registrar los pedidos. */
    private HistorialController historialController;
    /** Lista observable que respalda la tabla de detalles. */
    private ObservableList<DetallePedido> datos;

    // 🧾 Información del último pago para mostrar en el comprobante
    private String ultimoMetodoPago = "No especificado";
    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public CarritoViewController() {}

    /**
     * Inicializa la vista del carrito.
     * <p>
     * Se ejecuta automáticamente al cargar el FXML y realiza:
     * <ul>
     *     <li>Obtención del carrito actual desde {@link TiendaSession}.</li>
     *     <li>Creación de {@link GestorPedidosController} y {@link HistorialController}.</li>
     *     <li>Configuración de las columnas de la tabla utilizando propiedades.</li>
     *     <li>Sincronización de la tabla con el contenido del carrito.</li>
     *     <li>Cálculo inicial del total del carrito.</li>
     * </ul>
     */
    @FXML
    private void initialize() {
        carrito = TiendaSession.getInstance().getCarrito();
        gestor = new GestorPedidosController();
        historialController = new HistorialController();

        columnaProducto.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DetallePedido, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DetallePedido, String> param) {
                return new SimpleStringProperty(param.getValue().getProducto().getNombre());
            }
        });
        columnaCantidad.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DetallePedido, Integer>, ObservableValue<Integer>>() {
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<DetallePedido, Integer> param) {
                return new SimpleIntegerProperty(param.getValue().getCantidad()).asObject();
            }
        });
        columnaSubtotal.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DetallePedido, Double>, ObservableValue<Double>>() {
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<DetallePedido, Double> param) {
                return new SimpleDoubleProperty(param.getValue().getSubtotal()).asObject();
            }
        });

        datos = FXCollections.observableArrayList(carrito.getDetalles());
        tablaCarrito.setItems(datos);
        actualizarTotal();
    }

    // ================== RF-003: COTIZAR ENVÍO ==================

    /**
     * Acción del botón "Cotizar envío".
     * <p>
     * Implementa el requerimiento RF-003:
     * <ol>
     *     <li>Verifica que el carrito no esté vacío.</li>
     *     <li>Obtiene el peso y volumen total del carrito
     *         (usando {@link CarritoDeCompras#calcularPesoTotal()} y {@link CarritoDeCompras#calcularVolumenTotal()}).</li>
     *     <li>Pide al usuario el origen y destino mediante diálogos.</li>
     *     <li>Permite elegir la prioridad del envío ({@link PrioridadEnvio}).</li>
     *     <li>Llama a {@link CotizadorEnvioService#cotizar(String, String, double, double, PrioridadEnvio)}
     *         para obtener la tarifa estimada.</li>
     *     <li>Muestra el resumen de la cotización en una alerta informativa.</li>
     * </ol>
     */
    @FXML
    private void cotizarEnvio() {
        if (carrito.getDetalles().isEmpty()) {
            mostrarAlerta("Carrito vacío", "Agrega productos antes de cotizar el envío.");
            return;
        }

        // Peso y volumen totales del carrito (necesitas calcularPesoTotal / calcularVolumenTotal en CarritoDeCompras)
        double pesoTotal = carrito.calcularPesoTotal();
        double volumenTotal = carrito.calcularVolumenTotal();

        // 1) Origen
        TextInputDialog dlgOrigen = new TextInputDialog("Armenia");
        dlgOrigen.setTitle("Cotizar envío");
        dlgOrigen.setHeaderText("Datos de envío");
        dlgOrigen.setContentText("Origen:");
        String origen = dlgOrigen.showAndWait().orElse(null);
        if (origen == null || origen.isBlank()) {
            return;
        }

        // 2) Destino
        TextInputDialog dlgDestino = new TextInputDialog("Bogotá");
        dlgDestino.setTitle("Cotizar envío");
        dlgDestino.setHeaderText("Datos de envío");
        dlgDestino.setContentText("Destino:");
        String destino = dlgDestino.showAndWait().orElse(null);
        if (destino == null || destino.isBlank()) {
            return;
        }

        // 3) Prioridad
        ChoiceDialog<String> dlgPrioridad = new ChoiceDialog<>("NORMAL", "BAJA", "NORMAL", "ALTA");
        dlgPrioridad.setTitle("Cotizar envío");
        dlgPrioridad.setHeaderText("Seleccione prioridad de envío");
        dlgPrioridad.setContentText("Prioridad:");

        String elegido = dlgPrioridad.showAndWait().orElse("NORMAL");
        PrioridadEnvio prioridad = PrioridadEnvio.valueOf(elegido);

        // 4) Calcular tarifa usando el servicio de negocio
        double tarifa = CotizadorEnvioService.cotizar(
                origen,
                destino,
                pesoTotal,
                volumenTotal,
                prioridad
        );

        // 5) Mostrar cotización al usuario
        String mensaje = "Origen: " + origen +
                "\nDestino: " + destino +
                "\nPrioridad: " + prioridad +
                "\n\nPeso total: " + String.format("%.2f", pesoTotal) + " kg" +
                "\nVolumen total: " + String.format("%.0f", volumenTotal) + " cm³" +
                "\n\nTarifa estimada de envío: $" + String.format("%.0f", tarifa);

        mostrarInfo("Cotización de envío", mensaje);
    }

    // ================== PEDIDO / PAGO ==================

    /**
     * Acción del botón "Realizar pedido".
     * <p>
     * Flujo completo:
     * <ol>
     *     <li>Valida que el carrito tenga productos.</li>
     *     <li>Crea un {@link Pedido} a partir del carrito usando {@link GestorPedidosController}.</li>
     *     <li>Registra el pedido en el historial mediante {@link HistorialController}.</li>
     *     <li>Notifica el estado inicial del pedido.</li>
     *     <li>Solicita al usuario el método de pago y procesa el cobro (patrón Strategy en {@link ProcesarPago}).</li>
     *     <li>Ejecuta la transición de estado "pagar" sobre el pedido (patrón State).</li>
     *     <li>Genera y muestra un comprobante de pago con datos del cliente y del pedido.</li>
     *     <li>Actualiza el total mostrado del carrito.</li>
     * </ol>
     */
    @FXML
    private void realizarPedido() {
        if (carrito.getDetalles().isEmpty()) {
            mostrarAlerta("Carrito vacío", "Agrega productos antes de realizar el pedido.");
            return;
        }

        // Crear el pedido en estado inicial desde el gestor
        Pedido pedido = gestor.crearPedido(carrito);

        // Registrar el pedido en el historial
        historialController.registrarPedido(pedido);

        // Mostrar mensaje del estado inicial
        mostrarInfo("Pedido creado", "📌 Estado actual: " + pedido.getEstado());

        // Procesar el pago (Strategy)
        ProcesarPago procesador = getProcesarPago();
        String resultado = procesador.ejecutarPago(pedido.calcularTotal());
        mostrarInfo("Resultado del pago", resultado);

        // Cambiar a "Pagado" (State) y mostrar comprobante
        try {
            pedido.procesar("pagar");  // ⚡ Cambia el estado a "Pagado"
            mostrarInfo("Estado actualizado", "✅ Estado actual: " + pedido.getEstado());

            // 🧾 Ticket / comprobante de pago
            String nombreCliente = "";
            String apellidoCliente = "";
            if (TiendaSession.getInstance().getPersonaActiva() != null) {
                nombreCliente = TiendaSession.getInstance().getPersonaActiva().getNombre();
                apellidoCliente = TiendaSession.getInstance().getPersonaActiva().getApellido();
            }

            String fechaCreacion = pedido.getFechaCreacion() == null
                    ? "—"
                    : FECHA_HORA.format(pedido.getFechaCreacion());

            String ticket = String.format(
                    "ID pedido: %s%n" +
                            "Fecha creación: %s%n" +
                            "Cliente: %s %s%n" +
                            "Total pagado: $%.2f%n" +
                            "Método de pago: %s%n" +
                            "Estado: %s",
                    pedido.getId(),
                    fechaCreacion,
                    nombreCliente,
                    apellidoCliente,
                    pedido.calcularTotal(),
                    ultimoMetodoPago,
                    pedido.getEstado()
            );

            mostrarInfo("Comprobante de pago", ticket);

        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error de estado", e.getMessage());
        }

        // Actualizar el total del carrito
        actualizarTotal();
    }

    /**
     * Construye un {@link ProcesarPago} configurando el método concreto
     * según la selección del usuario.
     * <p>
     * Implementa el patrón Strategy:
     * <ul>
     *     <li>Cada implementación de {@link MetodoPago} encapsula la lógica de cobro.</li>
     *     <li>El usuario elige "Efectivo", "Tarjeta", "PayPal" o "Pasarela Externa".</li>
     *     <li>Se guarda el nombre del método en {@link #ultimoMetodoPago} para el comprobante.</li>
     * </ul>
     *
     * @return instancia de {@link ProcesarPago} lista para ejecutar el pago.
     */
    private ProcesarPago getProcesarPago() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Efectivo", "Efectivo", "Tarjeta", "PayPal", "Pasarela Externa");
        dialog.setTitle("Método de pago");
        dialog.setHeaderText("Seleccione un método de pago");
        dialog.setContentText("Método:");
        String elegido = dialog.showAndWait().orElse("Efectivo");

        ProcesarPago procesador = new ProcesarPago();
        switch (elegido) {
            case "Tarjeta" -> {
                procesador.setMetodoPago(new PagoTarjeta("1234-5678-9012"));
                ultimoMetodoPago = "Tarjeta";
            }
            case "PayPal" -> {
                procesador.setMetodoPago(new PagoPayPal("usuario@paypal.com"));
                ultimoMetodoPago = "PayPal";
            }
            case "Pasarela Externa" -> {
                procesador.setMetodoPago(new Pasarela("QuickPayService"));
                ultimoMetodoPago = "Pasarela externa";
            }
            default -> {
                procesador.setMetodoPago(new PagoEfectivo());
                ultimoMetodoPago = "Efectivo";
            }
        }
        return procesador;
    }

    // ================== CRUD CARRITO ==================

    /**
     * Elimina del carrito el {@link DetallePedido} actualmente seleccionado en la tabla.
     * <p>
     * Controla:
     * <ul>
     *     <li>Que exista una fila seleccionada.</li>
     *     <li>Sincronización de la lista observable con el carrito.</li>
     *     <li>Re-cálculo del total.</li>
     *     <li>Mensaje informativo con el nombre del producto eliminado.</li>
     * </ul>
     */
    @FXML
    private void eliminarProducto() {
        DetallePedido seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto", "Debes seleccionar un producto para eliminar.");
            return;
        }
        carrito.getDetalles().remove(seleccionado);
        datos.setAll(carrito.getDetalles());
        actualizarTotal();
        mostrarInfo("Eliminado", "Se eliminó " + seleccionado.getProducto().getNombre() + " del carrito.");
    }

    /**
     * Actualiza la cantidad del producto seleccionado tomando el valor
     * digitado en {@link #campoNuevaCantidad}.
     * <p>
     * Validaciones:
     * <ul>
     *     <li>Debe haber un producto seleccionado.</li>
     *     <li>La cantidad debe ser un entero mayor que cero.</li>
     * </ul>
     * Si es correcto, actualiza el modelo, refresca la tabla y recalcula el total.
     */
    @FXML
    private void actualizarCantidad() {
        DetallePedido seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto", "Debes seleccionar un producto para actualizar su cantidad.");
            return;
        }
        int nuevaCantidad = leerCantidad();
        if (nuevaCantidad <= 0) {
            mostrarAlerta("Cantidad inválida", "La cantidad debe ser mayor que cero.");
            return;
        }
        seleccionado.setCantidad(nuevaCantidad);
        datos.setAll(carrito.getDetalles());
        actualizarTotal();
        mostrarInfo("Actualizado", "Nueva cantidad: " + nuevaCantidad + " para " + seleccionado.getProducto().getNombre());
    }

    // ================== NAVEGACIÓN ==================

    /**
     * Acción del botón "Volver".
     * <p>
     * Regresa al panel principal Amazen ({@code amazen.fxml}) dentro de la
     * misma ventana actual.
     */
    @FXML
    private void onVolver() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("amazen.fxml"));
            AnchorPane root = loader.load();
            Stage stage = (Stage) botonVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver a la pantalla principal.");
        }
    }

    // ================== HELPERS ==================

    /**
     * Intenta leer la cantidad desde {@link #campoNuevaCantidad}.
     *
     * @return la cantidad como entero, o -1 si ocurre un error de parseo.
     */
    private int leerCantidad() {
        try {
            return Integer.parseInt(campoNuevaCantidad.getText().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Recalcula el total del carrito usando {@link CarritoDeCompras#calcularTotal()}
     * y actualiza la etiqueta {@link #totalLabel}.
     */
    private void actualizarTotal() {
        totalLabel.setText("Total: $" + String.format("%.2f", carrito.calcularTotal()));
    }

    /**
     * Muestra una alerta de tipo WARNING sin encabezado.
     *
     * @param titulo título de la ventana de alerta
     * @param msg    mensaje a mostrar
     */
    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    /**
     * Muestra una alerta de tipo INFORMATION sin encabezado.
     *
     * @param titulo título de la ventana de alerta
     * @param msg    mensaje a mostrar
     */
    private void mostrarInfo(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
