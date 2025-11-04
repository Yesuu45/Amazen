package co.edu.uniquindio.poo.amazen.Model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportarArchivo {
    public static void exportarPedido(Pedido pedido, String rutaArchivoTxt) throws IOException {
        // Asegurar carpeta existente
        File archivo = new File(rutaArchivoTxt);
        File carpeta = archivo.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }

        FileWriter writer = new FileWriter(archivo);
        writer.write("=========================================\n");
        writer.write("            DETALLE DEL PEDIDO\n");
        writer.write("=========================================\n");
        writer.write("ID Pedido: " + pedido.getId() + "\n");
        writer.write("Estado: " + pedido.getEstado() + "\n\n");

        writer.write(String.format("%-25s %8s %12s\n", "Producto", "Cantidad", "Subtotal"));
        writer.write("-----------------------------------------\n");

        List<DetallePedido> detalles = pedido.getCarrito().getDetalles();
        for (int i = 0; i < detalles.size(); i++) {
            DetallePedido d = detalles.get(i);
            String nombre = d.getProducto().getNombre();
            int cantidad = d.getCantidad();
            double subtotal = d.getSubtotal();
            writer.write(String.format("%-25s %8d %12.2f\n", nombre, cantidad, subtotal));
        }

        writer.write("-----------------------------------------\n");
        writer.write(String.format("TOTAL: %.2f\n", pedido.calcularTotal()));
        writer.close();
    }
}
