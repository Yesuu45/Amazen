package co.edu.uniquindio.poo.amazen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Usa ruta ABSOLUTA desde resources
        String fxml = "/co/edu/uniquindio/poo/amazen/login.fxml";
        System.out.println("[App] Cargando FXML: " + fxml);

        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml));
        Scene scene = new Scene(loader.load(), 700, 500);

        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}
