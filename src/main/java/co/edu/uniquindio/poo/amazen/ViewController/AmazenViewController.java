package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.Model.Strategy.EstrategiaVista;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Controlador principal de la vista Amazen.
 * Administra la navegación entre vistas (Catálogo, Carrito, Historial, Estado)
 * y aplica el patrón Strategy para modificar dinámicamente la interfaz según
 * el tipo de usuario (por ejemplo, Cliente, Administrador, Invitado, etc.).
 */
public class AmazenViewController {

    // ============================================================
    // ELEMENTOS DE LA INTERFAZ
    // ============================================================

    @FXML
    public Button botonCatalogo;

    @FXML
    public Button botonCarrito;

    @FXML
    public Button botonHistorial;

    @FXML
    public Button botonEstado;

    @FXML
    private Label tituloLabel;

    // ============================================================
    // ATRIBUTOS DE CONTROL
    // ============================================================

    /**
     * Estrategia que define el comportamiento visual según el tipo de usuario.
     * Por ejemplo: EstrategiaVistaCliente, EstrategiaVistaAdministrador, etc.
     */
    private EstrategiaVista estrategiaVista;

    // ============================================================
    // MÉTODOS DE CONFIGURACIÓN DE ESTRATEGIA
    // ============================================================

    /**
     * Establece la estrategia de vista (se puede definir desde el login
     * o desde otro controlador según el rol del usuario).
     *
     * @param estrategiaVista implementación concreta de EstrategiaVista
     */
    public void setEstrategiaVista(EstrategiaVista estrategiaVista) {
        this.estrategiaVista = estrategiaVista;
        if (this.estrategiaVista != null) {
            this.estrategiaVista.configurarVista(this);
        }
    }

    /**
     * Permite actualizar dinámicamente el título del encabezado.
     *
     * @param titulo texto a mostrar en la etiqueta superior
     */
    public void actualizarTitulo(String titulo) {
        if (tituloLabel != null) {
            tituloLabel.setText(titulo);
        }
    }

    // ============================================================
    // MÉTODOS DE NAVEGACIÓN ENTRE ESCENAS
    // ============================================================

    @FXML
    private void irAlCatalogo() {
        cambiarEscena("/co/edu/uniquindio/poo/amazen/catalogo.fxml", botonCatalogo);
    }

    @FXML
    private void irAlCarrito() {
        cambiarEscena("/co/edu/uniquindio/poo/amazen/carrito.fxml", botonCarrito);
    }

    @FXML
    private void irAlHistorial() {
        cambiarEscena("/co/edu/uniquindio/poo/amazen/historial.fxml", botonHistorial);
    }

    @FXML
    private void irAlEstado() {
        cambiarEscena("/co/edu/uniquindio/poo/amazen/estado.fxml", botonEstado);
    }

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    /**
     * Cambia la escena actual a otra vista FXML.
     *
     * @param rutaFXML ruta completa del archivo FXML
     * @param boton    referencia a un botón de la vista actual, usada para obtener la ventana
     */
    private void cambiarEscena(String rutaFXML, Button boton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            AnchorPane root = loader.load();

            // Reaplicar la estrategia si existe
            Object controller = loader.getController();
            if (controller instanceof AmazenViewController && estrategiaVista != null) {
                ((AmazenViewController) controller).setEstrategiaVista(estrategiaVista);
            }

            // Cambiar la escena
            Stage stage = (Stage) boton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.err.println("❌ Error al cargar la vista: " + rutaFXML);
            e.printStackTrace();
        }
    }
}
