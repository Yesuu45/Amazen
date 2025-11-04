package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.ExportarArchivo;
import co.edu.uniquindio.poo.amazen.Model.HistorialPedido;
import co.edu.uniquindio.poo.amazen.Model.Pedido;

import java.io.IOException;
import java.util.List;

public class HistorialController {
    private HistorialPedido historial;

    public HistorialController() {
        historial = HistorialPedido.getInstance();
    }

    public List<Pedido> obtenerPedidos() {
        return historial.obtenerPedidos();
    }

    public void registrarPedido(Pedido pedido) {
        historial.registrarPedido(pedido);
    }

    public void exportarPedido(Pedido pedido, String rutaArchivoTxt) throws IOException {
        if (!"PAGADO".equals(pedido.getEstado().toString())) {
            throw new IllegalStateException("❌ Solo se pueden exportar pedidos pagados.");
        }
        ExportarArchivo.exportarPedido(pedido, rutaArchivoTxt);
    }
}
