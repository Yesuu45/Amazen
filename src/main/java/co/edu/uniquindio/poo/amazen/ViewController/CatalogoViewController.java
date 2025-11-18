package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Controller.ProductoController;
import co.edu.uniquindio.poo.amazen.Model.CarritoDeCompras;
import co.edu.uniquindio.poo.amazen.Model.Inventario;
import co.edu.uniquindio.poo.amazen.Model.Producto;
import co.edu.uniquindio.poo.amazen.Model.Strategy.EstrategiaCatalogoAdmin;
import co.edu.uniquindio.poo.amazen.Model.Strategy.EstrategiaCatalogoUsuario;
import co.edu.uniquindio.poo.amazen.Model.Strategy.EstrategiaVistaCatalogo;
import co.edu.uniquindio.poo.amazen.Model.TiendaSession;

// RF-003
import co.edu.uniquindio.poo.amazen.Model.PrioridadEnvio;
import co.edu.uniquindio.poo.amazen.Service.CotizadorEnvioService;

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
    @FXML private TableColumn<Producto, String>  colId;
    @FXML private TableColumn<Producto, String>  colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Boolean> colDisponible;
    @FXML private TableColumn<Producto, Double> colPeso;
    @FXML private TableColumn<Producto, Double> colVolumen;

    // =================== CAMPOS PRODUCTO ===================
    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtPeso;
    @FXML private TextField txtVolumen;
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
    @FXML private Button botonVolver;

    // =================== PANEL AGREGAR ===================
    @FXML private TitledPane panelAgregarProducto;

    // =================== RF-003: ENVÍO PRODUCTO SELECCIONADO ===================
    @FXML private TextField txtOrigenEnvio;
    @FXML private TextField txtDestinoEnvio;
    @FXML private ComboBox<PrioridadEnvio> cmbPrioridadEnvio;
    @FXML private Label lblResumenEnvio;
    @FXML private Button btnCotizarEnvio;

    // =================== RF-003: PAQUETE LIBRE ===================
    @FXML private TextField txtOrigenPaquete;
    @FXML private TextField txtDestinoPaquete;
    @FXML private TextField txtPesoPaquete;
    @FXML private TextField txtVolumenPaquete;
    @FXML private ComboBox<PrioridadEnvio> cmbPrioridadPaquete;
    @FXML private Label lblResumenPaquete;

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
        colPeso.setCellValueFactory(new PropertyValueFactory<>("pesoKg"));
        colVolumen.setCellValueFactory(new PropertyValueFactory<>("volumenCm3"));

        // Cargar productos iniciales
        datosTabla = FXCollections.observableArrayList(productoController.obtenerTodos());
        tablaProductos.setItems(datosTabla);

        // Estrategia según rol
        if (TiendaSession.getInstance().esAdministrador()) {
            setEstrategiaVistaCatalogo(new EstrategiaCatalogoAdmin());
        } else {
            setEstrategiaVistaCatalogo(new EstrategiaCatalogoUsuario());
        }

        // Config RF-003 para producto seleccionado
        if (cmbPrioridadEnvio != null) {
            cmbPrioridadEnvio.getItems().setAll(PrioridadEnvio.values());
            cmbPrioridadEnvio.setValue(PrioridadEnvio.NORMAL);
        }
        if (txtOrigenEnvio != null) {
            txtOrigenEnvio.setText("Armenia");
        }

        // Config RF-003 para paquete libre
        if (cmbPrioridadPaquete != null) {
            cmbPrioridadPaquete.getItems().setAll(PrioridadEnvio.values());
            cmbPrioridadPaquete.setValue(PrioridadEnvio.NORMAL);
        }
        if (txtOrigenPaquete != null) {
            txtOrigenPaquete.setText("Armenia");
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

    // ================= ACCIONES BÁSICAS =================
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
        String pesoStr = txtPeso.getText().trim();
        String volumenStr = txtVolumen.getText().trim();
        boolean disponible = chkDisponible.isSelected();

        if (id.isEmpty() || nombre.isEmpty() || precioStr.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor, completa al menos ID, nombre y precio.");
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            double peso = pesoStr.isEmpty() ? 0.0 : Double.parseDouble(pesoStr);
            double volumen = volumenStr.isEmpty() ? 0.0 : Double.parseDouble(volumenStr);

            Producto nuevo = new Producto(id, nombre, precio, disponible, peso, volumen);
            TiendaSession.getInstance().getInventario().agregarProducto(nuevo);
            datosTabla.setAll(productoController.obtenerTodos());
            limpiarCampos();
            mostrarInfo("Producto agregado", "Se ha agregado el producto correctamente.");
        } catch (NumberFormatException e) {
            mostrarAlerta("Valores inválidos", "Precio, peso y volumen deben ser números válidos.");
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
        txtPeso.clear();
        txtVolumen.clear();
        chkDisponible.setSelected(false);
        txtBuscar.clear();
        if (txtCantidad != null) txtCantidad.setText("1");
        tablaProductos.getSelectionModel().clearSelection();
    }

    // ================= RF-003: COTIZAR ENVÍO (PRODUCTO SELECCIONADO) =================
    @FXML
    private void cotizarEnvioSeleccion() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto", "Debes seleccionar un producto para cotizar el envío.");
            return;
        }

        int cantidad = leerCantidad();
        if (cantidad <= 0) {
            mostrarAlerta("Cantidad inválida", "La cantidad debe ser mayor que cero.");
            return;
        }

        String origen = txtOrigenEnvio.getText() == null ? "" : txtOrigenEnvio.getText().trim();
        String destino = txtDestinoEnvio.getText() == null ? "" : txtDestinoEnvio.getText().trim();
        PrioridadEnvio prioridad = cmbPrioridadEnvio.getValue();

        if (origen.isEmpty() || destino.isEmpty()) {
            mostrarAlerta("Datos incompletos", "Ingresa origen y dirección/destino del envío.");
            return;
        }
        if (prioridad == null) {
            mostrarAlerta("Prioridad requerida", "Selecciona una prioridad de envío.");
            return;
        }

        double pesoTotal = seleccionado.getPesoKg() * cantidad;
        double volumenTotal = seleccionado.getVolumenCm3() * cantidad;

        double tarifa = CotizadorEnvioService.cotizar(
                origen,
                destino,
                pesoTotal,
                volumenTotal,
                prioridad
        );

        String resumen = String.format(
                "Origen: %s\nDestino: %s\nPrioridad: %s\n\n" +
                        "Producto: %s x%d\n" +
                        "Peso total: %.2f kg\nVolumen total: %.0f cm³\n\n" +
                        "Tarifa estimada de envío: $%.0f",
                origen, destino, prioridad,
                seleccionado.getNombre(), cantidad,
                pesoTotal, volumenTotal, tarifa
        );

        if (lblResumenEnvio != null) {
            lblResumenEnvio.setText("Tarifa estimada: $" + String.format("%.0f", tarifa));
        }

        mostrarInfo("Cotización de envío", resumen);
    }

    // ===== Helper para paquete libre =====
    private static class CotizacionPaquete {
        String origen;
        String destino;
        PrioridadEnvio prioridad;
        double peso;
        double volumen;
        double tarifa;
    }

    private CotizacionPaquete calcularCotizacionPaquete() {
        String origen  = txtOrigenPaquete == null ? "" : txtOrigenPaquete.getText().trim();
        String destino = txtDestinoPaquete == null ? "" : txtDestinoPaquete.getText().trim();
        String pesoStr = txtPesoPaquete == null ? "" : txtPesoPaquete.getText().trim();
        String volStr  = txtVolumenPaquete == null ? "" : txtVolumenPaquete.getText().trim();
        PrioridadEnvio prioridad = (cmbPrioridadPaquete != null) ? cmbPrioridadPaquete.getValue() : null;

        if (origen.isEmpty() || destino.isEmpty() || pesoStr.isEmpty() || volStr.isEmpty()) {
            mostrarAlerta("Datos incompletos",
                    "Ingresa origen, destino, peso (kg) y volumen (cm³) para cotizar el paquete.");
            return null;
        }

        double peso;
        double volumen;
        try {
            peso = Double.parseDouble(pesoStr);
            volumen = Double.parseDouble(volStr);
            if (peso <= 0 || volumen <= 0) {
                mostrarAlerta("Valores inválidos", "Peso y volumen deben ser mayores que cero.");
                return null;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Valores inválidos", "Peso y volumen deben ser números válidos.");
            return null;
        }

        if (prioridad == null) {
            prioridad = PrioridadEnvio.NORMAL;
        }

        double tarifa = CotizadorEnvioService.cotizar(
                origen,
                destino,
                peso,
                volumen,
                prioridad
        );

        CotizacionPaquete c = new CotizacionPaquete();
        c.origen = origen;
        c.destino = destino;
        c.prioridad = prioridad;
        c.peso = peso;
        c.volumen = volumen;
        c.tarifa = tarifa;
        return c;
    }

    // ================= RF-003: COTIZAR ENVÍO (PAQUETE LIBRE) =================
    @FXML
    private void onCotizarPaqueteLibre() {
        CotizacionPaquete c = calcularCotizacionPaquete();
        if (c == null) return;

        String resumen = String.format(
                "Origen: %s\nDestino: %s\nPrioridad: %s\n\n" +
                        "Peso: %.2f kg\nVolumen: %.0f cm³\n\n" +
                        "Tarifa estimada de envío: $%.0f",
                c.origen, c.destino, c.prioridad,
                c.peso, c.volumen, c.tarifa
        );

        if (lblResumenPaquete != null) {
            lblResumenPaquete.setText("Tarifa estimada: $" + String.format("%.0f", c.tarifa));
        }

        mostrarInfo("Cotización de paquete", resumen);
    }

    // ================= RF-003: AGREGAR PAQUETE LIBRE AL CARRITO =================
    @FXML
    private void onAgregarPaqueteAlCarrito() {
        CotizacionPaquete c = calcularCotizacionPaquete();
        if (c == null) return;

        // Producto "virtual" solo para representar el envío en el carrito
        String id = "ENV-" + System.currentTimeMillis();
        String nombre = "Envío paquete " + c.origen + " → " + c.destino;

        Producto envio = new Producto(
                id,
                nombre,
                c.tarifa,     // el precio del producto es la tarifa del envío
                true,
                c.peso,
                c.volumen
        );

        carrito.agregarProducto(envio, 1);

        if (lblResumenPaquete != null) {
            lblResumenPaquete.setText("Tarifa agregada: $" + String.format("%.0f", c.tarifa));
        }

        mostrarInfo(
                "Paquete agregado al carrito",
                "Se agregó al carrito el envío cotizado:\n\n" +
                        nombre + "\nTarifa: $" + String.format("%.0f", c.tarifa)
        );
    }

    // ================= BOTÓN VOLVER =================
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
        if (txtCantidad == null) return 1;
        try {
            return Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            return 1;
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
