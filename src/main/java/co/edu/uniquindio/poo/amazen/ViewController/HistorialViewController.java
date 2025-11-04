package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Controller.HistorialController;
import co.edu.uniquindio.poo.amazen.Model.Pedido;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HistorialViewController {
    @FXML
    private TableView<Pedido> tablaPedidos;
    @FXML private TableColumn<Pedido, String> columnaId;
    @FXML private TableColumn<Pedido, String> columnaEstado;
    @FXML private TableColumn<Pedido, Double> columnaTotal;

    @FXML private Button botonVolver;
    @FXML private Button botonExportar;

    private ObservableList<Pedido> datos;
    private HistorialController historialController;

    @FXML
    private void initialize() {
        historialController = new HistorialController();

        // Configuración de columnas
        columnaId.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getId())
        );

        columnaEstado.setCellValueFactory(cellData -> {
            // Convertimos el estado del pedido a String usando el método toString de cada Estado
            if (cellData.getValue().getEstado() != null) {
                return new SimpleStringProperty(cellData.getValue().getEstado().toString());
            } else {
                return new SimpleStringProperty("SIN ESTADO");
            }
        });

        columnaTotal.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().calcularTotal()).asObject()
        );

        // Cargamos los pedidos en la tabla
        datos = FXCollections.observableArrayList(historialController.obtenerPedidos());
        tablaPedidos.setItems(datos);
    }

    @FXML
    private void exportarPedido() {
        Pedido seleccionado = tablaPedidos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un pedido", "Debes elegir un pedido para exportar.");
            return;
        }
        try {
            String ruta = "pedidos_exportados/" + seleccionado.getId() + ".txt";
            historialController.exportarPedido(seleccionado, ruta);
            mostrarInfo("Exportado", "Archivo generado en: " + ruta);
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo exportar el pedido: " + e.getMessage());
        }
    }

    @FXML
    private void onVolver() {
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

    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarInfo(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
