package co.edu.uniquindio.poo.amazen;

import co.edu.uniquindio.poo.amazen.Model.Direccion;
import co.edu.uniquindio.poo.amazen.ViewController.CatalogoViewController;
import co.edu.uniquindio.poo.amazen.ViewController.MapaViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/co/edu/uniquindio/poo/amazen/login.fxml"));
        Scene scene = new Scene(loader.load(), 700, 500);

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Muestra la ventana del mapa.
     * @param catalogoController Controlador que recibirá la dirección seleccionada.
     * @param origen Dirección de origen (ej. tienda)
     * @param destino Dirección inicial de destino (puede ser null)
     */
    public static void mostrarMapa(CatalogoViewController catalogoController, Direccion origen, Direccion destino) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/co/edu/uniquindio/poo/amazen/mapa.fxml"));
            AnchorPane root = loader.load();

            MapaViewController mapaController = loader.getController();
            mapaController.setCallbackController(catalogoController);
            mapaController.setOrigen(origen);
            mapaController.setDestino(destino);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Seleccionar Dirección");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

