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
import co.edu.uniquindio.poo.amazen.Model.PrioridadEnvio;
import co.edu.uniquindio.poo.amazen.Service.CotizadorEnvioService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Controlador de la vista de catálogo de productos.
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 *     <li>Mostrar el inventario de productos en una tabla.</li>
 *     <li>Permitir búsqueda por nombre y recarga del catálogo completo.</li>
 *     <li>Agregar productos al carrito de compras del usuario.</li>
 *     <li>Crear, clonar y limpiar productos (según el rol).</li>
 *     <li>RF-003: Cotizar envíos tanto para un producto seleccionado como para un paquete libre.</li>
 *     <li>Integrarse con el mapa para seleccionar una dirección de destino.</li>
 * </ul>
 *
 * <p>Patrones utilizados:</p>
 * <ul>
 *     <li><b>Strategy</b>: {@link EstrategiaVistaCatalogo}, {@link EstrategiaCatalogoAdmin},
 *     {@link EstrategiaCatalogoUsuario} para adaptar la interfaz según el rol (admin/usuario).</li>
 *     <li>Servicios de dominio: {@link CotizadorEnvioService} para el cálculo de tarifas de envío.</li>
 * </ul>
 */
public class CatalogoViewController {

    /**
     * Estrategia visual del catálogo (admin o usuario), definida en tiempo de ejecución
     * según el rol de la persona activa en {@link TiendaSession}.
     */
    private EstrategiaVistaCatalogo estrategiaVistaCatalogo;

    // =================== TABLA ===================

    /** Tabla principal que muestra la lista de productos del inventario. */
    @FXML private TableView<Producto> tablaProductos;
    /** Columna que muestra el identificador del producto. */
    @FXML private TableColumn<Producto, String>  colId;
    /** Columna que muestra el nombre del producto. */
    @FXML private TableColumn<Producto, String>  colNombre;
    /** Columna que muestra el precio unitario del producto. */
    @FXML private TableColumn<Producto, Double> colPrecio;
    /** Columna que indica si el producto está disponible. */
    @FXML private TableColumn<Producto, Boolean> colDisponible;
    /** Columna que muestra el peso en kg del producto. */
    @FXML private TableColumn<Producto, Double> colPeso;
    /** Columna que muestra el volumen en cm³ del producto. */
    @FXML private TableColumn<Producto, Double> colVolumen;

    // =================== CAMPOS PRODUCTO ===================

    /** Campo de texto para el ID del producto (alta/edición). */
    @FXML private TextField txtId;
    /** Campo de texto para el nombre del producto. */
    @FXML private TextField txtNombre;
    /** Campo de texto para el precio del producto. */
    @FXML private TextField txtPrecio;
    /** Campo de texto para el peso en kg del producto. */
    @FXML private TextField txtPeso;
    /** Campo de texto para el volumen en cm³ del producto. */
    @FXML private TextField txtVolumen;
    /** Indica si el producto está disponible para su compra. */
    @FXML private CheckBox chkDisponible;

    // =================== BUSQUEDA / CANTIDAD ===================

    /** Texto de búsqueda por nombre de producto. */
    @FXML private TextField txtBuscar;
    /** Cantidad a agregar al carrito o usar en la cotización del envío. */
    @FXML private TextField txtCantidad;

    // =================== BOTONES ===================

    @FXML private Button btnBuscar;
    @FXML private Button btnMostrarTodos;
    @FXML private Button btnAgregarCarrito;
    @FXML private Button btnClonar;
    @FXML private Button btnAgregar;
    @FXML private Button btnLimpiar;
    @FXML private Button botonVolver;

    // =================== RF-003: ENVÍO PRODUCTO SELECCIONADO ===================

    /** Campo con el origen del envío para el producto seleccionado. */
    @FXML private TextField txtOrigenEnvio;
    /** Campo con el destino del envío para el producto seleccionado. */
    @FXML private TextField txtDestinoEnvio;
    /** Selección de prioridad del envío para el producto seleccionado. */
    @FXML private ComboBox<PrioridadEnvio> cmbPrioridadEnvio;
    /** Resumen simple de la cotización de envío del producto seleccionado. */
    @FXML private Label lblResumenEnvio;
    @FXML private Button btnCotizarEnvio;

    /** Botón para abrir el mapa desde la sección de envío de producto (si existe en el FXML). */
    @FXML private Button btnSeleccionarMapaEnvio;

    // =================== RF-003: PAQUETE LIBRE ===================

    /** Campo con el origen del envío para el paquete libre (no ligado a un producto). */
    @FXML private TextField txtOrigenPaquete;
    /** Campo con el destino del envío para el paquete libre. */
    @FXML private TextField txtDestinoPaquete;
    /** Campo con el peso del paquete libre en kg. */
    @FXML private TextField txtPesoPaquete;
    /** Campo con el volumen del paquete libre en cm³. */
    @FXML private TextField txtVolumenPaquete;
    /** Selección de prioridad del envío para el paquete libre. */
    @FXML private ComboBox<PrioridadEnvio> cmbPrioridadPaquete;
    /** Resumen de la cotización del paquete libre. */
    @FXML private Label lblResumenPaquete;

    /** Botón para abrir el mapa desde la sección de paquete libre (si existe en el FXML). */
    @FXML private Button btnSeleccionarMapaPaquete;

    // =================== CONTROLADORES ===================

    /** Controlador de negocio para operaciones sobre productos. */
    private ProductoController productoController;
    /** Carrito de compras asociado a la sesión actual. */
    private CarritoDeCompras carrito;
    /** Lista observable que respalda la tabla de productos. */
    private ObservableList<Producto> datosTabla;

    /**
     * Inicialización del controlador.
     *
     * <p>Se ejecuta automáticamente al cargar el FXML y realiza:</p>
     * <ul>
     *     <li>Obtiene el {@link Inventario} y el {@link CarritoDeCompras} desde {@link TiendaSession}.</li>
     *     <li>Crea el {@link ProductoController} asociado al inventario.</li>
     *     <li>Configura las columnas de la tabla con las propiedades de {@link Producto}.</li>
     *     <li>Carga el listado inicial de productos.</li>
     *     <li>Selecciona la estrategia de vista según si el usuario es administrador o no.</li>
     *     <li>Inicializa los controles de RF-003 para el producto seleccionado y paquete libre.</li>
     * </ul>
     */
    @FXML
    private void initialize() {
        Inventario inventario = TiendaSession.getInstance().getInventario();
        carrito = TiendaSession.getInstance().getCarrito();
        productoController = new ProductoController(inventario);

        // Configuración de columnas de la tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));
        colPeso.setCellValueFactory(new PropertyValueFactory<>("pesoKg"));
        colVolumen.setCellValueFactory(new PropertyValueFactory<>("volumenCm3"));

        // Cargar productos iniciales
        datosTabla = FXCollections.observableArrayList(productoController.obtenerTodos());
        tablaProductos.setItems(datosTabla);

        // Selección de estrategia según rol
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

    /**
     * Establece la estrategia visual del catálogo (admin o usuario) y
     * la aplica inmediatamente sobre esta vista.
     *
     * @param estrategiaVistaCatalogo implementación concreta de {@link EstrategiaVistaCatalogo}
     *                                (ej. {@link EstrategiaCatalogoAdmin}, {@link EstrategiaCatalogoUsuario}).
     */
    public void setEstrategiaVistaCatalogo(EstrategiaVistaCatalogo estrategiaVistaCatalogo) {
        this.estrategiaVistaCatalogo = estrategiaVistaCatalogo;
        if (this.estrategiaVistaCatalogo != null) {
            this.estrategiaVistaCatalogo.mostrarCatalogo(this);
        }
    }

    /**
     * Permite mostrar u ocultar el panel de alta/edición de producto.
     * <p>Implementación opcional: si se usa un {@code TitledPane} u otro contenedor
     * para la sección de "Agregar producto", puede controlarse aquí.</p>
     *
     * @param visible {@code true} para mostrar el panel; {@code false} para ocultarlo.
     */
    public void mostrarPanelAgregarProducto(boolean visible) {
        // Si tienes un TitledPane u otro panel, puedes controlar su visibilidad aquí
    }

    /**
     * Muestra u oculta el botón para agregar al carrito.
     * Esto es útil para el Strategy de catálogo de administrador.
     */
    public void mostrarBotonAgregarCarrito(boolean visible) {
        if (btnAgregarCarrito != null) {
            btnAgregarCarrito.setVisible(visible);
            btnAgregarCarrito.setManaged(visible);
        }
    }

    /**
     * Muestra u oculta el botón para clonar productos.
     * Normalmente se habilita sólo en la vista de administrador.
     */
    public void mostrarBotonClonar(boolean visible) {
        if (btnClonar != null) {
            btnClonar.setVisible(visible);
            btnClonar.setManaged(visible);
        }
    }

    // ================= ACCIONES BÁSICAS =================

    /**
     * Acción del botón "Buscar".
     * Filtra los productos por nombre utilizando {@link ProductoController#buscarPorNombre(String)}
     * y actualiza la tabla con los resultados.
     */
    @FXML
    private void buscarProducto() {
        String nombre = txtBuscar.getText().trim();
        List<Producto> resultados = productoController.buscarPorNombre(nombre);
        datosTabla.setAll(resultados);
    }

    /**
     * Acción del botón "Mostrar todos".
     * Recarga en la tabla todo el catálogo disponible.
     */
    @FXML
    private void mostrarTodos() {
        datosTabla.setAll(productoController.obtenerTodos());
    }

    /**
     * Agrega el producto seleccionado en la tabla al carrito de compras,
     * usando la cantidad indicada en {@link #txtCantidad}.
     */
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

    /**
     * Acción del botón "Agregar" (crear nuevo producto).
     * <p>
     * Valida los campos obligatorios (id, nombre, precio), convierte los
     * valores numéricos y agrega el producto al inventario.
     * Finalmente, recarga la tabla y limpia los campos.
     * </p>
     */
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

    /**
     * Acción del botón "Clonar".
     * Crea una copia del producto seleccionado (con nuevo id)
     * y recarga la tabla con el resultado.
     */
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

    /**
     * Limpia todos los campos de texto y desmarca el checkbox de disponibilidad.
     * Además, restablece la cantidad a 1 y limpia la selección de la tabla.
     */
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

    /**
     * Cotiza el envío de un producto seleccionado en la tabla considerando:
     * <ul>
     *     <li>Origen y destino (campos de texto).</li>
     *     <li>Cantidad seleccionada en el catálogo.</li>
     *     <li>Peso y volumen del producto.</li>
     *     <li>Prioridad del envío ({@link PrioridadEnvio}).</li>
     * </ul>
     * Usa {@link CotizadorEnvioService#cotizar(String, String, double, double, PrioridadEnvio)}
     * y muestra el resultado tanto en un {@link Alert} como en el {@link #lblResumenEnvio}.
     */
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

    /**
     * Estructura de datos interna para devolver una cotización calculada
     * de un paquete libre (no asociado a un producto).
     */
    private static class CotizacionPaquete {
        String origen;
        String destino;
        PrioridadEnvio prioridad;
        double peso;
        double volumen;
        double tarifa;
    }

    /**
     * Lee los campos de origen, destino, peso, volumen y prioridad del paquete libre,
     * valida sus valores y llama a {@link CotizadorEnvioService#cotizar(String, String, double, double, PrioridadEnvio)}.
     *
     * @return la {@link CotizacionPaquete} resultante o {@code null} si hubo errores de validación.
     */
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

    /**
     * Cotiza un paquete libre usando los campos de origen, destino, peso,
     * volumen y prioridad, y muestra el resultado en pantalla y en
     * {@link #lblResumenPaquete}.
     */
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

    /**
     * Toma la cotización del paquete libre y lo agrega al carrito como un
     * "producto virtual" que representa el costo del envío.
     * <p>
     * Esto permite que el costo de envío viaje en el mismo flujo de pedido que los productos.
     * </p>
     */
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

    // ================== MAPA: HANDLERS FXML ==================

    /**
     * Handler genérico usado en el FXML (por ejemplo onAction="#onConfirmarDireccion")
     * para abrir la ventana de selección de dirección en mapa.
     */
    @FXML
    private void onConfirmarDireccion() {
        abrirMapa();
    }

    /**
     * Handler alternativo para el botón de mapa (por ejemplo onAction="#onSeleccionarMapa").
     * Internamente delega en {@link #abrirMapa()}.
     */
    @FXML
    private void onSeleccionarMapa() {
        // Si en el FXML usas este nombre, también funcionará:
        abrirMapa();
    }

    /**
     * Abre la ventana modal del mapa ({@code mapa.fxml}) y registra este controlador
     * como callback para recibir la dirección seleccionada.
     */
    private void abrirMapa() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("mapa.fxml"));
            Parent root = loader.load();

            MapaViewController controller = loader.getController();
            controller.setCallbackController(this);

            Stage dialog = new Stage();
            dialog.setTitle("Seleccionar dirección en mapa");
            dialog.initModality(Modality.APPLICATION_MODAL);

            // Ventana "hija" de la actual
            Stage owner = (Stage) botonVolver.getScene().getWindow();
            dialog.initOwner(owner);

            dialog.setScene(new Scene(root, 800, 600));
            dialog.centerOnScreen();
            dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el mapa para seleccionar dirección.");
        }
    }

    /**
     * Método llamado por {@link MapaViewController} cuando el usuario confirma la dirección.
     *
     * @param lat   latitud seleccionada en el mapa.
     * @param lng   longitud seleccionada en el mapa.
     * @param texto descripción legible de la dirección.
     */
    public void establecerDireccionUsuario(double lat, double lng, String texto) {
        String valor = texto + " (lat=" + lat + ", lon=" + lng + ")";

        // Destino para envío de producto
        if (txtDestinoEnvio != null) {
            txtDestinoEnvio.setText(valor);
        }

        // Y/o destino del paquete libre
        if (txtDestinoPaquete != null) {
            txtDestinoPaquete.setText(valor);
        }
    }

    // ================= BOTÓN VOLVER =================

    /**
     * Acción del botón "Volver".
     * <p>
     * Vuelve al panel principal de Amazen ({@code amazen.fxml}) en la misma ventana.
     * </p>
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

    // ================= UTILIDADES =================

    /**
     * Lee la cantidad desde {@link #txtCantidad}.
     *
     * @return cantidad como entero; si no se puede parsear, retorna 1 por defecto.
     */
    private int leerCantidad() {
        if (txtCantidad == null) return 1;
        try {
            return Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /**
     * Muestra un cuadro de diálogo de advertencia ({@link Alert.AlertType#WARNING}).
     *
     * @param titulo   título del cuadro de diálogo.
     * @param mensaje  mensaje a mostrar.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra un cuadro de diálogo informativo ({@link Alert.AlertType#INFORMATION}).
     *
     * @param titulo   título del cuadro de diálogo.
     * @param mensaje  mensaje a mostrar.
     */
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // ================= MÉTODO PÚBLICO =================

    /**
     * Reaplica la estrategia visual del catálogo (útil si se quiere refrescar
     * la vista externa a partir de otro componente).
     */
    public void mostrarCatalogo() {
        if (estrategiaVistaCatalogo != null) {
            estrategiaVistaCatalogo.mostrarCatalogo(this);
        }
    }
}
