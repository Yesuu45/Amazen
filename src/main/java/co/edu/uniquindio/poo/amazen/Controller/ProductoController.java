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

    public List<Producto> obtenerTodos() {
        return inventario.obtenerProductos();
    }

    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> res = new ArrayList<>();
        String criterio = nombre == null ? "" : nombre.trim().toLowerCase();
        List<Producto> todos = inventario.obtenerProductos();
        for (int i = 0; i < todos.size(); i++) {
            Producto p = todos.get(i);
            String n = p.getNombre() == null ? "" : p.getNombre().toLowerCase();
            if (n.contains(criterio)) res.add(p);
        }
        return res;
    }

    public Producto clonarProducto(Producto original) {
        if (original == null) return null;
        Producto clon = original.clone();
        if (clon == null) return null;
        String nuevoId = inventario.generarIdClon(original.getId());
        clon.setId(nuevoId);
        inventario.agregarProducto(clon);
        return clon;
    }
}
