package co.edu.uniquindio.poo.amazen.Service;

import co.edu.uniquindio.poo.amazen.Model.DTO.PedidoTicketDTO;
import co.edu.uniquindio.poo.amazen.Model.Pedido;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class ExportTxtService {

    public static Path exportPedido(Pedido p, Path out) throws IOException {
        if (out.getParent() != null) Files.createDirectories(out.getParent());

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("            DETALLE DEL PEDIDO\n");
        sb.append("=========================================\n");
        sb.append("ID Pedido : ").append(p.getId()).append("\n");
        sb.append("Estado    : ").append(p.getEstado()).append("\n\n");

        sb.append(String.format("%-25s %8s %12s%n", "Producto","Cantidad","Subtotal"));
        sb.append("-----------------------------------------\n");

        if (p.getCarrito() != null && p.getCarrito().getDetalles() != null) {
            p.getCarrito().getDetalles().forEach(d ->
                    sb.append(String.format("%-25s %8d %12.2f%n",
                            d.getProducto().getNombre(), d.getCantidad(), d.getSubtotal()))
            );
        }
        sb.append("-----------------------------------------\n");
        sb.append(String.format("TOTAL: %.2f%n", p.calcularTotal()));

        Files.writeString(out, sb.toString(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return out;
    }

    public static void exportarPedido(Path of, PedidoTicketDTO dto) {
    }
}
