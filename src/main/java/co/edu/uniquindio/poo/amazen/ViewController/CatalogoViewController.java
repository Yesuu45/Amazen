package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Controller.ProductoController;
import co.edu.uniquindio.poo.amazen.Model.CarritoDeCompras;
import co.edu.uniquindio.poo.amazen.Model.Inventario;
import co.edu.uniquindio.poo.amazen.Model.Producto;
import co.edu.uniquindio.poo.amazen.Model.TiendaSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CatalogoViewController {
    @FXML
    private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> columnaNombre;
    @FXML private TableColumn<Producto, Double> columnaPrecio;

    @FXML private TextField campoBusqueda;
    @FXML private TextField campoCantidad;
    @FXML private Button botonBuscar;
    @FXML private Button botonAgregar;
    @FXML private Button botonClonar;
    @FXML private Button botonVolver;

    private ProductoController productoController;
    private CarritoDeCompras carrito;
    private ObservableList<Producto> datosTabla;

    public CatalogoViewController() { }

    @FXML
    private void initialize() {
        Inventario inventario = TiendaSession.getInstance().getInventario();
        carrito = TiendaSession.getInstance().getCarrito();
        productoController = new ProductoController(inventario);

        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        datosTabla = FXCollections.observableArrayList();
        datosTabla.setAll(productoController.obtenerTodos());
        tablaProductos.setItems(datosTabla);

        if (campoCantidad != null) {
            campoCantidad.setText("1");
        }
    }

    @FXML
    private void buscarProducto() {
        String nombre = campoBusqueda.getText();
        List<Producto> res = productoController.buscarPorNombre(nombre);
        datosTabla.setAll(res);
    }

    @FXML
    private void agregarAlCarrito() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto", "Debes seleccionar un producto de la tabla.");
            return;
        }
        int cantidad = leerCantidad();
        if (cantidad <= 0) {
            mostrarAlerta("Cantidad inválida", "La cantidad debe ser un número entero positivo.");
            return;
        }
        carrito.agregarProducto(seleccionado, cantidad);
        mostrarInfo("Agregado", "Se agregó al carrito: " + seleccionado.getNombre() + " x" + cantidad);
    }

    @FXML
    private void clonarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto", "Debes seleccionar un producto para clonar.");
            return;
        }
        Producto clon = productoController.clonarProducto(seleccionado);
        if (clon != null) {
            datosTabla.setAll(productoController.obtenerTodos());
            mostrarInfo("Producto clonado", "Se creó el clon con id: " + clon.getId());
        } else {
            mostrarAlerta("Error", "No fue posible clonar el producto.");
        }
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
            return Integer.parseInt(campoCantidad.getText().trim());
        } catch (Exception e) {
            return -1;
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
