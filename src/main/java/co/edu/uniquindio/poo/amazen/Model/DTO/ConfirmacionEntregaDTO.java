package co.edu.uniquindio.poo.amazen.Model.DTO;

import java.time.LocalDateTime;

/**
 * DTO para registrar la prueba de entrega de un pedido.
 * (Datos quemados en demo o capturados desde la UI del repartidor)
 */
public record ConfirmacionEntregaDTO(
        String pedidoId,
        String receptorNombre,
        String receptorDocumento,
        String observaciones,
        LocalDateTime fecha
) {}
