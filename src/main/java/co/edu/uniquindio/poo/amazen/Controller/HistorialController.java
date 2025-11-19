package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.ExportarArchivo;
import co.edu.uniquindio.poo.amazen.Model.HistorialPedido;
import co.edu.uniquindio.poo.amazen.Model.Pedido;

import java.io.IOException;
import java.util.List;

/**
 * Encapsula el acceso al {@link HistorialPedido} y operaciones de exportación.
 */
public class HistorialController {

    private final HistorialPedido historial;

    public HistorialController() {
        this.historial = HistorialPedido.getInstance();
    }

    /**
     * Obtiene todos los pedidos registrados en el historial.
     *
     * @return lista de pedidos
     */
    public List<Pedido> obtenerPedidos() {
        return historial.obtenerPedidos();
    }

    /**
     * Registra un nuevo pedido en el historial global.
     *
     * @param pedido pedido a registrar
     */
    public void registrarPedido(Pedido pedido) {
        historial.registrarPedido(pedido);
    }

    /**
     * Exporta un pedido a un archivo de texto, validando que esté pagado.
     *
     * @param pedido        pedido a exportar
     * @param rutaArchivoTxt ruta del archivo de salida
     * @throws IOException si ocurre un error de escritura
     */
    public void exportarPedido(Pedido pedido, String rutaArchivoTxt) throws IOException {
        if (!"PAGADO".equals(pedido.getEstado().toString())) {
            throw new IllegalStateException("Solo se pueden exportar pedidos pagados.");
        }
        ExportarArchivo.exportarPedido(pedido, rutaArchivoTxt);
    }
}
