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

public class AdminDashboardViewController {

    @FXML private LineChart<String, Number> lineTiempos;
    @FXML private BarChart<String, Number>  barIngresos;
    @FXML private PieChart                   pieIncidencias;
    @FXML private PieChart                   pieEstados;
    @FXML private Label                      lblResumen;

    @FXML
    public void initialize() {
        cargar();
    }

    @FXML
    private void onRefrescar() {
        cargar();
        var a = new Alert(Alert.AlertType.INFORMATION, "Métricas actualizadas", ButtonType.OK);
        a.setHeaderText("Refrescar"); a.showAndWait();
    }

    private void cargar() {
        List<Pedido> pedidos = HistorialPedido.getInstance().obtenerPedidos();
        if (pedidos == null) pedidos = List.of();

        cargarLineaTiempos(pedidos);
        cargarBarrasIngresos(pedidos);
        cargarPieIncidencias(pedidos);
        cargarPieEstados(pedidos);
        cargarResumen(pedidos);
    }

    // --- Línea: tiempo promedio de entrega (minutos) por fecha de entrega ---
    private void cargarLineaTiempos(List<Pedido> pedidos) {
        Map<LocalDate, List<Long>> porDiaMin = new HashMap<>();

        for (Pedido p : pedidos) {
            LocalDateTime fCre = p.getFechaCreacion();
            LocalDateTime fEnt = p.getFechaEntrega();
            if (fCre != null && fEnt != null && !fEnt.isBefore(fCre)) {
                long mins = ChronoUnit.MINUTES.between(fCre, fEnt);
                porDiaMin.computeIfAbsent(fEnt.toLocalDate(), d -> new ArrayList<>()).add(mins);
            }
        }

        lineTiempos.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Promedio (min)");

        porDiaMin.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    double avg = e.getValue().stream().mapToLong(Long::longValue).average().orElse(0);
                    serie.getData().add(new XYChart.Data<>(e.getKey().toString(), avg));
                });

        lineTiempos.getData().add(serie);
    }

    // --- Barras: ingresos por día (suma de calcularTotal() por fecha de creación) ---
    private void cargarBarrasIngresos(List<Pedido> pedidos) {
        Map<LocalDate, Double> ingresosPorDia = new HashMap<>();

        for (Pedido p : pedidos) {
            LocalDateTime fCre = p.getFechaCreacion();
            if (fCre != null) {
                ingresosPorDia.merge(fCre.toLocalDate(), p.calcularTotal(), Double::sum);
            }
        }

        barIngresos.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("COP");

        ingresosPorDia.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> serie.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));

        barIngresos.getData().add(serie);
    }

    // --- Pie: incidencias por zona (reflexión para no depender de firma exacta) ---
    private void cargarPieIncidencias(List<Pedido> pedidos) {
        Map<String, Integer> incidenciasPorZona = new HashMap<>();

        for (Pedido p : pedidos) {
            var list = p.getIncidencias();
            if (list == null) continue;

            for (Object inc : list) {
                try {
                    Method m = inc.getClass().getMethod("getZona");
                    Object z = m.invoke(inc);
                    String zona = (z == null) ? "Sin zona" : z.toString();
                    incidenciasPorZona.merge(zona, 1, Integer::sum);
                } catch (Throwable t) {
                    incidenciasPorZona.merge("Incidencias", 1, Integer::sum);
                }
            }
        }

        pieIncidencias.getData().clear();
        incidenciasPorZona.forEach((k, v) -> pieIncidencias.getData().add(new PieChart.Data(k, v)));
    }

    // --- Pie: distribución por estado (usa toString() del estado actual) ---
    private void cargarPieEstados(List<Pedido> pedidos) {
        Map<String, Long> porEstado = pedidos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getEstado() == null ? "—" : p.getEstado().toString(),
                        Collectors.counting()
                ));

        pieEstados.getData().clear();
        porEstado.forEach((k, v) -> pieEstados.getData().add(new PieChart.Data(k, v)));
    }

    // --- Resumen inferior ---
    private void cargarResumen(List<Pedido> pedidos) {
        Map<String, Long> porEstado = pedidos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getEstado() == null ? "—" : p.getEstado().toString(),
                        Collectors.counting()
                ));

        long entregados = porEstado.getOrDefault("ENTREGADO", 0L);
        long enviados   = porEstado.getOrDefault("ENVIADO",   0L);
        long empaq      = porEstado.getOrDefault("EMPAQUETADO",0L);
        long pagado     = porEstado.getOrDefault("PAGADO",    0L);
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
