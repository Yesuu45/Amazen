package co.edu.uniquindio.poo.amazen.Model.DTO;

/** Línea del ticket: nombre del producto, cantidad y subtotal. */
public record DetallePedidoDTO(
        String productoNombre,
        int    cantidad,
        double subtotal
) {}
