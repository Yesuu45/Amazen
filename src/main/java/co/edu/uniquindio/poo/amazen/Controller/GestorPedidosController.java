package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.CarritoDeCompras;
import co.edu.uniquindio.poo.amazen.Model.Disponibilidad;
import co.edu.uniquindio.poo.amazen.Model.Incidencia;
import co.edu.uniquindio.poo.amazen.Model.Pedido;
import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;
import co.edu.uniquindio.poo.amazen.Model.Persona.Repartidor;
import co.edu.uniquindio.poo.amazen.Model.Persona.SesionUsuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Gestiona la creación y administración de pedidos:
 * creación desde carrito, asignación de repartidor e incidencias.
 */
public class GestorPedidosController {

    /** Lista local de pedidos creados con este gestor. */
    private final List<Pedido> pedidos;

    public GestorPedidosController() {
        this.pedidos = new ArrayList<>();
    }

    /**
     * Crea un nuevo pedido a partir del carrito actual.
     * Genera un ID simple, intenta asociar el cliente logueado
     * y registra el pedido en Amazen (historial central).
     *
     * @param carrito carrito de compras de origen
     * @return pedido creado
     */
    public Pedido crearPedido(CarritoDeCompras carrito) {
        if (carrito == null) {
            throw new IllegalArgumentException("Carrito requerido para crear pedido");
        }

        String id = "PED" + (pedidos.size() + 1);

        String documentoCliente = null;
        try {
            SesionUsuario sesion = SesionUsuario.instancia();
            Persona p = sesion != null ? sesion.getPersona() : null;
            if (p != null && p.getDocumento() != null) {
                documentoCliente = p.getDocumento();
            }
        } catch (Throwable ignore) {
            // Si falla la sesión, se crea sin documento de cliente
        }

        Pedido p = (documentoCliente != null)
                ? new Pedido(id, carrito, documentoCliente)
                : new Pedido(id, carrito);

        pedidos.add(p);
        Amazen.getInstance().addPedido(p);

        return p;
    }

    /**
     * Devuelve los pedidos creados por este gestor.
     *
     * @return lista de pedidos
     */
    public List<Pedido> getPedidos() {
        return pedidos;
    }

    /**
     * Busca un pedido local por ID.
     *
     * @param id identificador del pedido
     * @return {@link Optional} con el pedido si existe
     */
    public Optional<Pedido> buscarPorId(String id) {
        return pedidos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    /**
     * Asigna un repartidor disponible a un pedido.
     *
     * @param idPedido           identificador del pedido
     * @param documentoRepartidor documento del repartidor
     */
    public void asignarRepartidor(String idPedido, String documentoRepartidor) {
        Pedido pedido = buscarPorId(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + idPedido));

        Repartidor repartidor = buscarRepartidor(documentoRepartidor);
        if (repartidor.getDisponibilidad() == null
                || repartidor.getDisponibilidad() == Disponibilidad.INACTIVO) {
            throw new IllegalStateException("Repartidor no disponible");
        }
        pedido.asignarRepartidor(repartidor.getDocumento());
    }

    /**
     * Reemplaza el repartidor asignado a un pedido por otro.
     *
     * @param idPedido               identificador del pedido
     * @param nuevoDocumentoRepartidor documento del nuevo repartidor
     */
    public void reasignarRepartidor(String idPedido, String nuevoDocumentoRepartidor) {
        asignarRepartidor(idPedido, nuevoDocumentoRepartidor);
    }

    /**
     * Registra una incidencia asociada a un pedido.
     *
     * @param idPedido identificador del pedido
     * @param zona     zona de la incidencia
     * @param tipo     tipo de incidencia
     * @param detalle  detalle descriptivo
     */
    public void registrarIncidencia(String idPedido, String zona, String tipo, String detalle) {
        Pedido pedido = buscarPorId(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + idPedido));
        pedido.registrarIncidencia(new Incidencia(zona, tipo, detalle));
    }

    /**
     * Busca y valida que el documento pertenezca a un repartidor.
     *
     * @param documento documento de la persona
     * @return repartidor encontrado
     */
    private Repartidor buscarRepartidor(String documento) {
        Persona p = Amazen.getInstance().getListaPersonas().stream()
                .filter(per -> per.getDocumento().equals(documento))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe persona con documento " + documento));

        if (!(p instanceof Repartidor r)) {
            throw new IllegalArgumentException("El documento no pertenece a un repartidor");
        }
        return r;
    }
}
