package co.edu.uniquindio.poo.amazen.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Carrito de compras asociado a un pedido.
 * Mantiene los detalles de productos y permite calcular totales.
 */
public class CarritoDeCompras {

    private List<DetallePedido> detalles;

    public CarritoDeCompras() {
        this.detalles = new ArrayList<>();
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    /**
     * Agrega un producto al carrito.
     *
     * @param producto producto a agregar
     * @param cantidad cantidad solicitada
     */
    public void agregarProducto(Producto producto, int cantidad) {
        detalles.add(new DetallePedido(producto, cantidad));
    }

    /**
     * Calcula el valor total del carrito.
     *
     * @return suma de los subtotales
     */
    public double calcularTotal() {
        return detalles.stream()
                .mapToDouble(DetallePedido::getSubtotal)
                .sum();
    }

    /**
     * Calcula el peso total del carrito (RF-003).
     *
     * @return peso total en kilogramos
     */
    public double calcularPesoTotal() {
        return detalles.stream()
                .mapToDouble(d -> d.getProducto().getPesoKg() * d.getCantidad())
                .sum();
    }

    /**
     * Calcula el volumen total del carrito (RF-003).
     *
     * @return volumen total en cm³
     */
    public double calcularVolumenTotal() {
        return detalles.stream()
                .mapToDouble(d -> d.getProducto().getVolumenCm3() * d.getCantidad())
                .sum();
    }

}
