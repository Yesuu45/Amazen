package co.edu.uniquindio.poo.amazen.Model;

import co.edu.uniquindio.poo.amazen.Model.DTO.ConfirmacionEntregaDTO;
import co.edu.uniquindio.poo.amazen.Model.Estado.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Contexto del patrón State para Pedido.
 */
public class Pedido {

    private String id;
    private CarritoDeCompras carrito;
    private EstadoPedido estado;

    // ⭐ Cliente dueño del pedido (documento del usuario que compra)
    private String documentoCliente;

    // RF-012: trazabilidad
    private String documentoRepartidorAsignado;  // null si no ha sido asignado
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaEntrega;
    private final List<Incidencia> incidencias = new ArrayList<>();

    // Prueba de entrega (DTO)
    private ConfirmacionEntregaDTO confirmacionEntrega;

    // ============================================================
    // 🔹 CONSTRUCTORES
    // ============================================================

    /** Constructor original (2 parámetros) */
    public Pedido(String id, CarritoDeCompras carrito) {
        this.id = id;
        this.carrito = carrito;
        this.estado = new EstadoPagado(this); // estado inicial
        this.fechaCreacion = LocalDateTime.now();
    }

    /** ⭐ NUEVO CONSTRUCTOR (3 parámetros: incluye documentoCliente) */
    public Pedido(String id, CarritoDeCompras carrito, String documentoCliente) {
        this(id, carrito);           // reutiliza el constructor principal
        this.documentoCliente = documentoCliente;
    }

    // ============================================================
    // 🔹 GETTERS / SETTERS
    // ============================================================

    public String getId() { return id; }
    public CarritoDeCompras getCarrito() { return carrito; }
    public EstadoPedido getEstado() { return estado; }

    // Cliente que hizo el pedido
    public String getDocumentoCliente() { return documentoCliente; }
    public void setDocumentoCliente(String documentoCliente) { this.documentoCliente = documentoCliente; }

    public String getDocumentoRepartidorAsignado() { return documentoRepartidorAsignado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public List<Incidencia> getIncidencias() { return incidencias; }
    public ConfirmacionEntregaDTO getConfirmacionEntrega() { return confirmacionEntrega; }

    // ============================================================
    // 🔹 STATE - Cambios de estado y flujo
    // ============================================================

    public void cambiarEstado(EstadoPedido nuevoEstado) {
        if (nuevoEstado == null) throw new IllegalArgumentException("El nuevo estado no puede ser null.");
        this.estado = nuevoEstado;
        if (nuevoEstado instanceof EstadoEntregado) {
            this.fechaEntrega = LocalDateTime.now();
        }
        System.out.println("🔄 Estado cambiado a: " + nuevoEstado);
    }

    public boolean procesar(String accion) {
        if (estado == null) return false;
        try {
            estado.ejecutarAccion(accion);
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("🚫 Acción no válida: " + e.getMessage());
            return false;
        }
    }

    // Métodos directos (atajos)
    public void pagar()         { if (estado != null) estado.pagar(); }
    public void verificarPago() { if (estado != null) estado.VerificacionPago(); }
    public void empaquetar()    { if (estado != null) estado.Empaquetado(); }
    public void enviar()        { if (estado != null) estado.Enviado(); }
    public void entregar()      { if (estado != null) estado.Entregado(); }

    // ============================================================
    // 🔹 RF-012 Datos adicionales
    // ============================================================

    public double calcularTotal() {
        return carrito != null ? carrito.calcularTotal() : 0.0;
    }

    public void asignarRepartidor(String documentoRepartidor) {
        if (documentoRepartidor == null || documentoRepartidor.isBlank()) {
            throw new IllegalArgumentException("Documento de repartidor requerido");
        }
        this.documentoRepartidorAsignado = documentoRepartidor;
        this.fechaAsignacion = LocalDateTime.now();
    }

    public void registrarIncidencia(Incidencia inc) {
        if (inc == null) throw new IllegalArgumentException("Incidencia requerida");
        this.incidencias.add(inc);
    }

    /** Guarda la prueba de entrega (DTO) y fuerza estado ENTREGADO si aún no se cambió. */
    public void confirmarEntrega(ConfirmacionEntregaDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Confirmación requerida");
        this.confirmacionEntrega = dto;
        if (!(estado instanceof EstadoEntregado)) {
            cambiarEstado(new EstadoEntregado(this));
        }
    }

    // ============================================================
    // 🔹 toString()
    // ============================================================

    @Override
    public String toString() {
        return "Pedido " + id +
                " | Cliente: " + (documentoCliente == null ? "—" : documentoCliente) +
                " | Estado: " + (estado != null ? estado.toString() : "—") +
                " | Repartidor: " + (documentoRepartidorAsignado == null ? "—" : documentoRepartidorAsignado);
    }
}
