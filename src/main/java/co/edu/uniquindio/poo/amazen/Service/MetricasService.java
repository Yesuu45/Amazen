package co.edu.uniquindio.poo.amazen.Service;

import co.edu.uniquindio.poo.amazen.Model.Pedido;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MetricasService {

    public record Resultado(
            int totalEntregas,
            double tiempoPromedioHoras,
            double ingresosEstimados,
            int totalIncidencias,
            Map<String, Double> tiemposPorDia,
            Map<String, Integer> incidenciasPorZona,
            Map<String, Integer> estadosDistribucion
    ) {}

    private Resultado ultimo; // para exportación rápida

    public Resultado obtenerDatos(LocalDate desde, LocalDate hasta, String zonaFiltro) {
        List<Pedido> pedidos = cargarPedidos();

        // Filtrado por fechas (creación) y zona (en incidencias o repartidor si así lo decides)
        var filtrados = pedidos.stream()
                .filter(p -> {
                    var fc = p.getFechaCreacion() == null ? LocalDate.MIN : p.getFechaCreacion().toLocalDate();
                    boolean okFecha = (desde == null || !fc.isBefore(desde)) && (hasta == null || !fc.isAfter(hasta));
                    return okFecha;
                })
                .toList();

        // KPI: entregas y tiempo promedio (solo pedidos entregados con fechas válidas)
        var entregados = filtrados.stream()
                .filter(p -> p.getFechaEntrega() != null && p.getFechaCreacion() != null)
                .toList();

        double tiempoPromedioHoras = 0.0;
        if (!entregados.isEmpty()) {
            tiempoPromedioHoras = entregados.stream()
                    .mapToDouble(p -> Duration.between(p.getFechaCreacion(), p.getFechaEntrega()).toMinutes() / 60.0)
                    .average().orElse(0.0);
        }

        // Ingresos estimados: sumatoria del total del carrito (puedes cambiarlo por Pagos reales)
        double ingresos = filtrados.stream().mapToDouble(Pedido::calcularTotal).sum();

        // Incidencias
        var todasIncidencias = filtrados.stream()
                .flatMap(p -> p.getIncidencias().stream())
                .toList();

        if (zonaFiltro != null) {
            String z = zonaFiltro.trim().toLowerCase();
            // filtra incidencias por coincidencia en zona
            // si no tienes zona en pedido, usamos la zona de cada incidencia
            // (puedes ampliar aquí si más adelante enlazas zona por repartidor)
            var tmp = todasIncidencias.stream()
                    .filter(i -> i.getZona() != null && i.getZona().toLowerCase().contains(z))
                    .toList();
            // si filtras incidencias, también puedes filtrar pedidos que no tengan incidencias en esa zona
            // de momento mantenemos pedidos filtrados solo por fechas
            todasIncidencias = tmp;
        }

        Map<String, Integer> incPorZona = todasIncidencias.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getZona() == null || i.getZona().isBlank() ? "(sin zona)" : i.getZona(),
                        Collectors.summingInt(x -> 1)
                ));

        // Tiempos por día (promedio)
        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Double> tiemposPorDia = entregados.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getFechaEntrega().toLocalDate().format(FMT),
                        Collectors.averagingDouble(p ->
                                Duration.between(p.getFechaCreacion(), p.getFechaEntrega()).toMinutes() / 60.0
                        )
                ));

        // Distribución por estado (toString() de tus clases de estado)
        Map<String, Integer> estados = filtrados.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getEstado() == null ? "(sin estado)" : p.getEstado().toString(),
                        Collectors.summingInt(x -> 1)
                ));

        ultimo = new Resultado(
                entregados.size(),
                round2(tiempoPromedioHoras),
                round2(ingresos),
                todasIncidencias.size(),
                sortByKeyAsc(tiemposPorDia),
                sortByValueDesc(incPorZona),
                sortByValueDesc(estados)
        );
        return ultimo;
    }

    public Resultado obtenerUltimoResultado() { return ultimo; }

    // ====== Fuente de pedidos ======
    private List<Pedido> cargarPedidos() {
        // TODO: cámbialo si usas HistorialPedido u otra fuente central
        // return HistorialPedido.getInstance().obtenerPedidos();
        // De momento, intenta encontrar una lista estática si la tienes; si no, devuelve vacía.
        try {
            var field = Class.forName("co.edu.uniquindio.poo.amazen.Controller.GestorPedidosController")
                    .getDeclaredField("pedidos");
            field.setAccessible(true);
        } catch (Exception ignored) {}
        return Collections.emptyList();
    }

    // ====== Helpers ordenar y redondear ======
    private static Map<String, Double> sortByKeyAsc(Map<String, Double> src) {
        return src.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a,b)->a, LinkedHashMap::new));
    }

    private static <T> Map<String, T> sortByValueDesc(Map<String, T> src) {
        return src.entrySet().stream()
                .sorted((a,b) -> {
                    var va = a.getValue() instanceof Number ? ((Number)a.getValue()).doubleValue() : 0;
                    var vb = b.getValue() instanceof Number ? ((Number)b.getValue()).doubleValue() : 0;
                    return Double.compare(vb, va);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a,b)->a, LinkedHashMap::new));
    }

    private static double round2(double v) { return Math.round(v * 100.0) / 100.0; }
}
