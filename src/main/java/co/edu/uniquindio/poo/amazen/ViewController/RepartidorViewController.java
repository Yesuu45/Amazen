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

/**
 * Controlador del panel de Repartidor.
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 *     <li>Cargar y mostrar los pedidos asignados al repartidor en sesión.</li>
 *     <li>Permitir al repartidor actualizar su {@link Disponibilidad} (ACTIVO, INACTIVO, EN_RUTA).</li>
 *     <li>Permitir avanzar el flujo de estado del pedido usando el patrón State:
 *         verificación de pago, empaquetado, enviado y entregado.</li>
 *     <li>Registrar confirmación de entrega usando {@link ConfirmacionEntregaDTO}.</li>
 *     <li>Exportar la información de los pedidos a TXT/CSV.</li>
 *     <li>Regresar al panel principal (Amazen) cuando el repartidor lo requiera.</li>
 * </ul>
 *
 * <p>Se apoya en:</p>
 * <ul>
 *     <li>{@link SesionUsuario} para obtener el repartidor autenticado.</li>
 *     <li>{@link HistorialPedido} como fuente centralizada de pedidos.</li>
 *     <li>Método {@link Pedido#procesar(String)} para ejecutar transiciones del patrón State.</li>
 * </ul>
 */
public class RepartidorViewController {

    // =================== CONTROLES FXML ===================

    /** Combo para seleccionar y mostrar la disponibilidad actual del repartidor. */
    @FXML private ComboBox<String> cmbDisponibilidad;

    /** Tabla con los pedidos asignados al repartidor. */
    @FXML private TableView<Pedido> tblPedidos;

    /** Columna que muestra el ID del pedido. */
    @FXML private TableColumn<Pedido,String> colId;

    /** Columna que muestra el estado actual del pedido (toString del estado State). */
    @FXML private TableColumn<Pedido,String> colEstado;

    /** Columna que muestra el total del pedido formateado. */
    @FXML private TableColumn<Pedido,String> colTotal;

    /** Columna que muestra la fecha de creación del pedido. */
    @FXML private TableColumn<Pedido,String> colCreacion;

    /** Columna que muestra la fecha de asignación del pedido al repartidor. */
    @FXML private TableColumn<Pedido,String> colAsignacion;

    /**
     * Columna que muestra la fecha de entrega y, si existe,
     * el nombre del receptor registrado en {@link ConfirmacionEntregaDTO}.
     */
    @FXML private TableColumn<Pedido,String> colEntrega;

    /** Columna que muestra el número de incidencias registradas en el pedido. */
    @FXML private TableColumn<Pedido,String> colIncidencias;

    /** Etiqueta inferior para mostrar resumen (cantidad de pedidos, nombre del repartidor, etc.). */
    @FXML private Label lblInfo;

    /** Formato estándar para fechas y horas mostradas al repartidor. */
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // =================== INICIALIZACIÓN Y SESIÓN ===================

    /**
     * Obtiene el repartidor actual a partir de la {@link SesionUsuario}.
     *
     * @return el repartidor actual si la persona en sesión es instancia de {@link Repartidor},
     *         o {@code null} en caso contrario.
     */
    private Repartidor repartidorActual() {
        Persona p = SesionUsuario.instancia().getPersona();
        return (p instanceof Repartidor) ? (Repartidor) p : null;
    }

    /**
     * Inicializa la tabla, columnas y combos al cargar la vista.
     *
     * <p>Acciones:</p>
     * <ul>
     *     <li>Configura la forma en que cada columna obtiene y formatea sus datos.</li>
     *     <li>Carga la lista de valores permitidos para la disponibilidad.</li>
     *     <li>Carga la disponibilidad actual del repartidor y sus pedidos asignados.</li>
     * </ul>
     */
    @FXML
    public void initialize() {
        // Configuración de columnas
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));

        colEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstado() == null ? "—" : c.getValue().getEstado().toString()
        ));

        colTotal.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("$ %, .0f", c.getValue().calcularTotal()).replace(" ,", "")
        ));

        colCreacion.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaCreacion() == null ? "—" : FMT.format(c.getValue().getFechaCreacion())
        ));

        colAsignacion.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaAsignacion() == null ? "—" : FMT.format(c.getValue().getFechaAsignacion())
        ));

        // Fecha de entrega + receptor (si existe)
        colEntrega.setCellValueFactory(c -> {
            var f   = c.getValue().getFechaEntrega();
            var dto = c.getValue().getConfirmacionEntrega();
            if (f == null && dto == null) {
                return new SimpleStringProperty("—");
            }

            String base = (f != null)
                    ? FMT.format(f)
                    : (dto != null ? FMT.format(dto.fecha()) : "—");

            if (dto != null && dto.receptorNombre() != null && !dto.receptorNombre().isBlank()) {
                base = base + " · " + dto.receptorNombre();
            }
            return new SimpleStringProperty(base);
        });

        colIncidencias.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getIncidencias() == null ? 0 : c.getValue().getIncidencias().size())
        ));

        // Opciones de disponibilidad
        cmbDisponibilidad.getItems().setAll("ACTIVO", "INACTIVO", "EN_RUTA");

        cargarDisponibilidad();
        cargarPedidosAsignados();
    }

    /**
     * Carga el valor de disponibilidad actual del repartidor en el combo,
     * o muestra un mensaje si no se está en sesión como repartidor.
     */
    private void cargarDisponibilidad() {
        Repartidor r = repartidorActual();
        if (r == null) {
            info("Sesión", "Este panel es solo para Repartidor.");
            return;
        }
        String val = (r.getDisponibilidad() == null ? "INACTIVO" : r.getDisponibilidad().name());
        cmbDisponibilidad.setValue(val);
    }

    /**
     * Carga en la tabla únicamente los pedidos que tienen asignado
     * el documento del repartidor en sesión.
     */
    private void cargarPedidosAsignados() {
        Repartidor r = repartidorActual();
        if (r == null) {
            tblPedidos.setPlaceholder(new Label("Inicia sesión como Repartidor."));
            return;
        }

        String doc = r.getDocumento();

        List<Pedido> mios = HistorialPedido.getInstance()
                .obtenerPedidos()
                .stream()
                .filter(p -> Objects.equals(doc, p.getDocumentoRepartidorAsignado()))
                .collect(Collectors.toList());

        tblPedidos.getItems().setAll(mios);
        lblInfo.setText("Pedidos asignados: " + mios.size() + "  |  Repartidor: " + r.getNombre());
    }

    // =================== ACCIONES SOBRE DISPONIBILIDAD ===================

    /**
     * Acción del botón "Guardar disponibilidad".
     *
     * <p>Actualiza el campo {@link Disponibilidad} del repartidor en sesión
     * según el valor seleccionado en el combo.</p>
     */
    @FXML
    private void onGuardarDisponibilidad() {
        Repartidor r = repartidorActual();
        if (r == null) {
            error("Sesión", "Este panel es solo para Repartidor.");
            return;
        }
        String valor = cmbDisponibilidad.getValue();
        if (valor == null || valor.isBlank()) {
            error("Disponibilidad", "Selecciona un estado.");
            return;
        }
        r.setDisponibilidad(Disponibilidad.valueOf(valor));
        info("Disponibilidad", "Estado actualizado a " + valor + ".");
    }

    /**
     * Acción del botón "Refrescar".
     *
     * <p>Vuelve a leer la disponibilidad y la lista de pedidos asignados desde el modelo.</p>
     */
    @FXML
    private void onRefrescar() {
        cargarDisponibilidad();
        cargarPedidosAsignados();
    }

    /**
     * Acción del botón "Volver".
     *
     * <p>Regresa al panel principal de Amazen reutilizando el {@link Stage} actual.</p>
     */
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

    // =================== FLUJO DE ESTADOS (STATE) ===================

    /**
     * Ejecuta las acciones indicadas sobre el pedido, en orden,
     * utilizando {@link Pedido#procesar(String)}.
     *
     * <p>Ejemplo de uso: {@code avanzar(pedido, "verificacionpago", "empaquetado")}.</p>
     *
     * @param p        pedido sobre el que se aplican las acciones.
     * @param acciones secuencia de acciones (por ejemplo: "verificacionpago", "empaquetado", "enviado").
     * @return {@code true} si al menos una de las acciones se aplicó correctamente, {@code false} en caso contrario.
     */
    private boolean avanzar(Pedido p, String... acciones) {
        boolean aplicado = false;
        for (String a : acciones) {
            try {
                boolean ok = p.procesar(a);
                aplicado = aplicado || ok;
            } catch (Exception ignored) {
                // Ignora acciones no válidas para el estado actual
            }
        }
        return aplicado;
    }

    /**
     * Marca el pedido seleccionado como "EMPAQUETADO".
     *
     * <p>Internamente intenta asegurar que el pedido pase por "verificacionpago"
     * y luego "empaquetado", llamando a {@link #avanzar(Pedido, String...)}.</p>
     */
    @FXML
    private void onMarcarEmpaquetado() {
        var sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            error("Selección", "Elige un pedido.");
            return;
        }
        boolean ok = avanzar(sel, "verificacionpago", "empaquetado");
        tblPedidos.refresh();
        if (ok) {
            info("Actualizado", "Pedido " + sel.getId() + " → EMPAQUETADO.");
        } else {
            error("Estado", "No fue posible marcar como EMPAQUETADO desde " + sel.getEstado());
        }
    }

    /**
     * Marca el pedido seleccionado como "ENVIADO".
     *
     * <p>Para cumplir el flujo, intenta llamar a "verificacionpago", "empaquetado" y "enviado" en secuencia.</p>
     */
    @FXML
    private void onMarcarEnviado() {
        var sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            error("Selección", "Elige un pedido.");
            return;
        }
        boolean ok = avanzar(sel, "verificacionpago", "empaquetado", "enviado");
        tblPedidos.refresh();
        if (ok) {
            info("Actualizado", "Pedido " + sel.getId() + " → ENVIADO.");
        } else {
            error("Estado", "No fue posible marcar como ENVIADO desde " + sel.getEstado());
        }
    }

    /**
     * Marca el pedido seleccionado como "ENTREGADO" y registra la confirmación
     * de entrega en un {@link ConfirmacionEntregaDTO}.
     *
     * <p>Flujo:</p>
     * <ol>
     *     <li>Pide al repartidor nombre, documento y observaciones del receptor.</li>
     *     <li>Asegura la secuencia completa de estados: verificación de pago → empaquetado → enviado → entregado.</li>
     *     <li>Crea un {@link ConfirmacionEntregaDTO} con la fecha/hora actual y lo asigna al pedido.</li>
     *     <li>Refresca la tabla y muestra un mensaje de confirmación.</li>
     * </ol>
     */
    @FXML
    private void onMarcarEntregado() {
        var sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            error("Selección", "Elige un pedido.");
            return;
        }

        // 1) Capturar datos de prueba de entrega
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

        // 2) Garantizar secuencia completa de estados
        avanzar(sel, "verificacionpago", "empaquetado", "enviado", "entregado");

        // 3) Guardar DTO de confirmación de entrega
        ConfirmacionEntregaDTO dto = new ConfirmacionEntregaDTO(
                sel.getId(),
                receptor,
                docRec,
                obs,
                java.time.LocalDateTime.now()
        );
        sel.confirmarEntrega(dto);

        tblPedidos.refresh();
        info("Entregado", "Pedido " + sel.getId() + " entregado a " + receptor + ".");
    }

    /**
     * Acción para reportar una incidencia asociada al pedido seleccionado.
     *
     * <p>Actualmente solo muestra un mensaje de confirmación,
     * pero aquí se podría llamar a {@code sel.registrarIncidencia(...)} si la clase
     * {@code Incidencia} está disponible.</p>
     */
    @FXML
    private void onReportarIncidencia() {
        var sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            error("Selección", "Elige un pedido.");
            return;
        }
        TextInputDialog d = new TextInputDialog();
        d.setHeaderText("Describe la incidencia");
        d.setContentText("Detalle:");
        String detalle = d.showAndWait().orElse(null);
        if (detalle == null || detalle.isBlank()) return;

        // Punto de integración con la entidad Incidencia (si se implementa):
        // sel.registrarIncidencia(new Incidencia("Ruta", "Repartidor", detalle));

        info("Incidencia", "Incidencia registrada para " + sel.getId());
        tblPedidos.refresh();
    }

    // =================== EXPORTACIONES ===================

    /**
     * Exporta el pedido seleccionado a un archivo TXT usando {@code ExportarArchivo.exportarPedido}.
     *
     * <p>El archivo se genera en la carpeta {@code reportes} con prefijo {@code repartidor_pedido_}.</p>
     */
    @FXML
    private void onExportarTxt() {
        var sel = tblPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            error("Selección", "Elige un pedido.");
            return;
        }
        try {
            co.edu.uniquindio.poo.amazen.Model.ExportarArchivo
                    .exportarPedido(sel, "reportes/repartidor_pedido_" + sel.getId() + ".txt");
            info("Exportación", "TXT generado para " + sel.getId());
        } catch (Exception e) {
            error("Exportación TXT", e.getMessage());
        }
    }

    /**
     * Exporta todos los pedidos visibles en la tabla a un CSV utilizando {@link co.edu.uniquindio.poo.amazen.Service.ExportCsvService}.
     *
     * <p>Se convierte cada {@link Pedido} a su DTO correspondiente mediante {@link co.edu.uniquindio.poo.amazen.Model.DTO.DtoMapper#toDTO(Pedido)}
     * y se escribe en {@code reportes/mis_pedidos.csv}.</p>
     */
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

    // =================== HELPERS DE ALERTA ===================

    /**
     * Muestra una alerta de información con encabezado y contenido dados.
     *
     * @param h texto del encabezado.
     * @param c contenido del mensaje.
     */
    private void info(String h, String c) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, c, ButtonType.OK);
        a.setHeaderText(h);
        a.showAndWait();
    }

    /**
     * Muestra una alerta de error con encabezado y contenido dados.
     *
     * @param h texto del encabezado.
     * @param c contenido del mensaje.
     */
    private void error(String h, String c) {
        Alert a = new Alert(Alert.AlertType.ERROR, c, ButtonType.OK);
        a.setHeaderText(h);
        a.showAndWait();
    }
}
