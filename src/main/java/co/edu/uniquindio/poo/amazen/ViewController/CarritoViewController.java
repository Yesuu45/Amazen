package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Controller.GestorPedidosController;
import co.edu.uniquindio.poo.amazen.Controller.HistorialController;
import co.edu.uniquindio.poo.amazen.Model.CarritoDeCompras;
import co.edu.uniquindio.poo.amazen.Model.DetallePedido;
import co.edu.uniquindio.poo.amazen.Model.Pago.*;
import co.edu.uniquindio.poo.amazen.Model.Pedido;
import co.edu.uniquindio.poo.amazen.Model.TiendaSession;
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

public class CarritoViewController {
    @FXML
    private TableView<DetallePedido> tablaCarrito;
    @FXML private TableColumn<DetallePedido, String> columnaProducto;
    @FXML private TableColumn<DetallePedido, Integer> columnaCantidad;
    @FXML private TableColumn<DetallePedido, Double> columnaSubtotal;

    @FXML private Label totalLabel;
    @FXML private Button botonRealizarPedido;
    @FXML private Button botonVolver;
    @FXML private Button botonEliminar;
    @FXML private Button botonActualizar;
    @FXML private TextField campoNuevaCantidad;

    private CarritoDeCompras carrito;
    private GestorPedidosController gestor;
    private HistorialController historialController;
    private ObservableList<DetallePedido> datos;

    public CarritoViewController() {}

    @FXML
    private void initialize() {
        carrito = TiendaSession.getInstance().getCarrito();
        gestor = new GestorPedidosController();
        historialController = new HistorialController();

        columnaProducto.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DetallePedido,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DetallePedido, String> param) {
                return new SimpleStringProperty(param.getValue().getProducto().getNombre());
            }
        });
        columnaCantidad.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DetallePedido,Integer>, ObservableValue<Integer>>() {
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<DetallePedido, Integer> param) {
                return new SimpleIntegerProperty(param.getValue().getCantidad()).asObject();
            }
        });
        columnaSubtotal.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DetallePedido,Double>, ObservableValue<Double>>() {
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<DetallePedido, Double> param) {
                return new SimpleDoubleProperty(param.getValue().getSubtotal()).asObject();
            }
        });

        datos = FXCollections.observableArrayList(carrito.getDetalles());
        tablaCarrito.setItems(datos);
        actualizarTotal();
    }
    @FXML
    private void realizarPedido() {
        if (carrito.getDetalles().isEmpty()) {
            mostrarAlerta("Carrito vacío", "Agrega productos antes de realizar el pedido.");
            return;
        }

        // Crear el pedido en estado "Nuevo" desde el gestor
        Pedido pedido = gestor.crearPedido(carrito);

        // Registrar el pedido en el historial
        historialController.registrarPedido(pedido);

        // Mostrar mensaje del estado inicial (ya es "Nuevo")
        mostrarInfo("Pedido realizado", "📌 Estado actual: " + pedido.getEstado());

        // Procesar el pago
        ProcesarPago procesador = getProcesarPago();
        String resultado = procesador.ejecutarPago(pedido.calcularTotal());
        mostrarInfo("Resultado del pago", resultado);

        // Cambiar a "Pagado" solo si el pago fue exitoso
        if (resultado.toLowerCase().contains("exitoso")) {
            try {
                pedido.procesar("pagar");  // ⚡ Cambia el estado a "Pagado"
                mostrarInfo("Estado actualizado", "✅ Estado actual: " + pedido.getEstado());
            } catch (IllegalArgumentException e) {
                mostrarAlerta("Error de estado", e.getMessage());
            }
        }

        // Actualizar el total del carrito
        actualizarTotal();
    }






    private static ProcesarPago getProcesarPago() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Efectivo", "Efectivo", "Tarjeta", "PayPal", "Pasarela Externa");
        dialog.setTitle("Método de pago");
        dialog.setHeaderText("Seleccione un método de pago");
        dialog.setContentText("Método:");
        String elegido = dialog.showAndWait().orElse("Efectivo");

        // Configurar el procesador de pago
        ProcesarPago procesador = new ProcesarPago();
        switch (elegido) {
            case "Tarjeta" -> procesador.setMetodoPago(new PagoTarjeta("1234-5678-9012"));
            case "PayPal" -> procesador.setMetodoPago(new PagoPayPal("usuario@paypal.com"));
            case "Pasarela Externa" -> procesador.setMetodoPago(new Pasarela("QuickPayService"));
            default -> procesador.setMetodoPago(new PagoEfectivo());
        }
        return procesador;
    }


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
        a.setTitle(titulo); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarInfo(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}
