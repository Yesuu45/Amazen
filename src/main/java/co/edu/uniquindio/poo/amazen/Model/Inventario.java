package co.edu.uniquindio.poo.amazen.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Inventario de productos disponibles en la tienda.
 * Implementado como singleton.
 */
public class Inventario {

    private static Inventario instancia;
    private List<Producto> productos;

    private Inventario() {
        productos = new ArrayList<>();
    }

    /**
     * Obtiene la instancia única del inventario.
     */
    public static Inventario getInstance() {
        if (instancia == null) {
            instancia = new Inventario();
        }
        return instancia;
    }

    /**
     * Agrega un producto al inventario.
     */
    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    /**
     * Devuelve la lista de productos registrados.
     */
    public List<Producto> obtenerProductos() {
        return productos;
    }

    /**
     * Busca un producto por id.
     *
     * @param id identificador del producto
     * @return producto si existe, o {@code null} si no
     */
    public Producto obtenerProductoPorId(String id) {
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    /**
     * Inicializa el inventario con datos de ejemplo si está vacío.
     */
    public void inicializarData() {
        if (!productos.isEmpty()) return;

        agregarProducto(new Producto("P001", "Laptop",      899.99, true, 2.0, 3000));
        agregarProducto(new Producto("P002", "Smartphone",  499.99, true, 0.3,  400));
        agregarProducto(new Producto("P003", "Tablet",      299.99, true, 0.5,  800));
        agregarProducto(new Producto("P004", "Auriculares",  99.99, true, 0.2,  200));
    }

    /**
     * Genera un id nuevo para un clon de un producto base.
     *
     * @param baseId id original
     * @return id generado que no exista en el inventario
     */
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
