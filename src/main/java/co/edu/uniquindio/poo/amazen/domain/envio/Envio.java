package co.edu.uniquindio.poo.amazen.domain.envio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import co.edu.uniquindio.poo.amazen.domain.usuario.Direccion;
import co.edu.uniquindio.poo.amazen.domain.usuario.Usuario;
import co.edu.uniquindio.poo.amazen.domain.reparto.Repartidor;
import co.edu.uniquindio.poo.amazen.domain.pago.PagoRegistro;


public class Envio {
    private String id;
    private Usuario cliente;
    private Direccion origen;
    private Direccion destino;
    private final List<Paquete> paquetes = new ArrayList<>();
    private EstadoEnvio estado = EstadoEnvio.CREADO;

    // Datos asociados
    private String prioridad;       // "Normal", "Alta"
    private String extrasResumen;   // (se detallará con Decorator más adelante)
    private String detalleTarifa;   // string simple por ahora (TarifaDTO en DTOs)
    private PagoRegistro pago;      // registro de pago (si existe)
    private Repartidor repartidor;  // asignado (si existe)

    public Envio() { }

    public Envio(String id, Usuario cliente, Direccion origen, Direccion destino) {
        this.id = Objects.requireNonNull(id, "id");
        this.cliente = cliente;
        this.origen = origen;
        this.destino = destino;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = Objects.requireNonNull(id, "id"); }
    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }
    public Direccion getOrigen() { return origen; }
    public void setOrigen(Direccion origen) { this.origen = origen; }
    public Direccion getDestino() { return destino; }
    public void setDestino(Direccion destino) { this.destino = destino; }
    public List<Paquete> getPaquetes() { return List.copyOf(paquetes); }
    public boolean addPaquete(Paquete p) { return paquetes.add(Objects.requireNonNull(p)); }
    public EstadoEnvio getEstado() { return estado; }
    public void setEstado(EstadoEnvio estado) { this.estado = estado; }
    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
    public String getExtrasResumen() { return extrasResumen; }
    public void setExtrasResumen(String extrasResumen) { this.extrasResumen = extrasResumen; }
    public String getDetalleTarifa() { return detalleTarifa; }
    public void setDetalleTarifa(String detalleTarifa) { this.detalleTarifa = detalleTarifa; }
    public PagoRegistro getPago() { return pago; }
    public void setPago(PagoRegistro pago) { this.pago = pago; }
    public Repartidor getRepartidor() { return repartidor; }
    public void setRepartidor(Repartidor repartidor) { this.repartidor = repartidor; }
}
