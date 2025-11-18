package co.edu.uniquindio.poo.amazen.Model;

import java.util.ArrayList;
import java.util.List;

public class CarritoDeCompras {

    private List<DetallePedido> detalles;

    public CarritoDeCompras() {
        this.detalles = new ArrayList<>();
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    // Agregar productos al carrito
    public void agregarProducto(Producto producto, int cantidad) {
        detalles.add(new DetallePedido(producto, cantidad));
    }

    // Calcular subtotal total
    public double calcularTotal() {
        return detalles.stream()
                .mapToDouble(DetallePedido::getSubtotal)
                .sum();
    }

    // ==========================================================
    // 🔥 NECESARIOS PARA RF-003 (COTIZACIÓN DE ENVÍO)
    // ==========================================================

    /** Calcula el peso TOTAL del carrito */
    public double calcularPesoTotal() {
        return detalles.stream()
                .mapToDouble(d -> d.getProducto().getPesoKg() * d.getCantidad())
                .sum();
    }

    /** Calcula el volumen TOTAL del carrito */
    public double calcularVolumenTotal() {
        return detalles.stream()
                .mapToDouble(d -> d.getProducto().getVolumenCm3() * d.getCantidad())
                .sum();
    }

}
