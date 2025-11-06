package co.edu.uniquindio.poo.amazen.domain.pago;

import co.edu.uniquindio.poo.amazen.domain.dto.PagoDTO;

/**
 * Interfaz genérica para cualquier pasarela de pago.
 * Aplica el patrón Adapter/Strategy.
 */
public interface PasarelaPago {
    ResultadoPago cobrar(PagoDTO pagoDTO);
}
