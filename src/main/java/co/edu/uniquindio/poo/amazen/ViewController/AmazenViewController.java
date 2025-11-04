package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AmazenViewController {
    @FXML
    private Button botonCatalogo;

    @FXML
    private Button botonCarrito;

    @FXML
    private Button botonHistorial;

    @FXML
    private Button botonEstado;

    @FXML
    private void irAlCatalogo() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("catalogo.fxml"));
            AnchorPane root = loader.load();
            Stage stage = (Stage) botonCatalogo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void irAlCarrito() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("carrito.fxml"));
            AnchorPane root = loader.load();
            Stage stage = (Stage) botonCarrito.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void irAlHistorial() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("historial.fxml"));
            AnchorPane root = loader.load();
            Stage stage = (Stage) botonHistorial.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void irAlEstado() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("estado.fxml"));
            AnchorPane root = loader.load();
            Stage stage = (Stage) botonEstado.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
