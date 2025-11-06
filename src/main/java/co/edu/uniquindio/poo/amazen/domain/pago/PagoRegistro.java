package co.edu.uniquindio.poo.amazen.domain.pago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoRegistro {
    private String id;
    private String envioId;
    private LocalDateTime fecha;
    private BigDecimal montoTotal;
    private String estado;            // "APROBADO" / "RECHAZADO"
    private String referenciaExterna; // id de pasarela
    private String medioUsado;        // "Tarjeta", "Efectivo", etc.

    public PagoRegistro() { }

    public PagoRegistro(String id, String envioId, LocalDateTime fecha, BigDecimal montoTotal,
                        String estado, String referenciaExterna, String medioUsado) {
        this.id = id;
        this.envioId = envioId;
        this.fecha = fecha;
        this.montoTotal = montoTotal;
        this.estado = estado;
        this.referenciaExterna = referenciaExterna;
        this.medioUsado = medioUsado;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEnvioId() { return envioId; }
    public void setEnvioId(String envioId) { this.envioId = envioId; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getReferenciaExterna() { return referenciaExterna; }
    public void setReferenciaExterna(String referenciaExterna) { this.referenciaExterna = referenciaExterna; }
    public String getMedioUsado() { return medioUsado; }
    public void setMedioUsado(String medioUsado) { this.medioUsado = medioUsado; }
}
