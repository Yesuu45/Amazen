package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Model.Persona.Administrador;
import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;
import co.edu.uniquindio.poo.amazen.Model.Persona.Repartidor;
import co.edu.uniquindio.poo.amazen.Model.Persona.Usuario;
import co.edu.uniquindio.poo.amazen.Model.Strategy.EstrategiaAdmin;
import co.edu.uniquindio.poo.amazen.Model.Strategy.EstrategiaRepartidor;
import co.edu.uniquindio.poo.amazen.Model.Strategy.EstrategiaVista;
import co.edu.uniquindio.poo.amazen.Model.Strategy.EsteategiaUsuario;
import co.edu.uniquindio.poo.amazen.Model.TiendaSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controlador principal del panel Amazen (vista raíz tras el login).
 * <p>
 * Esta clase:
 * <ul>
 *     <li>Detecta el tipo de {@link Persona} logueada (Administrador, Usuario, Repartidor).</li>
 *     <li>Aplica una {@link EstrategiaVista} distinta según el rol (patrón Strategy).</li>
 *     <li>Controla la navegación hacia catálogos, carrito, historial, estado de pedidos,
 *     panel administrador, panel repartidor y gestión de perfil.</li>
 *     <li>Permite cerrar la sesión y volver a la pantalla de login.</li>
 * </ul>
 */
public class AmazenViewController {

    // ========= BOTONES PRINCIPALES DEL MENÚ =========

    /** Botón para ir al catálogo de productos. */
    @FXML private Button botonCatalogo;
    /** Botón para ir al carrito de compras. */
    @FXML private Button botonCarrito;
    /** Botón para ver el historial de pedidos. */
    @FXML private Button botonHistorial;
    /** Botón para ver el estado de los pedidos (seguimiento). */
    @FXML private Button botonEstado;
    /** Botón para que el repartidor acceda a su panel de envíos. */
    @FXML private Button botonMisEnvios;
    /** Botón que abre el panel de administración (solo visible para administradores). */
    @FXML private Button botonAdmin;
    /** Botón para cerrar sesión y volver a la vista de login. */
    @FXML private Button botonCerrarSesion;
    /** Botón para ir a la vista de gestión de perfil del usuario actual. */
    @FXML private Button botonGestionPerfil;

    /** Etiqueta con el título del panel (cambia según el rol/estrategia). */
    @FXML private Label tituloLabel;

    /** Estrategia de configuración de la vista según el rol (patrón Strategy). */
    private EstrategiaVista estrategiaVista;

    /**
     * Método llamado automáticamente por JavaFX al cargar el FXML.
     * <p>
     * Obtiene la {@link Persona} activa de {@link TiendaSession} y selecciona
     * la estrategia adecuada:
     * <ul>
     *     <li>{@link EstrategiaAdmin} para administradores.</li>
     *     <li>{@link EsteategiaUsuario} para usuarios finales.</li>
     *     <li>{@link EstrategiaRepartidor} para repartidores.</li>
     * </ul>
     */
    @FXML
    public void initialize() {
        Persona persona = TiendaSession.getInstance().getPersonaActiva();
        if (persona instanceof Administrador) {
            setEstrategiaVista(new EstrategiaAdmin());
        } else if (persona instanceof Usuario) {
            setEstrategiaVista(new EsteategiaUsuario());
        } else if (persona instanceof Repartidor) {
            setEstrategiaVista(new EstrategiaRepartidor());
        }
    }

    /**
     * Asigna la estrategia a usar para configurar la vista y la ejecuta inmediatamente.
     *
     * @param estrategiaVista implementación concreta de {@link EstrategiaVista}
     */
    public void setEstrategiaVista(EstrategiaVista estrategiaVista) {
        this.estrategiaVista = estrategiaVista;
        if (this.estrategiaVista != null) {
            this.estrategiaVista.configurarVista(this);
        }
    }

    // ================= MÉTODOS DE VISIBILIDAD =================

    /**
     * Actualiza el texto del título principal del panel.
     *
     * @param titulo nuevo título a mostrar
     */
    public void actualizarTitulo(String titulo) {
        tituloLabel.setText(titulo);
    }

    /**
     * Muestra u oculta el botón de administración, además de habilitarlo/deshabilitarlo.
     *
     * @param visible {@code true} para mostrarlo, {@code false} para ocultarlo
     */
    public void setBotonAdminVisible(boolean visible) {
        if (botonAdmin != null) {
            botonAdmin.setVisible(visible);
            botonAdmin.setDisable(!visible);
        }
    }

    /** Controla la visibilidad del botón Catálogo. */
    public void mostrarBotonCatalogo(boolean visible)  { botonCatalogo.setVisible(visible); }
    /** Controla la visibilidad del botón Carrito. */
    public void mostrarBotonCarrito(boolean visible)   { botonCarrito.setVisible(visible); }
    /** Controla la visibilidad del botón Historial. */
    public void mostrarBotonHistorial(boolean visible) { botonHistorial.setVisible(visible); }
    /** Controla la visibilidad del botón Estado. */
    public void mostrarBotonEstado(boolean visible)    { botonEstado.setVisible(visible); }
    /** Controla la visibilidad del botón Mis Envíos (panel repartidor). */
    public void mostrarBotonMisEnvios(boolean visible) { botonMisEnvios.setVisible(visible); }
    /** Controla la visibilidad del botón Gestión de Perfil. */
    public void mostrarBotonGestionPerfil(boolean visible) { botonGestionPerfil.setVisible(visible); }

    // ================= NAVEGACIÓN (USUARIO / CLIENTE) =================

    /**
     * Navega a la vista de catálogo (catalogo.fxml).
     */
    @FXML private void irAlCatalogo()  { cambiarEscena("catalogo.fxml", botonCatalogo); }

    /**
     * Navega a la vista de carrito (carrito.fxml).
     */
    @FXML private void irAlCarrito()   { cambiarEscena("carrito.fxml", botonCarrito); }

    /**
     * Navega a la vista de historial de pedidos (historial.fxml).
     */
    @FXML private void irAlHistorial() { cambiarEscena("historial.fxml", botonHistorial); }

    /**
     * Navega a la vista de estado de pedido/envío (estado.fxml).
     */
    @FXML private void irAlEstado()    { cambiarEscena("estado.fxml", botonEstado); }

    // ================= PANEL ADMINISTRADOR =================

    /**
     * Acción del botón Admin.
     * <p>
     * Abre el panel de administración (admin.fxml) en una nueva ventana
     * y cierra la ventana actual de Amazen.
     *
     * @param event evento de acción disparado desde el botón
     */
    @FXML
    private void irAGestionAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("admin.fxml"));
            Scene scene = new Scene(loader.load(), 1100, 700);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Panel Administrador - Amazen");
            stage.centerOnScreen();
            stage.show();

            // Cerrar la ventana actual
            Stage actual = (Stage) botonAdmin.getScene().getWindow();
            actual.close();

        } catch (Exception e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR,
                    "No se pudo abrir el panel administrador.",
                    javafx.scene.control.ButtonType.OK);
            a.setHeaderText("Error");
            a.showAndWait();
        }
    }

    // ================= GESTIÓN DE PERFIL =================

    /**
     * Navega a la vista de Gestión de Perfil (GestionUsuarios.fxml) en la misma ventana.
     * <p>
     * Permite al usuario editar sus propios datos sin pasar por el panel completo de administración.
     */
    @FXML
    private void irGestionPerfil() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("GestionUsuario.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) botonGestionPerfil.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionar Perfil");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "No se pudo abrir la vista de gestionar perfil",
                    javafx.scene.control.ButtonType.OK);
            alert.setHeaderText("Error");
            alert.showAndWait();
        }
    }

    // ================= CERRAR SESIÓN =================

    /**
     * Cierra la sesión del usuario actual (a través de {@link TiendaSession})
     * y vuelve a la vista de login (login.fxml) en la misma ventana.
     */
    @FXML
    private void cerrarSesion() {
        try {
            // Limpiar la sesión de la tienda
            TiendaSession.getInstance().cerrarSesion();

            // Volver a login
            FXMLLoader loader = new FXMLLoader(App.class.getResource("login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) botonCerrarSesion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login Amazen");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= CAMBIO DE ESCENA BÁSICO (UTIL) =================

    /**
     * Cambia la escena actual a otro FXML dentro de la misma ventana.
     * <p>
     * Se usa para navegación entre vistas "simples" (catálogo, carrito, historial, etc.).
     *
     * @param fxmlNombre nombre del archivo FXML (en el mismo paquete de recursos que {@link App})
     * @param origen     botón desde el cual se obtiene la ventana actual
     */
    private void cambiarEscena(String fxmlNombre, Button origen) {
        try {
            // Carga el FXML desde el mismo paquete de recursos que App
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlNombre));
            Parent root = loader.load();

            Stage stage = (Stage) origen.getScene().getWindow();

            // Forzamos un tamaño "cómodo" para todas las pantallas
            Scene scene = new Scene(root, 1100, 700);
            stage.setScene(scene);

            // Límites mínimos para que no se deforme demasiado al redimensionar
            stage.setMinWidth(900);
            stage.setMinHeight(600);

            // Centrar siempre en la pantalla
            stage.centerOnScreen();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= PANEL REPARTIDOR =================

    /**
     * Navega al panel del repartidor (repartidor.fxml) en la misma ventana.
     * <p>
     * Este panel permite al repartidor gestionar sus envíos, ver rutas y estados.
     */
    @FXML
    private void irPanelRepartidor() {
        final String FXML = "repartidor.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(FXML));
            Scene scene  = new Scene(loader.load());
            Stage stage  = (Stage) botonMisEnvios.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panel del Repartidor");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR,
                    "No se pudo abrir el panel del repartidor",
                    javafx.scene.control.ButtonType.OK);
            a.setHeaderText("Error");
            a.showAndWait();
        }
    }
}
