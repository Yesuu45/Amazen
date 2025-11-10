package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.Inventario;
import co.edu.uniquindio.poo.amazen.Model.Producto;

import java.util.ArrayList;
import java.util.List;

public class ProductoController {
    private final Inventario inventario;

    public ProductoController(Inventario inventario) {
        this.inventario = inventario;
    }

    /**
     * Retorna todos los productos disponibles en el inventario.
     */
    public List<Producto> obtenerTodos() {
        return inventario.obtenerProductos();
    }

    /**
     * Busca productos por nombre (coincidencia parcial, sin importar mayúsculas/minúsculas).
     */
    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> resultados = new ArrayList<>();
        if (nombre == null || nombre.trim().isEmpty()) {
            resultados.addAll(inventario.obtenerProductos());
            return resultados;
        }

        String criterio = nombre.trim().toLowerCase();
        for (Producto p : inventario.obtenerProductos()) {
            if (p.getNombre() != null && p.getNombre().toLowerCase().contains(criterio)) {
                resultados.add(p);
            }
        }
        return resultados;
    }

    /**
     * Clona un producto, asignándole un nuevo ID generado automáticamente.
     */
    public Producto clonarProducto(Producto original) {
        if (original == null) return null;

        Producto clon = original.clone();
        if (clon == null) return null;

        String nuevoId = inventario.generarIdClon(original.getId());
        clon.setId(nuevoId);
        inventario.agregarProducto(clon);
        return clon;
    }

    /**
     * Agrega un nuevo producto al inventario, si el ID no está repetido.
     */
    public boolean agregarProducto(Producto producto) {
        if (producto == null || producto.getId() == null) return false;

        // Verificar duplicados
        for (Producto existente : inventario.obtenerProductos()) {
            if (existente.getId().equalsIgnoreCase(producto.getId())) {
                System.out.println("⚠️ Ya existe un producto con el ID: " + producto.getId());
                return false;
            }
        }

        inventario.agregarProducto(producto);
        return true;
    }
}
