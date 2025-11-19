package co.edu.uniquindio.poo.amazen.Model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registra una incidencia asociada al proceso de entrega de un pedido.
 */
public class Incidencia {

    private final String id;
    private final LocalDateTime fecha;
    private String zona;
    private String tipo;
    private String detalle;

    /**
     * Crea una incidencia con zona, tipo y detalle.
     *
     * @param zona    zona donde ocurrió
     * @param tipo    tipo de incidencia (ej. dirección incorrecta)
     * @param detalle descripción adicional
     */
    public Incidencia(String zona, String tipo, String detalle) {
        this.id = UUID.randomUUID().toString();
        this.fecha = LocalDateTime.now();
        this.zona = zona;
        this.tipo = tipo;
        this.detalle = detalle;
    }

    public String getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
}
