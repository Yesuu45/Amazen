package co.edu.uniquindio.poo.amazen.Model.DTO;

import java.time.LocalDateTime;

/** Fila para CSV de pedidos (inmutable). */
public record PedidoDTO(
        String id,
        String estado,
        double total,
        String repartidorDocumento,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaAsignacion,
        LocalDateTime fechaEntrega,
        int incidencias
) {}
