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
 * el tipo de usuario (Cliente, Administrador, Repartidor).
 */
public class AmazenViewController {

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

    private EstrategiaVista estrategiaVista;

    public void setEstrategiaVista(EstrategiaVista estrategiaVista) {
        this.estrategiaVista = estrategiaVista;
        if (this.estrategiaVista != null) {
            this.estrategiaVista.configurarVista(this);
        }
    }

    public void actualizarTitulo(String titulo) {
        if (tituloLabel != null) {
            tituloLabel.setText(titulo);
        }
    }

    // ============================================================
    // Navegación entre escenas
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

    private void cambiarEscena(String fxmlRuta, Button boton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlRuta));
            AnchorPane root = loader.load();

            // Reaplicar estrategia si se mantiene
            Object controller = loader.getController();
            if (controller instanceof AmazenViewController && estrategiaVista != null) {
                ((AmazenViewController) controller).setEstrategiaVista(estrategiaVista);
            }

            Stage stage = (Stage) boton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.err.println("Error al cambiar la escena: " + fxmlRuta);
            e.printStackTrace();
        }
    }
}
