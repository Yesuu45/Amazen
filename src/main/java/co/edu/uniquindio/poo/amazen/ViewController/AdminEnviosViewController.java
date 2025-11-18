package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.Pedido;
import co.edu.uniquindio.poo.amazen.Model.Persona.Repartidor;
import co.edu.uniquindio.poo.amazen.Model.DTO.DtoMapper;
import co.edu.uniquindio.poo.amazen.Service.ExportCsvService;
import co.edu.uniquindio.poo.amazen.Model.ExportarArchivo;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AdminEnviosViewController {

    // Toolbar
    @FXML private Button btnRefrescar;
    @FXML private ComboBox<Repartidor> cmbRepartidores;

    // Tabla
    @FXML private TableView<Pedido> tblPedidos;
    @FXML private TableColumn<Pedido, String> colId;
    @FXML private TableColumn<Pedido, String> colEstado;
    @FXML private TableColumn<Pedido, String> colRepartidor;
    @FXML private TableColumn<Pedido, Number> colTotal;
    @FXML private TableColumn<Pedido, String> colCreacion;
    @FXML private TableColumn<Pedido, String> colAsignacion;
    @FXML private TableColumn<Pedido, String> colEntrega;
    @FXML private TableColumn<Pedido, Number> colIncidencias;

    // Bottom
    @FXML private Label lblInfo;

    private final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        // ==== columnas ====
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colEstado.setCellValueFactory(c -> {
            var est = c.getValue().getEstado();
            return new SimpleStringProperty(est == null ? "—" : est.toString());
        });

        colRepartidor.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDocumentoRepartidorAsignado() == null ? "—" : c.getValue().getDocumentoRepartidorAsignado()
                )
        );

        colTotal.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().calcularTotal()));

        colCreacion.setCellValueFactory(c -> new SimpleStringProperty(format(c.getValue().getFechaCreacion())));
        colAsignacion.setCellValueFactory(c -> new SimpleStringProperty(format(c.getValue().getFechaAsignacion())));
        colEntrega.setCellValueFactory(c -> new SimpleStringProperty(format(c.getValue().getFechaEntrega())));

        colIncidencias.setCellValueFactory(c -> {
            int n = 0;
            try { n = c.getValue().getIncidencias() == null ? 0 : c.getValue().getIncidencias().size(); }
            catch (Throwable ignore) {}
            return new ReadOnlyObjectWrapper<>(n);
        });

        // ==== datos ====
        cargarRepartidores();
        cargarPedidos();

        // UX
        tblPedidos.setPlaceholder(new Label("No hay pedidos para mostrar"));
        setInfo("Listo.");
    }

    // ====== accxiones toolbar ======

    @FXML
    private void onRefrescar() {
        cargarRepartidores();
        cargarPedidos();
        setInfo("Refrescado.");
    }

    @FXML
    private void onAsignar() {
        var sel = seleccionado();
        if (sel == null) return;
        var rep = cmbRepartidores.getValue();
        if (rep == null) { warn("Selecciona un repartidor."); return; }
        try {
            sel.asignarRepartidor(rep.getDocumento());
            tblPedidos.refresh();
            setInfo("Asignado repartidor " + rep.getDocumento() + " a " + sel.getId());
        } catch (Exception e) {
            error("No se pudo asignar: " + e.getMessage());
        }
    }

    @FXML
    private void onReasignar() {
        onAsignar();
    }

    @FXML
    private void onEstadoEmpaquetado() {
        var sel = seleccionado();
        if (sel == null) return;
        try {
            sel.empaquetar(); // usa tu State
            tblPedidos.refresh();
            setInfo("Pedido " + sel.getId() + " → Empaquetado");
        } catch (Exception e) {
            error("No se pudo cambiar estado: " + e.getMessage());
        }
    }

    @FXML
    private void onEstadoEnviado() {
        var sel = seleccionado();
        if (sel == null) return;
        try {
            sel.enviar();
            tblPedidos.refresh();
            setInfo("Pedido " + sel.getId() + " → Enviado");
        } catch (Exception e) {
            error("No se pudo cambiar estado: " + e.getMessage());
        }
    }

    @FXML
    private void onEstadoEntregado() {
        var sel = seleccionado();
        if (sel == null) return;
        try {
            sel.entregar();
            tblPedidos.refresh();
            setInfo("Pedido " + sel.getId() + " → Entregado");
        } catch (Exception e) {
            error("No se pudo cambiar estado: " + e.getMessage());
        }
    }

    @FXML
    private void onExportarTxt() {
        var sel = seleccionado();
        if (sel == null) return;
        try {
            Path out = Path.of("reportes", "pedido_" + sel.getId() + ".txt");
            Files.createDirectories(out.getParent());
            ExportarArchivo.exportarPedido(sel, out.toString());
            info("TXT generado en:\n" + out.toAbsolutePath());
        } catch (Exception e) {
            error("Exportación TXT: " + e.getMessage());
        }
    }

    @FXML
    private void onExportarCsv() {
        try {
            var pedidos = tblPedidos.getItems();
            var dtos = pedidos.stream()
                    .map((Pedido p) -> DtoMapper.toDTO(p))  // evita ambigüedad con DetallePedido
                    .toList();

            Path out = Path.of("reportes", "pedidos.csv");
            Files.createDirectories(out.getParent());
            ExportCsvService.exportPedidos(out, dtos);
            info("CSV generado en:\n" + out.toAbsolutePath());
        } catch (Exception e) {
            error("Exportación CSV: " + e.getMessage());
        }
    }

    @FXML
    private void onIncidencia() {
        var sel = seleccionado();
        if (sel == null) return;

        TextInputDialog d = new TextInputDialog();
        d.setHeaderText("Registrar incidencia");
        d.setContentText("Describe la incidencia:");
        var txt = d.showAndWait().orElse(null);
        if (txt == null || txt.isBlank()) return;

        try {
            // Intentamos construir co.edu.uniquindio.poo.amazen.Model.Incidencia(String, LocalDateTime)
            // Si tu clase Incidencia tiene otra firma, ajústala o captura y notifica.
            Object inc;
            try {
                var clazz = Class.forName("co.edu.uniquindio.poo.amazen.Model.Incidencia");
                try {
                    var ctor = clazz.getConstructor(String.class, LocalDateTime.class);
                    inc = ctor.newInstance(txt.trim(), LocalDateTime.now());
                } catch (NoSuchMethodException ex) {
                    var ctor2 = clazz.getConstructor(String.class);
                    inc = ctor2.newInstance(txt.trim());
                }
            } catch (Exception reflect) {
                error("No se pudo crear la incidencia. Ajusta el constructor de Incidencia (String [, LocalDateTime]).");
                return;
            }

            // Llama a registrarIncidencia(Incidencia)
            sel.registrarIncidencia((co.edu.uniquindio.poo.amazen.Model.Incidencia) inc);
            tblPedidos.refresh();
            setInfo("Incidencia registrada para " + sel.getId());
        } catch (Exception e) {
            error("No se pudo registrar la incidencia: " + e.getMessage());
        }
    }

    @FXML
    private void onCerrar() {
        Stage st = (Stage) ((Node) lblInfo).getScene().getWindow();
        st.close();
    }

    // ====== util ======

    private void cargarPedidos() {
        List<Pedido> pedidos;
        try {
            pedidos = Amazen.getInstance().getListaPedidos();
        } catch (Throwable t) {
            pedidos = List.of();
        }
        // orden opcional: recientes primero
        pedidos = pedidos.stream()
                .sorted(Comparator.comparing(Pedido::getFechaCreacion,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());

        tblPedidos.setItems(FXCollections.observableArrayList(pedidos));
        tblPedidos.refresh();
    }

    private void cargarRepartidores() {
        try {
            var personas = Amazen.getInstance().getListaPersonas();
            var reps = personas.stream()
                    .filter(p -> p instanceof Repartidor)
                    .map(p -> (Repartidor) p)
                    .sorted(Comparator.comparing(Repartidor::getDocumento))
                    .toList();

            cmbRepartidores.setItems(FXCollections.observableArrayList(reps));
            // cell factory para mostrar Nombre Apellido (doc)
            cmbRepartidores.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(Repartidor item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : labelRep(item));
                }
            });
            cmbRepartidores.setCellFactory(list -> new ListCell<>() {
                @Override protected void updateItem(Repartidor item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : labelRep(item));
                }
            });
        } catch (Throwable t) {
            cmbRepartidores.setItems(FXCollections.observableArrayList());
        }
    }

    private String labelRep(Repartidor r) {
        String nom = r.getNombre() == null ? "" : r.getNombre();
        String ape = r.getApellido() == null ? "" : r.getApellido();
        String doc = r.getDocumento() == null ? "—" : r.getDocumento();
        return (nom + " " + ape + " (" + doc + ")").trim();
    }

    private Pedido seleccionado() {
        var sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) warn("Selecciona un pedido de la tabla.");
        return sel;
    }

    private String format(LocalDateTime dt) {
        return dt == null ? "" : FMT.format(dt);
    }

    private void setInfo(String msg) {
        if (lblInfo != null) lblInfo.setText(msg);
    }

    private void info(String msg) {
        alert(Alert.AlertType.INFORMATION, "Información", msg);
    }

    private void warn(String msg) {
        alert(Alert.AlertType.WARNING, "Atención", msg);
    }

    private void error(String msg) {
        alert(Alert.AlertType.ERROR, "Error", msg);
    }

    private void alert(Alert.AlertType t, String header, String msg) {
        var a = new Alert(t, msg, ButtonType.OK);
        a.setHeaderText(header);
        a.showAndWait();
    }
}
