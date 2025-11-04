package co.edu.uniquindio.poo.amazen.Model;

import java.util.ArrayList;
import java.util.List;

public class Inventario {
    private static Inventario instancia;
    private List<Producto> productos;

    private Inventario() {
        productos = new ArrayList<>();
    }

    public static Inventario getInstance() {
        if (instancia == null) {
            instancia = new Inventario();
        }
        return instancia;
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public List<Producto> obtenerProductos() {
        return productos;
    }

    public Producto obtenerProductoPorId(String id) {
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    public void inicializarData() {
        if (!productos.isEmpty()) return;
        agregarProducto(new Producto("P001", "Laptop", 899.99, true));
        agregarProducto(new Producto("P002", "Smartphone", 499.99, true));
        agregarProducto(new Producto("P003", "Tablet", 299.99, true));
        agregarProducto(new Producto("P004", "Auriculares", 99.99, true));
    }

    public String generarIdClon(String baseId) {
        int sufijo = 1;
        String candidato;
        while (true) {
            candidato = baseId + "-CLON" + sufijo;
            if (obtenerProductoPorId(candidato) == null) return candidato;
            sufijo++;
        }
    }
}
