package co.edu.uniquindio.poo.amazen.Model.DTO;

import co.edu.uniquindio.poo.amazen.Model.DetallePedido;
import co.edu.uniquindio.poo.amazen.Model.Pedido;

import java.util.Collections;
import java.util.List;

/**
 * Mapeos entre entidades del dominio y DTOs.
 * - toDTO(Pedido)         -> PedidoDTO (para reportes/CSV)
 * - toTicketDTO(Pedido)   -> PedidoTicketDTO (para TXT / ticket)
 * - toDTO(DetallePedido)  -> DetallePedidoDTO (líneas del pedido)
 */
public final class DtoMapper {
    private DtoMapper() {}

    /** Pedido -> DTO para CSV/Reportes. */
    public static PedidoDTO toDTO(Pedido p) {
        if (p == null) return null;

        String estado = (p.getEstado() == null) ? "—" : p.getEstado().toString();
        String docRep  = p.getDocumentoRepartidorAsignado();

        int cantInc = 0;
        try { cantInc = (p.getIncidencias() == null) ? 0 : p.getIncidencias().size(); }
        catch (Throwable ignore) {}

        double total = 0.0;
        try { total = p.calcularTotal(); }
        catch (Throwable ignore) {}

        return new PedidoDTO(
                p.getId(),
                estado,
                total,
                docRep,
                p.getFechaCreacion(),
                p.getFechaAsignacion(),
                p.getFechaEntrega(),
                cantInc
        );
    }

    /** Pedido -> DTO para impresión de ticket TXT. */
    public static PedidoTicketDTO toTicketDTO(Pedido p) {
        if (p == null) return null;

        List<DetallePedidoDTO> lineas = Collections.emptyList();
        try {
            if (p.getCarrito() != null && p.getCarrito().getDetalles() != null) {
                lineas = p.getCarrito().getDetalles().stream()
                        .map(DtoMapper::toDTO) // DetallePedido -> DetallePedidoDTO
                        .toList();
            }
        } catch (Throwable ignore) { /* tolerancia a nulls */ }

        String estado = p.getEstado() == null ? "—" : p.getEstado().toString();

        return new PedidoTicketDTO(
                p.getId(),
                estado,
                p.calcularTotal(),
                lineas
        );
    }

    /** DetallePedido -> DTO. */
    public static DetallePedidoDTO toDTO(DetallePedido d) {
        if (d == null) return null;
        String nombre = null;
        try { nombre = (d.getProducto() == null) ? null : d.getProducto().getNombre(); }
        catch (Throwable ignore) {}

        return new DetallePedidoDTO(
                nombre == null ? "(sin nombre)" : nombre,
                d.getCantidad(),
                d.getSubtotal()
        );
    }
}
