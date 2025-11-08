package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.Model.Pedido;
import co.edu.uniquindio.poo.amazen.Service.MetricasService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Map;

public class AdminDashboardViewController {

    @FXML private DatePicker dpDesde, dpHasta;
    @FXML private TextField txtZona;
    @FXML private Label lblTotalEntregas, lblTiempoPromedio, lblIngresos, lblIncidencias, lblInfo;

    @FXML private LineChart<String, Number> lineTiempos;
    @FXML private BarChart<String, Number>  barIncidenciasZona;
    @FXML private PieChart pieEstados;

    private final MetricasService metricas = new MetricasService();

    @FXML
    public void initialize() {
        // Filtros por defecto: últimos 30 días
        dpHasta.setValue(LocalDate.now());
        dpDesde.setValue(LocalDate.now().minusDays(30));
        recargar();
    }

    @FXML
    private void onAplicarFiltros() {
        recargar();
    }

    private void recargar() {
        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();
        String zona = txtZona.getText() == null ? "" : txtZona.getText().trim();

        var datos = metricas.obtenerDatos(desde, hasta, zona.isBlank() ? null : zona);

        // KPIs
        lblTotalEntregas.setText("Entregas: " + datos.totalEntregas());
        lblTiempoPromedio.setText(String.format("Tiempo promedio (h): %.2f", datos.tiempoPromedioHoras()));
        lblIngresos.setText(String.format("Ingresos (estimado): $%.2f", datos.ingresosEstimados()));
        lblIncidencias.setText("Incidencias: " + datos.totalIncidencias());

        // Línea: tiempos promedio por día
        lineTiempos.getData().clear();
        var serie = new XYChart.Series<String, Number>();
        serie.setName("Horas promedio");
        datos.tiemposPorDia().forEach((fecha, horas) -> serie.getData().add(new XYChart.Data<>(fecha, horas)));
        lineTiempos.getData().add(serie);

        // Barras: incidencias por zona
        barIncidenciasZona.getData().clear();
        var serieBar = new XYChart.Series<String, Number>();
        serieBar.setName("Incidencias");
        for (Map.Entry<String, Integer> e : datos.incidenciasPorZona().entrySet()) {
            serieBar.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }
        barIncidenciasZona.getData().add(serieBar);

        // Pie: distribución por estado
        pieEstados.setData(FXCollections.observableArrayList());
        datos.estadosDistribucion().forEach((estado, count) ->
                pieEstados.getData().add(new PieChart.Data(estado, count)));

        lblInfo.setText("Período: " + desde + " → " + hasta + (zona.isBlank() ? "" : (" | Zona: " + zona)));
    }

    @FXML
    private void onExportarCsv() {
        var datos = metricas.obtenerUltimoResultado();
        if (datos == null) { info("Exportar", "Primero aplica filtros."); return; }

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("kpi,valor\n");
            sb.append("total_entregas,").append(datos.totalEntregas()).append("\n");
            sb.append("tiempo_promedio_h,").append(String.format("%.2f", datos.tiempoPromedioHoras())).append("\n");
            sb.append("ingresos_estimados,").append(String.format("%.2f", datos.ingresosEstimados())).append("\n");
            sb.append("total_incidencias,").append(datos.totalIncidencias()).append("\n\n");

            sb.append("fecha,horas_promedio\n");
            datos.tiemposPorDia().forEach((f,h) -> sb.append(f).append(",").append(h).append("\n"));
            sb.append("\nzona,incidencias\n");
            datos.incidenciasPorZona().forEach((z,c) -> sb.append(z).append(",").append(c).append("\n"));
            sb.append("\nestado,cantidad\n");
            datos.estadosDistribucion().forEach((s,c) -> sb.append(s).append(",").append(c).append("\n"));

            FileChooser fc = new FileChooser();
            fc.setTitle("Exportar métricas (CSV)");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
            File f = fc.showSaveDialog(((Stage) pieEstados.getScene().getWindow()));
            if (f == null) return;

            Files.writeString(f.toPath(), sb.toString());
            info("Exportado", "CSV guardado en:\n" + f.getAbsolutePath());
        } catch (Exception e) {
            error("Error", "No se pudo exportar: " + e.getMessage());
        }
    }

    @FXML
    private void onCerrar() {
        ((Stage) pieEstados.getScene().getWindow()).close();
    }

    private void info(String t, String c) {
        var a = new Alert(Alert.AlertType.INFORMATION, c, ButtonType.OK);
        a.setHeaderText(t); a.showAndWait();
    }
    private void error(String t, String c) {
        var a = new Alert(Alert.AlertType.ERROR, c, ButtonType.OK);
        a.setHeaderText(t); a.showAndWait();
    }
}
