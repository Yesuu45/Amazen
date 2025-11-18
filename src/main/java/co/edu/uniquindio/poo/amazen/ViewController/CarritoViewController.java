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

public class CarritoViewController {

    @FXML
    private TableView<DetallePedido> tablaCarrito;
    @FXML private TableColumn<DetallePedido, String>  columnaProducto;
    @FXML private TableColumn<DetallePedido, Integer> columnaCantidad;
    @FXML private TableColumn<DetallePedido, Double>  columnaSubtotal;

    @FXML private Label totalLabel;
    @FXML private Button botonRealizarPedido;
    @FXML private Button botonVolver;
    @FXML private Button botonEliminar;
    @FXML private Button botonActualizar;
    @FXML private TextField campoNuevaCantidad;

    // OPCIONAL: si agregas un botón para cotizar en el FXML
    @FXML private Button botonCotizarEnvio;

    private CarritoDeCompras carrito;
    private GestorPedidosController gestor;
    private HistorialController historialController;
    private ObservableList<DetallePedido> datos;

    // 🧾 para mostrar en el ticket
    private String ultimoMetodoPago = "No especificado";
    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public CarritoViewController() {}

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
     * Ahora es de instancia (ya no static) para poder guardar ultimoMetodoPago.
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

    private int leerCantidad() {
        try {
            return Integer.parseInt(campoNuevaCantidad.getText().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    private void actualizarTotal() {
        totalLabel.setText("Total: $" + String.format("%.2f", carrito.calcularTotal()));
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarInfo(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
