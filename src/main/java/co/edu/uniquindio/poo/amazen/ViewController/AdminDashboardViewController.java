package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.Model.HistorialPedido;
import co.edu.uniquindio.poo.amazen.Model.Pedido;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador del panel de métricas del administrador.
 * <p>
 * Muestra:
 * <ul>
 *     <li>Gráfica de línea con el tiempo promedio de entrega por día.</li>
 *     <li>Gráfica de barras con los ingresos diarios.</li>
 *     <li>Gráfico de torta con incidencias por zona.</li>
 *     <li>Gráfico de torta con distribución de pedidos por estado.</li>
 *     <li>Resumen inferior con totales por estado.</li>
 * </ul>
 * <p>
 * Los datos se obtienen desde {@link HistorialPedido}.
 */
public class AdminDashboardViewController {

    /** Gráfico de línea: tiempo promedio de entrega (minutos) por fecha de entrega. */
    @FXML private LineChart<String, Number> lineTiempos;

    /** Gráfico de barras: ingresos por día (suma de {@link Pedido#calcularTotal()}). */
    @FXML private BarChart<String, Number>  barIngresos;

    /** Gráfico de torta: incidencias agrupadas por zona. */
    @FXML private PieChart pieIncidencias;

    /** Gráfico de torta: distribución de pedidos por estado (toString() del Estado). */
    @FXML private PieChart pieEstados;

    /** Etiqueta de resumen inferior (totales por estado). */
    @FXML private Label lblResumen;

    /**
     * Inicializa la vista al cargar el FXML.
     * Se invoca automáticamente por JavaFX y dispara la carga de métricas.
     */
    @FXML
    public void initialize() {
        cargar();
    }

    /**
     * Acción del botón "Refrescar".
     * Vuelve a cargar todas las métricas desde el historial y muestra un mensaje de confirmación.
     */
    @FXML
    private void onRefrescar() {
        cargar();
        var a = new Alert(Alert.AlertType.INFORMATION, "Métricas actualizadas", ButtonType.OK);
        a.setHeaderText("Refrescar");
        a.showAndWait();
    }

    /**
     * Carga todas las métricas a partir de la lista de pedidos del historial.
     * <p>
     * Orquesta las llamadas a:
     * <ul>
     *     <li>{@link #cargarLineaTiempos(List)}</li>
     *     <li>{@link #cargarBarrasIngresos(List)}</li>
     *     <li>{@link #cargarPieIncidencias(List)}</li>
     *     <li>{@link #cargarPieEstados(List)}</li>
     *     <li>{@link #cargarResumen(List)}</li>
     * </ul>
     */
    private void cargar() {
        List<Pedido> pedidos = HistorialPedido.getInstance().obtenerPedidos();
        if (pedidos == null) pedidos = List.of();

        cargarLineaTiempos(pedidos);
        cargarBarrasIngresos(pedidos);
        cargarPieIncidencias(pedidos);
        cargarPieEstados(pedidos);
        cargarResumen(pedidos);
    }

    // -------------------------------------------------------------------------
    // LÍNEA: Tiempo promedio de entrega
    // -------------------------------------------------------------------------

    /**
     * Llena la gráfica de línea con el tiempo promedio de entrega en minutos por fecha de entrega.
     * <p>
     * Para cada pedido que tenga fecha de creación y entrega válidas, calcula
     * la diferencia en minutos y la agrupa por día de entrega.
     *
     * @param pedidos lista de pedidos a analizar
     */
    private void cargarLineaTiempos(List<Pedido> pedidos) {
        // Mapa: día de entrega -> lista de tiempos (min) de cada pedido entregado ese día
        Map<LocalDate, List<Long>> porDiaMin = new HashMap<>();

        for (Pedido p : pedidos) {
            LocalDateTime fCre = p.getFechaCreacion();
            LocalDateTime fEnt = p.getFechaEntrega();
            // Solo se consideran pedidos con fechas válidas y entrega posterior o igual a la creación
            if (fCre != null && fEnt != null && !fEnt.isBefore(fCre)) {
                long mins = ChronoUnit.MINUTES.between(fCre, fEnt);
                porDiaMin.computeIfAbsent(fEnt.toLocalDate(), d -> new ArrayList<>()).add(mins);
            }
        }

        lineTiempos.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Promedio (min)");

        // Ordena por fecha y calcula el promedio de minutos por día
        porDiaMin.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    double avg = e.getValue().stream()
                            .mapToLong(Long::longValue)
                            .average()
                            .orElse(0);
                    serie.getData().add(new XYChart.Data<>(e.getKey().toString(), avg));
                });

        lineTiempos.getData().add(serie);
    }

    // -------------------------------------------------------------------------
    // BARRAS: Ingresos por día
    // -------------------------------------------------------------------------

    /**
     * Llena la gráfica de barras con la suma de ingresos por día,
     * usando {@link Pedido#calcularTotal()} y la fecha de creación.
     *
     * @param pedidos lista de pedidos a analizar
     */
    private void cargarBarrasIngresos(List<Pedido> pedidos) {
        // Mapa: día de creación -> suma de total del pedido
        Map<LocalDate, Double> ingresosPorDia = new HashMap<>();

        for (Pedido p : pedidos) {
            LocalDateTime fCre = p.getFechaCreacion();
            if (fCre != null) {
                ingresosPorDia.merge(
                        fCre.toLocalDate(),
                        p.calcularTotal(),
                        Double::sum
                );
            }
        }

        barIngresos.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("COP");

        ingresosPorDia.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e ->
                        serie.getData().add(
                                new XYChart.Data<>(e.getKey().toString(), e.getValue())
                        )
                );

        barIngresos.getData().add(serie);
    }

    // -------------------------------------------------------------------------
    // PIE: Incidencias por zona
    // -------------------------------------------------------------------------

    /**
     * Llena el gráfico de torta con incidencias agrupadas por zona.
     * <p>
     * Para no acoplarse a una implementación específica de {@code Incidencia},
     * se usa reflexión para invocar un método {@code getZona()} si existe;
     * si no, se agrupa como "Incidencias".
     *
     * @param pedidos lista de pedidos a analizar
     */
    private void cargarPieIncidencias(List<Pedido> pedidos) {
        // Mapa: nombre de la zona -> cantidad de incidencias registradas
        Map<String, Integer> incidenciasPorZona = new HashMap<>();

        for (Pedido p : pedidos) {
            var list = p.getIncidencias();
            if (list == null) continue;

            for (Object inc : list) {
                try {
                    // Intentar obtener el método getZona() de la incidencia
                    Method m = inc.getClass().getMethod("getZona");
                    Object z = m.invoke(inc);
                    String zona = (z == null) ? "Sin zona" : z.toString();
                    incidenciasPorZona.merge(zona, 1, Integer::sum);
                } catch (Throwable t) {
                    // Si no se puede obtener zona, se agrupa como genérico
                    incidenciasPorZona.merge("Incidencias", 1, Integer::sum);
                }
            }
        }

        pieIncidencias.getData().clear();
        incidenciasPorZona.forEach(
                (k, v) -> pieIncidencias.getData().add(new PieChart.Data(k, v))
        );
    }

    // -------------------------------------------------------------------------
    // PIE: Distribución por estado
    // -------------------------------------------------------------------------

    /**
     * Llena el gráfico de torta con la distribución de pedidos por estado.
     * <p>
     * Se usa {@code toString()} del estado actual; si el estado es {@code null}
     * se muestra como "—".
     *
     * @param pedidos lista de pedidos a analizar
     */
    private void cargarPieEstados(List<Pedido> pedidos) {
        // Mapa: nombre del estado -> número de pedidos en ese estado
        Map<String, Long> porEstado = pedidos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getEstado() == null ? "—" : p.getEstado().toString(),
                        Collectors.counting()
                ));

        pieEstados.getData().clear();
        porEstado.forEach(
                (k, v) -> pieEstados.getData().add(new PieChart.Data(k, v))
        );
    }

    // -------------------------------------------------------------------------
    // RESUMEN INFERIOR
    // -------------------------------------------------------------------------

    /**
     * Construye y muestra el resumen inferior con totales por estado
     * (ENTREGADO, ENVIADO, EMPAQUETADO, PAGADO y otros).
     *
     * @param pedidos lista de pedidos a analizar
     */
    private void cargarResumen(List<Pedido> pedidos) {
        Map<String, Long> porEstado = pedidos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getEstado() == null ? "—" : p.getEstado().toString(),
                        Collectors.counting()
                ));

        long entregados = porEstado.getOrDefault("ENTREGADO",   0L);
        long enviados   = porEstado.getOrDefault("ENVIADO",     0L);
        long empaq      = porEstado.getOrDefault("EMPAQUETADO", 0L);
        long pagado     = porEstado.getOrDefault("PAGADO",      0L);
        long otros      = pedidos.size() - (entregados + enviados + empaq + pagado);

        lblResumen.setText(
                "Total pedidos: " + pedidos.size() +
                        " | Entregados: " + entregados +
                        " | Enviados: " + enviados +
                        " | Empaquetados: " + empaq +
                        " | Pagado: " + pagado +
                        " | Otros: " + Math.max(0, otros)
        );
    }
}
