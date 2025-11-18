package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Model.HistorialPedido;
import co.edu.uniquindio.poo.amazen.Model.Pedido;
import co.edu.uniquindio.poo.amazen.Model.Persona.Administrador;
import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;
import co.edu.uniquindio.poo.amazen.Model.Persona.Repartidor;
import co.edu.uniquindio.poo.amazen.Model.Persona.SesionUsuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class HistorialViewController {

    @FXML private TableView<Pedido> tablaPedidos;
    @FXML private TableColumn<Pedido, String> colId;
    @FXML private TableColumn<Pedido, String> colEstado;
    @FXML private TableColumn<Pedido, String> colTotal;
    @FXML private TableColumn<Pedido, String> colCreacion;
    @FXML private TableColumn<Pedido, String> colAsignacion;
    @FXML private TableColumn<Pedido, String> colEntrega;
    @FXML private TableColumn<Pedido, String> colRepartidor;

    @FXML private Button btnVolver;
    @FXML private Button btnCancelar;
    @FXML private Label  lblUsuario;
    @FXML private Label  lblResumen;

    private final HistorialPedido historial = HistorialPedido.getInstance();
    private final ObservableList<Pedido> datos = FXCollections.observableArrayList();
    private final DateTimeFormatter fmtFechaHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private void initialize() {
        // Configurar columnas
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));

        colEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstado() == null ? "—" : c.getValue().getEstado().toString()
        ));

        colTotal.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("%.0f", c.getValue().calcularTotal())
        ));

        colCreacion.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaCreacion() == null
                        ? "—"
                        : c.getValue().getFechaCreacion().format(fmtFechaHora)
        ));

        colAsignacion.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaAsignacion() == null
                        ? "—"
                        : c.getValue().getFechaAsignacion().format(fmtFechaHora)
        ));

        colEntrega.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaEntrega() == null
                        ? "—"
                        : c.getValue().getFechaEntrega().format(fmtFechaHora)
        ));

        colRepartidor.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDocumentoRepartidorAsignado() == null
                        ? "—"
                        : c.getValue().getDocumentoRepartidorAsignado()
        ));

        tablaPedidos.setItems(datos);

        cargarPedidosUsuarioActual();
    }

    // ================== Carga de datos ==================

    private void cargarPedidosUsuarioActual() {
        Persona persona = SesionUsuario.instancia().getPersona();

        if (persona == null) {
            lblUsuario.setText("Usuario: (sin sesión)");
            datos.clear();
            lblResumen.setText("No hay sesión activa.");
            btnCancelar.setDisable(true);
            return;
        }

        lblUsuario.setText("Usuario: " + persona.getNombre() + " (" + persona.getDocumento() + ")");

        List<Pedido> todos = historial.getPedidos();
        List<Pedido> filtrados;

        // Admin o repartidor ven todo; usuario ve solo sus pedidos
        if (persona instanceof Administrador || persona instanceof Repartidor) {
            filtrados = todos;
        } else {
            filtrados = todos.stream()
                    .filter(p -> persona.getDocumento().equalsIgnoreCase(p.getDocumentoCliente()))
                    .collect(Collectors.toList());
        }

        datos.setAll(filtrados);
        lblResumen.setText(filtrados.size() + " pedido(s) encontrado(s).");
        btnCancelar.setDisable(datos.isEmpty());
    }

    // ================== Cancelar pedido ==================

    private Pedido pedidoSeleccionado() {
        Pedido p = tablaPedidos.getSelectionModel().getSelectedItem();
        if (p == null) {
            alerta(Alert.AlertType.WARNING, "Selección requerida",
                    "Debes seleccionar un pedido de la tabla.");
        }
        return p;
    }

    @FXML
    private void onCancelarPedido() {
        Pedido pedido = pedidoSeleccionado();
        if (pedido == null) return;

        // Confirmación
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Seguro que deseas cancelar el pedido " + pedido.getId() + "?\n" +
                        "Solo se puede cancelar si aún no ha sido empaquetado/enviado.",
                ButtonType.OK, ButtonType.CANCEL);
        conf.setHeaderText("Confirmar cancelación");
        if (conf.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            // Usa el patrón State (EstadoPagado / EstadoVerificarPago)
            pedido.cancelar();

            tablaPedidos.refresh();
            lblResumen.setText(datos.size() + " pedido(s) encontrado(s).");

            alerta(Alert.AlertType.INFORMATION, "Pedido cancelado",
                    "El pedido " + pedido.getId() + " ha sido cancelado correctamente.");
        } catch (Exception e) {
            alerta(Alert.AlertType.ERROR, "No se pudo cancelar",
                    "No fue posible cancelar el pedido:\n" + e.getMessage());
        }
    }

    // ================== Volver ==================

    @FXML
    private void onVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("amazen.fxml"));
            AnchorPane root = loader.load();
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            alerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo volver al panel principal.");
        }
    }

    // ================== Helper alertas ==================

    private void alerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo, mensaje, ButtonType.OK);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
