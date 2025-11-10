package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.Model.HistorialPedido;
import co.edu.uniquindio.poo.amazen.Controller.GestorEstadosController;
import co.edu.uniquindio.poo.amazen.Controller.GestorPedidosController;
import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.Incidencia;
import co.edu.uniquindio.poo.amazen.Model.Pedido;
import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;
import co.edu.uniquindio.poo.amazen.Model.Persona.Repartidor;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminEnviosViewController {

    @FXML private TableView<Pedido> tblPedidos;
    @FXML private TableColumn<Pedido, String> colId;
    @FXML private TableColumn<Pedido, String> colEstado;
    @FXML private TableColumn<Pedido, String> colRepartidor;
    @FXML private TableColumn<Pedido, String> colTotal;
    @FXML private TableColumn<Pedido, String> colCreacion;
    @FXML private TableColumn<Pedido, String> colAsignacion;
    @FXML private TableColumn<Pedido, String> colEntrega;
    @FXML private TableColumn<Pedido, String> colIncidencias;

    @FXML private ComboBox<String> cmbRepartidores;
    @FXML private Label lblInfo;

    private final ObservableList<Pedido> pedidosView = FXCollections.observableArrayList();

    // Controladores de negocio
    private final GestorPedidosController gestorPedidos = new GestorPedidosController();
    private final GestorEstadosController gestorEstados = new GestorEstadosController();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        // Configurar columnas
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstado() == null ? "—" : c.getValue().getEstado().toString()
        ));
        colRepartidor.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDocumentoRepartidorAsignado() == null ? "—" : c.getValue().getDocumentoRepartidorAsignado()
        ));
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(String.format("$ %.2f", c.getValue().calcularTotal())));
        colCreacion.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaCreacion() == null ? "—" : FMT.format(c.getValue().getFechaCreacion())
        ));
        colAsignacion.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaAsignacion() == null ? "—" : FMT.format(c.getValue().getFechaAsignacion())
        ));
        colEntrega.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaEntrega() == null ? "—" : FMT.format(c.getValue().getFechaEntrega())
        ));
        colIncidencias.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getIncidencias() == null ? 0 : c.getValue().getIncidencias().size())
        ));

        // Cargar datos iniciales
        cargarRepartidores();
        cargarPedidos();
    }

    private void cargarRepartidores() {
        cmbRepartidores.getItems().clear();
        Amazen.getInstance().getListaPersonas().stream()
                .filter(p -> p instanceof Repartidor)
                .map(Persona::getDocumento)
                .forEach(doc -> cmbRepartidores.getItems().add(doc));
    }


    private void cargarPedidos() {
        pedidosView.setAll(HistorialPedido.getInstance().obtenerPedidos()); // <- AQUÍ el cambio
        tblPedidos.setItems(pedidosView);
        tblPedidos.setPlaceholder(new Label("No hay pedidos. Crea alguno desde el flujo de compra."));
        lblInfo.setText("Pedidos cargados: " + pedidosView.size());
    }
    // === Botones ===

    @FXML
    private void onRefrescar() {
        cargarRepartidores();
        cargarPedidos();
        info("Refrescar", "Datos actualizados.");
    }

    @FXML
    private void onAsignar() {
        Pedido sel = tblPedidos.getSelectionModel().getSelectedItem();
        String docRep = cmbRepartidores.getValue();
        if (sel == null) { error("Seleccione un pedido", "Elija un pedido en la tabla."); return; }
        if (docRep == null || docRep.isBlank()) { error("Seleccione un repartidor", "Elija un documento de repartidor."); return; }

        sel.asignarRepartidor(docRep);                     // ✅ usa tu método del modelo
        tblPedidos.refresh();
        info("Asignado", "Repartidor asignado al pedido " + sel.getId());
    }
    @FXML
    private void onReasignar() {
        Pedido sel = tblPedidos.getSelectionModel().getSelectedItem();
        String docRep = cmbRepartidores.getValue();
        if (sel == null) { error("Seleccione un pedido", "Elija un pedido en la tabla."); return; }
        if (docRep == null || docRep.isBlank()) { error("Seleccione un repartidor", "Elija un documento de repartidor."); return; }

        sel.asignarRepartidor(docRep);                     // ✅ mismo método (reasigna y marca fecha)
        tblPedidos.refresh();
        info("Reasignado", "Repartidor reasignado al pedido " + sel.getId());
    }

    @FXML private void onEstadoEmpaquetado() { cambiarEstado("empaquetado"); }
    @FXML private void onEstadoEnviado()     { cambiarEstado("enviado"); }
    @FXML private void onEstadoEntregado()   { cambiarEstado("entregado"); }

    private void cambiarEstado(String accion) {
        Pedido sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Seleccione un pedido", "Elija un pedido en la tabla."); return; }

        gestorEstados.setPedido(sel);
        if (gestorEstados.cambiarEstado(accion)) {
            tblPedidos.refresh();                          // ✅ refresca columnas Estado/Entrega
            info("Estado actualizado", "Se aplicó: " + accion.toUpperCase());
        }
    }

    @FXML
    private void onIncidencia() {
        Pedido sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Seleccione un pedido", "Elija un pedido en la tabla."); return; }

        TextInputDialog dZona = new TextInputDialog();
        dZona.setHeaderText("Zona de la incidencia");
        dZona.setContentText("Zona:");
        String zona = dZona.showAndWait().orElse(null);
        if (zona == null) return;

        TextInputDialog dTipo = new TextInputDialog();
        dTipo.setHeaderText("Tipo de incidencia");
        dTipo.setContentText("Tipo:");
        String tipo = dTipo.showAndWait().orElse(null);
        if (tipo == null) return;

        TextInputDialog dDet = new TextInputDialog();
        dDet.setHeaderText("Detalle de la incidencia");
        dDet.setContentText("Detalle:");
        String detalle = dDet.showAndWait().orElse(null);
        if (detalle == null) return;

        sel.registrarIncidencia(new Incidencia(zona, tipo, detalle)); // ✅ tu API del modelo
        tblPedidos.refresh();
        info("Incidencia registrada", "Quedó asociada al pedido " + sel.getId());
    }


    @FXML
    private void onCerrar() {
        Stage st = (Stage) tblPedidos.getScene().getWindow();
        st.close();
    }

    // === Helpers ===
    private void info(String t, String c) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, c, ButtonType.OK);
        a.setHeaderText(t); a.showAndWait();
    }
    private void error(String t, String c) {
        Alert a = new Alert(Alert.AlertType.ERROR, c, ButtonType.OK);
        a.setHeaderText(t); a.showAndWait();
    }
    @FXML
    private void onAbrirEnvios() {
        final String FXML = "/co/edu/uniquindio/poo/amazen/admin_envios.fxml";
        try {
            var loader = new javafx.fxml.FXMLLoader(getClass().getResource(FXML));
            var scene = new javafx.scene.Scene(loader.load());
            var stage = new javafx.stage.Stage();
            stage.setTitle("Gestión de Envíos (Asignación, Estados, Incidencias)");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR, "No se pudo abrir la vista de envíos", ButtonType.OK);
            a.setHeaderText("Error"); a.showAndWait();
        }
    }

}
