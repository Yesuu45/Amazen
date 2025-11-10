package co.edu.uniquindio.poo.amazen.Model.DTO;

import java.util.List;

/** DTO para exportar el ticket del pedido a TXT/CSV/PDF sin exponer entidades. */
public record PedidoTicketDTO(
        String id,
        String estado,
        double total,
        List<DetallePedidoDTO> detalles
) {}
