package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Controller.ProductoController;
import co.edu.uniquindio.poo.amazen.Model.CarritoDeCompras;
import co.edu.uniquindio.poo.amazen.Model.Inventario;
import co.edu.uniquindio.poo.amazen.Model.Producto;
import co.edu.uniquindio.poo.amazen.Model.Strategy.*;
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

    private EstrategiaVistaCatalogo estrategiaVistaCatalogo;

    // =================== TABLA ===================
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Boolean> colDisponible;

    // =================== CAMPOS ===================
    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;

    @FXML private CheckBox chkDisponible;

    // =================== BUSQUEDA / CANTIDAD ===================
    @FXML private TextField txtBuscar;
    @FXML private TextField txtCantidad;

    // =================== BOTONES ===================
    @FXML private Button btnBuscar;
    @FXML private Button btnMostrarTodos;
    @FXML private Button btnAgregarCarrito;
    @FXML private Button btnClonar;
    @FXML private Button btnAgregar;
    @FXML private Button btnLimpiar;
    @FXML
    private Button botonVolver;

    // =================== PANELES ===================
    @FXML private TitledPane panelAgregarProducto;

    // =================== CONTROLADORES ===================
    private ProductoController productoController;
    private CarritoDeCompras carrito;
    private ObservableList<Producto> datosTabla;

    @FXML
    private void initialize() {
        Inventario inventario = TiendaSession.getInstance().getInventario();
        carrito = TiendaSession.getInstance().getCarrito();
        productoController = new ProductoController(inventario);

        // Configuración de columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        // Cargar productos iniciales
        datosTabla = FXCollections.observableArrayList(productoController.obtenerTodos());
        tablaProductos.setItems(datosTabla);

        // ===== Aplicar estrategia según usuario =====
        if(TiendaSession.getInstance().esAdministrador()){
            setEstrategiaVistaCatalogo(new EstrategiaCatalogoAdmin());
        } else {
            setEstrategiaVistaCatalogo(new EstrategiaCatalogoUsuario());
        }
    }

    // ================= STRATEGY =================
    public void setEstrategiaVistaCatalogo(EstrategiaVistaCatalogo estrategiaVistaCatalogo) {
        this.estrategiaVistaCatalogo = estrategiaVistaCatalogo;
        if (this.estrategiaVistaCatalogo != null) {
            this.estrategiaVistaCatalogo.mostrarCatalogo(this);
        }
    }

    public void mostrarPanelAgregarProducto(boolean visible) {
        if (panelAgregarProducto != null) {
            panelAgregarProducto.setVisible(visible);
            panelAgregarProducto.setManaged(visible);
        }
    }

    public void mostrarBotonAgregarCarrito(boolean visible) {
        if (btnAgregarCarrito != null) {
            btnAgregarCarrito.setVisible(visible);
            btnAgregarCarrito.setManaged(visible);
        }
    }

    public void mostrarBotonClonar(boolean visible) {
        if (btnClonar != null) {
            btnClonar.setVisible(visible);
            btnClonar.setManaged(visible);
        }
    }

    // ================= ACCIONES =================
    @FXML
    private void buscarProducto() {
        String nombre = txtBuscar.getText().trim();
        List<Producto> resultados = productoController.buscarPorNombre(nombre);
        datosTabla.setAll(resultados);
    }

    @FXML
    private void mostrarTodos() {
        datosTabla.setAll(productoController.obtenerTodos());
    }

    @FXML
    private void agregarAlCarrito() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto", "Debes seleccionar un producto para agregar al carrito.");
            return;
        }
        int cantidad = leerCantidad();
        if (cantidad <= 0) {
            mostrarAlerta("Cantidad inválida", "La cantidad debe ser mayor que cero.");
            return;
        }
        carrito.agregarProducto(seleccionado, cantidad);
        mostrarInfo("Agregado al carrito", "Producto agregado: " + seleccionado.getNombre() + " x" + cantidad);
    }

    @FXML
    private void agregarProducto() {
        String id = txtId.getText().trim();
        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        boolean disponible = chkDisponible.isSelected();

        if (id.isEmpty() || nombre.isEmpty() || precioStr.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor, completa todos los campos del producto.");
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            Producto nuevo = new Producto(id, nombre, precio, disponible);
            TiendaSession.getInstance().getInventario().agregarProducto(nuevo);
            datosTabla.setAll(productoController.obtenerTodos());
            limpiarCampos();
            mostrarInfo("Producto agregado", "Se ha agregado el producto correctamente.");
        } catch (NumberFormatException e) {
            mostrarAlerta("Precio inválido", "El precio debe ser un número válido.");
        }
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
            mostrarInfo("Clon creado", "Se clonó el producto con id: " + clon.getId());
        } else {
            mostrarAlerta("Error", "No se pudo clonar el producto.");
        }
    }

    @FXML
    private void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtPrecio.clear();
        chkDisponible.setSelected(false);
        txtBuscar.clear();
        txtCantidad.setText("1");
        tablaProductos.getSelectionModel().clearSelection();
    }

    // =================== BOTÓN VOLVER ===================
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

    // ================= UTILIDADES =================
    private int leerCantidad() {
        try {
            return Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // ================= MÉTODO PÚBLICO =================
    public void mostrarCatalogo() {
        if (estrategiaVistaCatalogo != null) {
            estrategiaVistaCatalogo.mostrarCatalogo(this);
        }
    }
}
