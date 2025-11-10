package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.App;
import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.DTO.ConfirmacionEntregaDTO;
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
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstado() == null ? "—" : c.getValue().getEstado().toString()));

        colTotal.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("$ %, .0f", c.getValue().calcularTotal()).replace(" ,", "")));

        colCreacion.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaCreacion()==null? "—" : FMT.format(c.getValue().getFechaCreacion())));

        colAsignacion.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaAsignacion()==null? "—" : FMT.format(c.getValue().getFechaAsignacion())));

        // Muestra fecha y receptor si existe DTO de entrega
        colEntrega.setCellValueFactory(c -> {
            var f = c.getValue().getFechaEntrega();
            var dto = c.getValue().getConfirmacionEntrega();
            if (f == null && dto == null) return new SimpleStringProperty("—");
            String base = (f != null ? FMT.format(f) : (dto != null ? FMT.format(dto.fecha()) : "—"));
            if (dto != null && dto.receptorNombre() != null && !dto.receptorNombre().isBlank()) {
                base = base + " · " + dto.receptorNombre();
            }
            return new SimpleStringProperty(base);
        });

        colIncidencias.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getIncidencias()==null?0:c.getValue().getIncidencias().size())));

        cmbDisponibilidad.getItems().setAll("ACTIVO", "INACTIVO", "EN_RUTA");

        cargarDisponibilidad();
        cargarPedidosAsignados();
    }

    private void cargarDisponibilidad() {
        Repartidor r = repartidorActual();
        if (r == null) { info("Sesión", "Este panel es solo para Repartidor."); return; }
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
        List<Pedido> mios = HistorialPedido.getInstance().obtenerPedidos().stream()
                .filter(p -> Objects.equals(doc, p.getDocumentoRepartidorAsignado()))
                .collect(Collectors.toList());
        tblPedidos.getItems().setAll(mios);
        lblInfo.setText("Pedidos asignados: " + mios.size() + "  |  Repartidor: " + r.getNombre());
    }

    // ===== Acciones =====

    @FXML
    private void onGuardarDisponibilidad() {
        Repartidor r = repartidorActual();
        if (r == null) { error("Sesión", "Este panel es solo para Repartidor."); return; }
        String valor = cmbDisponibilidad.getValue();
        if (valor == null || valor.isBlank()) { error("Disponibilidad", "Selecciona un estado."); return; }
        r.setDisponibilidad(Disponibilidad.valueOf(valor));
        info("Disponibilidad", "Estado actualizado a " + valor + ".");
    }

    @FXML private void onRefrescar() { cargarDisponibilidad(); cargarPedidosAsignados(); }

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

    // ===== Estados (el repartidor puede hacer todo el flujo) =====

    /** Ejecuta, en orden, las acciones dadas. Ignora las no válidas. */
    private boolean avanzar(Pedido p, String... acciones) {
        boolean aplicado = false;
        for (String a : acciones) {
            try {
                boolean ok = p.procesar(a);
                aplicado = aplicado || ok;
            } catch (Exception ignored) { }
        }
        return aplicado;
    }

    @FXML
    private void onMarcarEmpaquetado() {
        var sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Selección", "Elige un pedido."); return; }
        boolean ok = avanzar(sel, "verificacionpago", "empaquetado");
        tblPedidos.refresh();
        if (ok) info("Actualizado", "Pedido " + sel.getId() + " → EMPAQUETADO.");
        else    error("Estado", "No fue posible marcar como EMPAQUETADO desde " + sel.getEstado());
    }

    @FXML
    private void onMarcarEnviado() {
        var sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Selección", "Elige un pedido."); return; }
        boolean ok = avanzar(sel, "verificacionpago", "empaquetado", "enviado");
        tblPedidos.refresh();
        if (ok) info("Actualizado", "Pedido " + sel.getId() + " → ENVIADO.");
        else    error("Estado", "No fue posible marcar como ENVIADO desde " + sel.getEstado());
    }

    @FXML
    private void onMarcarEntregado() {
        var sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Selección", "Elige un pedido."); return; }

        // Capturar prueba de entrega
        TextInputDialog d1 = new TextInputDialog();
        d1.setHeaderText("Nombre del receptor");
        d1.setContentText("Nombre:");
        String receptor = d1.showAndWait().orElse(null);
        if (receptor == null || receptor.isBlank()) return;

        TextInputDialog d2 = new TextInputDialog();
        d2.setHeaderText("Documento del receptor");
        d2.setContentText("Documento:");
        String docRec = d2.showAndWait().orElse("");

        TextInputDialog d3 = new TextInputDialog();
        d3.setHeaderText("Observaciones de entrega");
        d3.setContentText("Notas:");
        String obs = d3.showAndWait().orElse("");

        // Garantizar secuencia completa
        avanzar(sel, "verificacionpago", "empaquetado", "enviado", "entregado");

        // Guardar DTO (prueba de entrega)
        ConfirmacionEntregaDTO dto = new ConfirmacionEntregaDTO(
                sel.getId(), receptor, docRec, obs, java.time.LocalDateTime.now()
        );
        sel.confirmarEntrega(dto);

        tblPedidos.refresh();
        info("Entregado", "Pedido " + sel.getId() + " entregado a " + receptor + ".");
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
        // Si tienes clase Incidencia, úsala:
        // sel.registrarIncidencia(new Incidencia("Ruta", "Repartidor", detalle));
        info("Incidencia", "Incidencia registrada para " + sel.getId());
        tblPedidos.refresh();
    }

    @FXML
    private void onExportarTxt() {
        var sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Selección", "Elige un pedido."); return; }
        try {
            co.edu.uniquindio.poo.amazen.Model.ExportarArchivo
                    .exportarPedido(sel, "reportes/repartidor_pedido_" + sel.getId() + ".txt");
            info("Exportación", "TXT generado para " + sel.getId());
        } catch (Exception e) {
            error("Exportación TXT", e.getMessage());
        }
    }

    @FXML
    private void onExportarCsv() {
        try {
            var dtos = tblPedidos.getItems().stream()
                    .map(co.edu.uniquindio.poo.amazen.Model.DTO.DtoMapper::toDTO) // Pedido -> PedidoDTO
                    .toList();

            var out = java.nio.file.Paths.get("reportes", "mis_pedidos.csv");
            java.nio.file.Files.createDirectories(out.getParent());

            co.edu.uniquindio.poo.amazen.Service.ExportCsvService.exportPedidos(out, dtos);

            info("Exportación", "CSV generado en: " + out.toAbsolutePath());
        } catch (Exception e) {
            error("Exportación CSV", String.valueOf(e.getMessage()));
        }
    }



    // ===== Helpers =====
    private void info(String h, String c) { Alert a = new Alert(Alert.AlertType.INFORMATION, c, ButtonType.OK); a.setHeaderText(h); a.showAndWait(); }
    private void error(String h, String c) { Alert a = new Alert(Alert.AlertType.ERROR, c, ButtonType.OK); a.setHeaderText(h); a.showAndWait(); }
}
