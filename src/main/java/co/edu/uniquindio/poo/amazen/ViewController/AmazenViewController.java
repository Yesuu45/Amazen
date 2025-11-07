package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Model.Strategy.*;
import co.edu.uniquindio.poo.amazen.Model.TiendaSession;
import co.edu.uniquindio.poo.amazen.Model.Persona.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AmazenViewController {

    @FXML private Button botonCatalogo;
    @FXML private Button botonCarrito;
    @FXML private Button botonHistorial;
    @FXML private Button botonEstado;
    @FXML private Button botonAdmin;
    @FXML private Button botonCerrarSesion;
    @FXML private Label tituloLabel;

    private EstrategiaVista estrategiaVista;

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

    public void setEstrategiaVista(EstrategiaVista estrategiaVista) {
        this.estrategiaVista = estrategiaVista;
        if (this.estrategiaVista != null) {
            this.estrategiaVista.configurarVista(this);
        }
    }

    // ================= MÉTODOS DE VISIBILIDAD =================
    public void actualizarTitulo(String titulo) { tituloLabel.setText(titulo); }
    public void setBotonAdminVisible(boolean visible) {
        if (botonAdmin != null) {
            botonAdmin.setVisible(visible);
            botonAdmin.setDisable(!visible);
        }
    }
    public void mostrarBotonCatalogo(boolean visible)  { botonCatalogo.setVisible(visible); }
    public void mostrarBotonCarrito(boolean visible)   { botonCarrito.setVisible(visible); }
    public void mostrarBotonHistorial(boolean visible) { botonHistorial.setVisible(visible); }
    public void mostrarBotonEstado(boolean visible)    { botonEstado.setVisible(visible); }

    // ================= NAVEGACIÓN =================
    @FXML private void irAlCatalogo()  { cambiarEscena("/co/edu/uniquindio/poo/amazen/catalogo.fxml", botonCatalogo); }
    @FXML private void irAlCarrito()   { cambiarEscena("/co/edu/uniquindio/poo/amazen/carrito.fxml", botonCarrito); }
    @FXML private void irAlHistorial() { cambiarEscena("/co/edu/uniquindio/poo/amazen/historial.fxml", botonHistorial); }
    @FXML private void irAlEstado()    { cambiarEscena("/co/edu/uniquindio/poo/amazen/estado.fxml", botonEstado); }
    @FXML private void irAGestionAdmin() { cambiarEscena("/co/edu/uniquindio/poo/amazen/Admin.fxml", botonAdmin); }

    // ================= CERRAR SESIÓN =================
    @FXML
    private void cerrarSesion() {
        try {
            TiendaSession.getInstance().cerrarSesion();

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

    private void cambiarEscena(String fxmlRuta, Button origen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlRuta));
            Parent root = loader.load();

            Stage stage = (Stage) origen.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
