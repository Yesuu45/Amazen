package co.edu.uniquindio.poo.amazen.Model;

import co.edu.uniquindio.poo.amazen.Model.Estado.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Pedido (Contexto del patrón State).
 * Mantiene referencia al estado actual del pedido
 * y delega las acciones al estado correspondiente.
 */
public class Pedido {

    private String id;
    private CarritoDeCompras carrito;
    private EstadoPedido estado;

    // ===== RF-012: asignación y trazabilidad =====
    private String documentoRepartidorAsignado;     // null si no ha sido asignado
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaEntrega;
    private final List<Incidencia> incidencias = new ArrayList<>();

    //  constructor
    public Pedido(String id, CarritoDeCompras carrito) {
        this.id = id;
        this.carrito = carrito;
        this.estado = new EstadoPagado(this); // Estado inicial (según tu flujo actual)
        this.fechaCreacion = LocalDateTime.now();
    }

    // ---- Getters/Setters básicos ----
    public String getId() { return id; }
    public CarritoDeCompras getCarrito() { return carrito; }
    public EstadoPedido getEstado() { return estado; }
    public String getDocumentoRepartidorAsignado() { return documentoRepartidorAsignado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public List<Incidencia> getIncidencias() { return incidencias; }

    // ---- Cambiar estado ----
    public void cambiarEstado(EstadoPedido nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("❌ El nuevo estado no puede ser null.");
        }
        this.estado = nuevoEstado;
        // marca de entrega
        if (nuevoEstado instanceof EstadoEntregado) {
            this.fechaEntrega = LocalDateTime.now();
        }
        System.out.println("🔄 Estado cambiado a: " + nuevoEstado);
    }

    // ---- Acciones delegadas al estado actual ----
    public void pagar()              { if (estado != null) estado.pagar(); }
    public void verificarPago()      { if (estado != null) estado.VerificacionPago(); }
    public void empaquetar()         { if (estado != null) estado.Empaquetado(); }
    public void enviar()             { if (estado != null) estado.Enviado(); }
    public void entregar()           { if (estado != null) estado.Entregado(); }

    /**
     * Procesa una acción genérica (por nombre),
     * y captura errores si el estado no lo permite.
     */
    public boolean procesar(String accion) {
        if (estado != null) {
            try {
                estado.ejecutarAccion(accion);
                return true;
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("🚫 Acción no válida: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    // ---- Cálculo ----
    public double calcularTotal() { return carrito != null ? carrito.calcularTotal() : 0.0; }

    // ===== RF-012: asignación / reasignación / incidencias =====
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

    // ---- Mostrar estado actual ----
    @Override
    public String toString() {
        return "🧾 Pedido " + id + " | Estado actual: " +
                (estado != null ? estado.toString() : "Sin estado") +
                " | Repartidor: " + (documentoRepartidorAsignado == null ? "—" : documentoRepartidorAsignado);
    }
}
