package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.HistorialPedido;
import co.edu.uniquindio.poo.amazen.Model.Pedido;

import java.util.List;
import java.util.Optional;

/**
 * Gestiona el cambio de estado de un pedido usando el historial central.
 */
public class GestorEstadosController {

    /** Pedido actualmente seleccionado para operaciones de estado. */
    private Pedido pedido;

    /**
     * Define el pedido sobre el cual se aplicarán las acciones de estado.
     *
     * @param pedido pedido seleccionado
     */
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    /**
     * Obtiene todos los pedidos registrados en el historial global.
     *
     * @return lista de pedidos
     */
    public List<Pedido> obtenerPedidos() {
        return HistorialPedido.getInstance().obtenerPedidos();
    }

    /**
     * Busca un pedido por su identificador.
     *
     * @param id identificador del pedido
     * @return {@link Optional} con el pedido si existe
     */
    public Optional<Pedido> buscarPorId(String id) {
        return obtenerPedidos().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    /**
     * Ejecuta una acción sobre el pedido seleccionado,
     * delegando en el patrón State del propio pedido.
     *
     * @param accion nombre de la acción (ej. "verificacionpago", "empaquetado")
     * @return true si la acción fue aplicada sin error
     */
    public boolean cambiarEstado(String accion) {
        if (pedido == null) {
            System.out.println("⚠️ No hay un pedido seleccionado.");
            return false;
        }
        try {
            return pedido.procesar(accion);
        } catch (Exception e) {
            System.out.println("❌ Error al procesar el pedido: " + e.getMessage());
            return false;
        }
    }
}
