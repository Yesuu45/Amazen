package co.edu.uniquindio.poo.amazen.Model;

import co.edu.uniquindio.poo.amazen.Model.Estado.EstadoPagado;
import co.edu.uniquindio.poo.amazen.Model.Estado.EstadoPedido;

public class Pedido {
    private String id;
    private CarritoDeCompras carrito;
    private EstadoPedido estado;

    public Pedido(String id, CarritoDeCompras carrito) {
        this.id = id;
        this.carrito = carrito;
        this.estado = new EstadoPagado(this); // Estado inicial
    }

    // Getters
    public String getId() {
        return id;
    }

    public CarritoDeCompras getCarrito() {
        return carrito;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    // Cambiar el estado del pedido
    public void cambiarEstado(EstadoPedido nuevoEstado) {
        this.estado = nuevoEstado;
    }

    // Calcular total
    public double calcularTotal() {
        return carrito.calcularTotal();
    }

    public boolean procesar(String accion) {
        if (estado != null) {
            try {
                estado.ejecutarAccion(accion); // delega al estado actual
                return true;
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("🚫 Acción no válida: " + e.getMessage());
                return false;
            }
        }
        return false;
    }
}
