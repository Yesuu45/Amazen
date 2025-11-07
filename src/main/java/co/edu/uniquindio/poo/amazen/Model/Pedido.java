package co.edu.uniquindio.poo.amazen.Model;

import co.edu.uniquindio.poo.amazen.Model.Estado.*;

/**
 * Clase Pedido (Contexto del patrón State).
 * Mantiene referencia al estado actual del pedido
 * y delega las acciones al estado correspondiente.
 */
public class Pedido {

    private String id;
    private CarritoDeCompras carrito;
    private EstadoPedido estado;

    // ✅ Conservamos tu constructor original
    public Pedido(String id, CarritoDeCompras carrito) {
        this.id = id;
        this.carrito = carrito;
        this.estado = new EstadoPagado(this); // Estado inicial
    }

    // ---- Getters ----
    public String getId() {
        return id;
    }

    public CarritoDeCompras getCarrito() {
        return carrito;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    // ---- Cambiar estado ----
    public void cambiarEstado(EstadoPedido nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("❌ El nuevo estado no puede ser null.");
        }
        this.estado = nuevoEstado;
        System.out.println("🔄 Estado cambiado a: " + nuevoEstado);
    }

    // ---- Acciones delegadas al estado actual ----
    public void pagar() {
        if (estado != null) estado.pagar();
    }

    public void verificarPago() {
        if (estado != null) estado.VerificacionPago();
    }

    public void empaquetar() {
        if (estado != null) estado.Empaquetado();
    }

    public void enviar() {
        if (estado != null) estado.Enviado();
    }

    public void entregar() {
        if (estado != null) estado.Entregado();
    }

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

    // ---- Calcular total ----
    public double calcularTotal() {
        return carrito != null ? carrito.calcularTotal() : 0.0;
    }

    // ---- Mostrar estado actual ----
    @Override
    public String toString() {
        return "🧾 Pedido " + id + " | Estado actual: " +
                (estado != null ? estado.toString() : "Sin estado");
    }
}
