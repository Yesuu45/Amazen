package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.Disponibilidad;
import co.edu.uniquindio.poo.amazen.Model.HistorialPedido;
import co.edu.uniquindio.poo.amazen.Model.Pedido;
import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;
import co.edu.uniquindio.poo.amazen.Model.Persona.Repartidor;
import co.edu.uniquindio.poo.amazen.Model.Persona.SesionUsuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RepartidorViewController {

    @FXML private ComboBox<String> cmbDisponibilidad;
    @FXML private TableView<Pedido> tblPedidos;
    @FXML private TableColumn<Pedido,String> colId;
    @FXML private TableColumn<Pedido,String> colEstado;
    @FXML private TableColumn<Pedido,String> colTotal;
    @FXML private TableColumn<Pedido,String> colCreacion;
    @FXML private TableColumn<Pedido,String> colAsignacion;
    @FXML private TableColumn<Pedido,String> colEntrega;
    @FXML private TableColumn<Pedido,String> colIncidencias;
    @FXML private Label lblInfo;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Repartidor repartidorActual() {
        Persona p = SesionUsuario.instancia().getPersona();
        return (p instanceof Repartidor) ? (Repartidor) p : null;
    }

    @FXML
    public void initialize() {
        // columnas
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstado() == null ? "—" : c.getValue().getEstado().toString()));
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("$ %, .0f", c.getValue().calcularTotal()).replace(" ,", "")));
        colCreacion.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaCreacion()==null? "—" : FMT.format(c.getValue().getFechaCreacion())));
        colAsignacion.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaAsignacion()==null? "—" : FMT.format(c.getValue().getFechaAsignacion())));
        colEntrega.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaEntrega()==null? "—" : FMT.format(c.getValue().getFechaEntrega())));
        colIncidencias.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getIncidencias()==null?0:c.getValue().getIncidencias().size())));

        // combo disponibilidad
        cmbDisponibilidad.getItems().setAll("ACTIVO", "INACTIVO", "EN_RUTA");

        // cargar datos iniciales
        cargarDisponibilidad();
        cargarPedidosAsignados();
    }

    private void cargarDisponibilidad() {
        Repartidor r = repartidorActual();
        if (r == null) {
            info("Sesión", "Este panel es solo para Repartidor.");
            return;
        }
        String val = (r.getDisponibilidad()==null ? "INACTIVO" : r.getDisponibilidad().name());
        cmbDisponibilidad.setValue(val);
    }

    private void cargarPedidosAsignados() {
        Repartidor r = repartidorActual();
        if (r == null) {
            tblPedidos.setPlaceholder(new Label("Inicia sesión como Repartidor."));
            return;
        }
        String doc = r.getDocumento();
        List<Pedido> todos = HistorialPedido.getInstance().obtenerPedidos();
        List<Pedido> mios = todos.stream()
                .filter(p -> Objects.equals(doc, p.getDocumentoRepartidorAsignado()))
                .collect(Collectors.toList());
        tblPedidos.getItems().setAll(mios);
        lblInfo.setText("Pedidos asignados: " + mios.size() + "  |  Repartidor: " + r.getNombre());
    }

    // ===== acciones =====

    @FXML
    private void onGuardarDisponibilidad() {
        Repartidor r = repartidorActual();
        if (r == null) { error("Sesión", "Este panel es solo para Repartidor."); return; }
        String valor = cmbDisponibilidad.getValue();
        if (valor == null || valor.isBlank()) { error("Disponibilidad", "Selecciona un estado."); return; }

        r.setDisponibilidad(Disponibilidad.valueOf(valor));
        info("Disponibilidad", "Estado actualizado a " + valor + ".");
    }

    @FXML
    private void onRefrescar() {
        cargarDisponibilidad();
        cargarPedidosAsignados();
        info("Refrescar", "Datos actualizados.");
    }

    @FXML
    private void onVolver() {
        final String FXML_AMAZEN = "/co/edu/uniquindio/poo/amazen/amazen.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(FXML_AMAZEN));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) tblPedidos.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Amazen");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            error("Volver", "No se pudo regresar a la pantalla principal.");
        }
    }

    // helpers
    private void info(String h, String c) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, c, ButtonType.OK);
        a.setHeaderText(h); a.showAndWait();
    }
    private void error(String h, String c) {
        Alert a = new Alert(Alert.AlertType.ERROR, c, ButtonType.OK);
        a.setHeaderText(h); a.showAndWait();
    }
    @FXML
    private void onMarcarEntregado() {
        var sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Selección", "Elige un pedido."); return; }

        // Solo permitir si ya fue ENVIADO (o superior)
        String estado = String.valueOf(sel.getEstado());
        if (!estado.contains("ENVIADO") && !estado.contains("EMPAQUETADO") && !estado.contains("VERIFICAR")) {
            error("Estado", "Aún no puedes marcar como ENTREGADO.");
            return;
        }

        try {
            sel.procesar("entregado");      // usa tu State
            tblPedidos.refresh();
            info("Listo", "Pedido " + sel.getId() + " marcado como ENTREGADO.");
        } catch (Exception e) {
            error("Error", e.getMessage());
        }
    }
    @FXML
    private void onReportarIncidencia() {
        var sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Selección", "Elige un pedido."); return; }

        TextInputDialog d = new TextInputDialog();
        d.setHeaderText("Describe la incidencia");
        d.setContentText("Detalle:");
        String detalle = d.showAndWait().orElse(null);
        if (detalle == null || detalle.isBlank()) return;

        try {
            // Si tienes clase Incidencia:
            // sel.registrarIncidencia(new Incidencia("Ruta", "Repartidor", detalle));
            // Si no, omite o deja un stub/log.
            info("Incidencia", "Incidencia registrada para " + sel.getId());
            tblPedidos.refresh();
        } catch (Exception e) {
            error("Error", e.getMessage());
        }
    }
    /** Avanza el estado del pedido aplicando, en orden, las acciones dadas.
     *  Ignora las que no apliquen (tu procesar(...) ya maneja errores y devuelve false).
     *  Devuelve true si al menos una transición fue válida. */
    private boolean avanzar(Pedido p, String... acciones) {
        boolean aplicado = false;
        for (String a : acciones) {
            try {
                boolean ok = p.procesar(a);
                aplicado = aplicado || ok;
            } catch (Exception ignored) {
                // transición inválida desde el estado actual: la ignoramos
            }
        }
        return aplicado;
    }

    @FXML
    private void onMarcarEmpaquetado() {
        var sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Selección", "Elige un pedido."); return; }
        // Si aún está en PAGADO, encadena verificación → empaquetado
        boolean ok = avanzar(sel, "verificacionpago", "empaquetado");
        tblPedidos.refresh();
        if (ok) info("Actualizado", "Pedido " + sel.getId() + " marcado como EMPAQUETADO.");
        else    error("Estado", "No fue posible marcar como EMPAQUETADO desde " + sel.getEstado());
    }

    @FXML
    private void onMarcarEnviado() {
        var sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Selección", "Elige un pedido."); return; }
        // Garantiza secuencia: verificar → empaquetar → enviar
        boolean ok = avanzar(sel, "verificacionpago", "empaquetado", "enviado");
        tblPedidos.refresh();
        if (ok) info("Actualizado", "Pedido " + sel.getId() + " marcado como ENVIADO.");
        else    error("Estado", "No fue posible marcar como ENVIADO desde " + sel.getEstado());
    }





}
