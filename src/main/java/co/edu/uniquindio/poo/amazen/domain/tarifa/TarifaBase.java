package co.edu.uniquindio.poo.amazen.domain.tarifa;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Representa una tarifa base sobre la que se aplican estrategias
 * (por distancia, peso, volumen, prioridad, etc.).
 *
 * Esta clase se usa como punto de partida para calcular los costos
 * totales de un envío mediante las estrategias de tarifa.
 */
public class TarifaBase {

    // ===== Atributos =====
    private String id;
    private String descripcion;          // "Base Nacional", "Envío Urbano", etc.
    private BigDecimal precioPorKm;      // costo por kilómetro
    private BigDecimal precioPorKg;      // costo por kilogramo
    private BigDecimal precioPorVolumen; // costo por 1000 cm³
    private BigDecimal iva;              // porcentaje (ej. 0.19)
    private boolean activa;

    // ===== Constructores =====
    public TarifaBase() { }

    public TarifaBase(String id,
                      String descripcion,
                      BigDecimal precioPorKm,
                      BigDecimal precioPorKg,
                      BigDecimal precioPorVolumen,
                      BigDecimal iva,
                      boolean activa) {
        this.id = Objects.requireNonNull(id, "id");
        this.descripcion = descripcion;
        this.precioPorKm = precioPorKm;
        this.precioPorKg = precioPorKg;
        this.precioPorVolumen = precioPorVolumen;
        this.iva = iva;
        this.activa = activa;
    }

    // ===== Getters y Setters =====
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioPorKm() {
        return precioPorKm;
    }

    public void setPrecioPorKm(BigDecimal precioPorKm) {
        this.precioPorKm = precioPorKm;
    }

    public BigDecimal getPrecioPorKg() {
        return precioPorKg;
    }

    public void setPrecioPorKg(BigDecimal precioPorKg) {
        this.precioPorKg = precioPorKg;
    }

    public BigDecimal getPrecioPorVolumen() {
        return precioPorVolumen;
    }

    public void setPrecioPorVolumen(BigDecimal precioPorVolumen) {
        this.precioPorVolumen = precioPorVolumen;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    // ===== Métodos utilitarios =====
    @Override
    public String toString() {
        return String.format(
                "%s [Km=%s, Kg=%s, Vol=%s, IVA=%s]",
                descripcion, precioPorKm, precioPorKg, precioPorVolumen, iva
        );
    }
}
