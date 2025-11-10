package co.edu.uniquindio.poo.amazen.Model;

import co.edu.uniquindio.poo.amazen.Model.DTO.DtoMapper;
import co.edu.uniquindio.poo.amazen.Model.DTO.PedidoTicketDTO;
import co.edu.uniquindio.poo.amazen.Service.ExportTxtService;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Clase usada por compatibilidad para el trabajo.
 * Internamente usa DTOs y un servicio de exportación.
 */
public class ExportarArchivo {

    /**
     * Exporta un pedido a TXT.
     * Mantiene la firma original para no romper llamadas existentes.
     */
    public static void exportarPedido(Pedido pedido, String rutaArchivoTxt) throws IOException {
        if (pedido == null) throw new IllegalArgumentException("Pedido requerido");
        if (rutaArchivoTxt == null || rutaArchivoTxt.isBlank())
            throw new IllegalArgumentException("Ruta inválida");

        PedidoTicketDTO dto = DtoMapper.toTicketDTO(pedido);
        ExportTxtService.exportarPedido(Path.of(rutaArchivoTxt), dto);
    }
}
