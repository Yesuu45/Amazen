package co.edu.uniquindio.poo.amazen.domain.envio;

import java.time.LocalDateTime;

public class Incidencia {
    private String id;
    private String envioId;
    private String tipo;        // "DIRECCIÓN", "DANIO", "RETRASO"
    private String descripcion;
    private LocalDateTime fecha;
    private boolean resuelta;

    public Incidencia() { }

    public Incidencia(String id, String envioId, String tipo, String descripcion,
                      LocalDateTime fecha, boolean resuelta) {
        this.id = id;
        this.envioId = envioId;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.resuelta = resuelta;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEnvioId() { return envioId; }
    public void setEnvioId(String envioId) { this.envioId = envioId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public boolean isResuelta() { return resuelta; }
    public void setResuelta(boolean resuelta) { this.resuelta = resuelta; }
}
