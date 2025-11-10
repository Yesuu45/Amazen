package co.edu.uniquindio.poo.amazen.Service;

import co.edu.uniquindio.poo.amazen.Model.DTO.PedidoDTO;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Exporta listas de pedidos a CSV (UTF-8, con encabezado). */
public final class ExportCsvService {
    private ExportCsvService(){}

    public static void exportPedidos(Path destino, List<PedidoDTO> filas) throws IOException {
        if (destino == null) throw new IllegalArgumentException("Ruta destino requerida");
        if (filas == null) throw new IllegalArgumentException("Lista de pedidos requerida");

        Path dir = destino.getParent();
        if (dir != null && !Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (BufferedWriter w = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            // Encabezado
            w.write(String.join(",",
                    "id","estado","total","repartidorDocumento",
                    "fechaCreacion","fechaAsignacion","fechaEntrega","incidencias"));
            w.newLine();

            // Filas
            for (PedidoDTO p : filas) {
                String fechaCreacion   = p.fechaCreacion()   == null ? "" : FMT.format(p.fechaCreacion());
                String fechaAsignacion = p.fechaAsignacion() == null ? "" : FMT.format(p.fechaAsignacion());
                String fechaEntrega    = p.fechaEntrega()    == null ? "" : FMT.format(p.fechaEntrega());

                w.write(csv(p.id())); w.write(',');
                w.write(csv(p.estado())); w.write(',');
                w.write(String.valueOf(p.total())); w.write(',');
                w.write(csv(p.repartidorDocumento())); w.write(',');
                w.write(csv(fechaCreacion)); w.write(',');
                w.write(csv(fechaAsignacion)); w.write(',');
                w.write(csv(fechaEntrega)); w.write(',');
                w.write(String.valueOf(p.incidencias()));
                w.newLine();
            }
        }
    }

    /** Escapa comas y comillas para CSV. */
    private static String csv(String s) {
        if (s == null) return "";
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String out = s.replace("\"", "\"\"");
        return needQuotes ? "\"" + out + "\"" : out;
    }
}
