package co.edu.uniquindio.poo.amazen.Model;

import co.edu.uniquindio.poo.amazen.Model.DTO.DtoMapper;
import co.edu.uniquindio.poo.amazen.Model.DTO.PedidoTicketDTO;
import co.edu.uniquindio.poo.amazen.Service.ExportTxtService;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Adaptador para exportar pedidos a archivo de texto.
 * Mantiene compatibilidad con la firma requerida por el trabajo.
 */
public class ExportarArchivo {

    /**
     * Exporta un pedido a un archivo TXT.
     *
     * @param pedido        pedido a exportar
     * @param rutaArchivoTxt ruta del archivo destino
     * @throws IOException si ocurre un error de E/S
     */
    public static void exportarPedido(Pedido pedido, String rutaArchivoTxt) throws IOException {
        if (pedido == null) throw new IllegalArgumentException("Pedido requerido");
        if (rutaArchivoTxt == null || rutaArchivoTxt.isBlank())
            throw new IllegalArgumentException("Ruta inválida");

        PedidoTicketDTO dto = DtoMapper.toTicketDTO(pedido);
        ExportTxtService.exportarPedido(Path.of(rutaArchivoTxt), dto);
    }
}
