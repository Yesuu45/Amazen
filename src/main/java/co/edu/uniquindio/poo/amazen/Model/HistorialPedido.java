package co.edu.uniquindio.poo.amazen.Model;

import java.util.ArrayList;
import java.util.List;

public class HistorialPedido {
    private static HistorialPedido instancia;
    private List<Pedido> pedidos;

    private HistorialPedido() {
        pedidos = new ArrayList<>();
    }

    public static HistorialPedido getInstance() {
        if (instancia == null) {
            instancia = new HistorialPedido();
        }
        return instancia;
    }

    public void registrarPedido(Pedido pedido) {
        pedidos.add(pedido);
    }

    public List<Pedido> obtenerPedidos() {
        return pedidos;
    }

    public List<Pedido> getPedidos() {
        // alias de obtenerPedidos()
        return obtenerPedidos();
    }

    public void agregarPedido(Pedido pedido) {
        // alias de registrarPedido()
        registrarPedido(pedido);
    }

    public boolean eliminarPedidoPorId(String id) {
        if (id == null) return false;
        return pedidos.removeIf(p -> id.equals(p.getId()));
    }

}
