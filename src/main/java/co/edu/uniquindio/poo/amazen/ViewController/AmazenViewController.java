package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.Model.Strategy.EstrategiaVista;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controlador principal de la vista Amazen.
 * Navega entre pantallas y aplica Strategy según el rol.
 */
public class AmazenViewController {

    @FXML public Button botonCatalogo;
    @FXML public Button botonCarrito;
    @FXML public Button botonHistorial;
    @FXML public Button botonEstado;

    // 👇 botón exclusivo de administradores
    @FXML public Button botonAdmin;

    @FXML private Label tituloLabel;

    private EstrategiaVista estrategiaVista;

    public void setEstrategiaVista(EstrategiaVista estrategiaVista) {
        this.estrategiaVista = estrategiaVista;
        if (this.estrategiaVista != null) {
            this.estrategiaVista.configurarVista(this);
        }
    }

    public void actualizarTitulo(String titulo) {
        if (tituloLabel != null) tituloLabel.setText(titulo);
    }

    /** Mostrar/ocultar el botón admin desde la estrategia */
    public void setBotonAdminVisible(boolean visible) {
        if (botonAdmin != null) {
            botonAdmin.setVisible(visible);
            botonAdmin.setDisable(!visible);
        }
    }

    // ================= Navegación =================

    @FXML private void irAlCatalogo() { cambiarEscena("/co/edu/uniquindio/poo/amazen/catalogo.fxml", botonCatalogo); }
    @FXML private void irAlCarrito()  { cambiarEscena("/co/edu/uniquindio/poo/amazen/carrito.fxml", botonCarrito); }
    @FXML private void irAlHistorial(){ cambiarEscena("/co/edu/uniquindio/poo/amazen/historial.fxml", botonHistorial); }
    @FXML private void irAlEstado()   { cambiarEscena("/co/edu/uniquindio/poo/amazen/estado.fxml", botonEstado); }

    /** 👉 abre el formulario de administración */
    @FXML private void irAGestionAdmin() {
        cambiarEscena("/co/edu/uniquindio/poo/amazen/Admin.fxml", botonAdmin);
    }

    /** Carga el FXML y cambia la escena en el mismo Stage */
    private void cambiarEscena(String fxmlRuta, Button origen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlRuta));
            Parent root = loader.load(); // ❗ Parent (no AnchorPane)

            // Si navegamos a otra vista Amazen, reinyectar estrategia
            Object ctrl = loader.getController();
            if (ctrl instanceof AmazenViewController && estrategiaVista != null) {
                ((AmazenViewController) ctrl).setEstrategiaVista(estrategiaVista);
            }

            Stage stage = (Stage) origen.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("[Amazen] Error al cambiar a: " + fxmlRuta);
            e.printStackTrace();
        }
    }
}
