package co.edu.uniquindio.poo.amazen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // FXML inicial (login)
        final String fxml = "login.fxml"; // mismo paquete que App
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml));

        Scene scene = new Scene(loader.load(), 700, 500);

        // 🔥 Hoja de estilos global (glassmorphism, botones, etc.)
        scene.getStylesheets().add(
                App.class.getResource("/Assets/styles.css").toExternalForm()
        );

        primaryStage.setTitle("Amazen - Login");
        primaryStage.setResizable(false);     // opcional: bloquear redimensionado
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();        // centrar en la pantalla
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
