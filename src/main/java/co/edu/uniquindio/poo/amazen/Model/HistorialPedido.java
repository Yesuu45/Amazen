package co.edu.uniquindio.poo.amazen.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Historial global de pedidos del sistema.
 * Implementado como singleton.
 */
public class HistorialPedido {

    private static HistorialPedido instancia;
    private List<Pedido> pedidos;

    private HistorialPedido() {
        pedidos = new ArrayList<>();
    }

    /**
     * Obtiene la instancia única del historial de pedidos.
     */
    public static HistorialPedido getInstance() {
        if (instancia == null) {
            instancia = new HistorialPedido();
        }
        return instancia;
    }

    /**
     * Registra un nuevo pedido en el historial.
     */
    public void registrarPedido(Pedido pedido) {
        pedidos.add(pedido);
    }

    /**
     * Obtiene la lista de pedidos registrados.
     */
    public List<Pedido> obtenerPedidos() {
        return pedidos;
    }

    /**
     * Alias de {@link #obtenerPedidos()} para compatibilidad.
     */
    public List<Pedido> getPedidos() {
        return obtenerPedidos();
    }

    /**
     * Alias de {@link #registrarPedido(Pedido)} para compatibilidad.
     */
    public void agregarPedido(Pedido pedido) {
        registrarPedido(pedido);
    }

    /**
     * Elimina un pedido por su id.
     *
     * @param id identificador del pedido
     * @return {@code true} si se eliminó al menos un pedido
     */
    public boolean eliminarPedidoPorId(String id) {
        if (id == null) return false;
        return pedidos.removeIf(p -> id.equals(p.getId()));
    }

}
