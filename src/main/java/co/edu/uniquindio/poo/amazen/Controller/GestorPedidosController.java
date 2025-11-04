package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.CarritoDeCompras;
import co.edu.uniquindio.poo.amazen.Model.Pedido;

import java.util.ArrayList;
import java.util.List;

public class GestorPedidosController {
    private final List<Pedido> pedidos;

    public GestorPedidosController() {
        pedidos = new ArrayList<>();
    }

    public Pedido crearPedido(CarritoDeCompras carrito) {
        String id = "PED" + (pedidos.size() + 1);
        Pedido p = new Pedido(id, carrito);
        pedidos.add(p);
        return p;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }
}
