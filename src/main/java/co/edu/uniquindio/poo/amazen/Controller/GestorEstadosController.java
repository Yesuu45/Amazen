package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.HistorialPedido;
import co.edu.uniquindio.poo.amazen.Model.Pedido;

import java.util.List;

public class GestorEstadosController {
    private Pedido pedido; // Pedido actualmente seleccionado

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public List<Pedido> obtenerPedidos() {
        return HistorialPedido.getInstance().obtenerPedidos();
    }

    public boolean cambiarEstado(String accion) {
        if (pedido == null) {
            System.out.println("⚠️ No hay un pedido seleccionado.");
            return false;
        }

        try {
            boolean exito = pedido.procesar(accion);
            return exito;
        } catch (Exception e) {
            System.out.println("❌ Error al procesar el pedido: " + e.getMessage());
            return false;
        }
    }
}
