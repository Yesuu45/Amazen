package co.edu.uniquindio.poo.amazen.Service;

import co.edu.uniquindio.poo.amazen.Model.PrioridadEnvio;

/**
 * RF-003: cotizar tarifa según origen, destino, peso, volumen y prioridad.
 * Es lógica de negocio pura (no depende de JavaFX).
 */
public final class CotizadorEnvioService {

    private CotizadorEnvioService() {}

    public static double cotizar(
            String origen,
            String destino,
            double pesoKg,
            double volumenCm3,
            PrioridadEnvio prioridad
    ) {
        if (origen == null || origen.isBlank()) {
            throw new IllegalArgumentException("Origen requerido");
        }
        if (destino == null || destino.isBlank()) {
            throw new IllegalArgumentException("Destino requerido");
        }
        if (prioridad == null) {
            throw new IllegalArgumentException("Prioridad requerida");
        }

        // Base según si es misma ciudad o no (ejemplo)
        double baseZona = origen.equalsIgnoreCase(destino) ? 5000.0 : 12000.0;

        // Costo por peso: 2000 por kg
        double costoPeso = pesoKg * 2000.0;

        // Costo por volumen: 400 por cada 1000 cm³
        double costoVolumen = (volumenCm3 / 1000.0) * 400.0;

        // Factor por prioridad
        double factorPrioridad;
        switch (prioridad) {
            case BAJA   -> factorPrioridad = 0.9;
            case ALTA   -> factorPrioridad = 1.2;
            default     -> factorPrioridad = 1.0; // NORMAL
        }

        return (baseZona + costoPeso + costoVolumen) * factorPrioridad;
    }
}
