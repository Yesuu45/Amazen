package co.edu.uniquindio.poo.amazen.Model;

import java.util.ArrayList;
import java.util.List;

public class CarritoDeCompras {
    private List<DetallePedido> detalles;

    public CarritoDeCompras() {
        detalles = new ArrayList<>();
    }

    public void agregarProducto(Producto producto, int cantidad) {
        for (int i = 0; i < detalles.size(); i++) {
            DetallePedido d = detalles.get(i);
            if (d.getProducto().getId().equals(producto.getId())) {
                d.setCantidad(d.getCantidad() + cantidad);
                return;
            }
        }
        detalles.add(new DetallePedido(producto, cantidad));
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public double calcularTotal() {
        double total = 0;
        for (int i = 0; i < detalles.size(); i++) {
            total += detalles.get(i).getSubtotal();
        }
        return total;
    }

    public void vaciar() {
        detalles.clear(); }
}
