package co.edu.uniquindio.poo.amazen.Model.DTO;

import co.edu.uniquindio.poo.amazen.Model.Disponibilidad;

/**
 * DTO ligero para exponer repartidores en UI / reportes.
 * No incluye contraseña ni otros datos sensibles.
 */
public record RepartidorDTO(
        String documento,
        String nombreCompleto,
        String email,
        String telefono,
        String celular,
        String direccion,
        String zonaCobertura,
        Disponibilidad disponibilidad
) {}
