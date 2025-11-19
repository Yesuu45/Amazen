package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.Inventario;
import co.edu.uniquindio.poo.amazen.Model.Producto;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsula la lógica de negocio para consultar, buscar,
 * clonar y agregar productos al inventario.
 */
public class ProductoController {

    private final Inventario inventario;

    public ProductoController(Inventario inventario) {
        this.inventario = inventario;
    }

    /**
     * Obtiene todos los productos presentes en el inventario.
     *
     * @return lista completa de productos
     */
    public List<Producto> obtenerTodos() {
        return inventario.obtenerProductos();
    }

    /**
     * Busca productos por nombre, usando coincidencia parcial e ignorando mayúsculas.
     * Si el nombre es vacío o nulo, devuelve todos los productos.
     *
     * @param nombre criterio de búsqueda
     * @return lista de productos que coinciden
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
     * Clona un producto existente asignándole un nuevo ID generado por el inventario.
     *
     * @param original producto a clonar
     * @return clon creado o null si no se pudo clonar
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
     * Agrega un producto al inventario, validando que el ID no exista previamente.
     *
     * @param producto producto a agregar
     * @return true si se agregó correctamente
     */
    public boolean agregarProducto(Producto producto) {
        if (producto == null || producto.getId() == null) return false;

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
