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

public class GestorPedidosController {

    // Lista local (si quieres mostrarla en alguna vista puntual)
    private final List<Pedido> pedidos;

    public GestorPedidosController() {
        pedidos = new ArrayList<>();
    }

    /**
     * Crea un pedido a partir del carrito actual.
     * - Asigna un ID incremental.
     * - Intenta asociar el documento del cliente logueado.
     * - Registra el pedido también en Amazen/HistorialPedido.
     */
    public Pedido crearPedido(CarritoDeCompras carrito) {
        if (carrito == null) {
            throw new IllegalArgumentException("Carrito requerido para crear pedido");
        }

        String id = "PED" + (pedidos.size() + 1);

        // 🔹 Intentar obtener al usuario logueado
        String documentoCliente = null;
        try {
            SesionUsuario sesion = SesionUsuario.instancia();
            Persona p = sesion != null ? sesion.getPersona() : null;
            if (p != null && p.getDocumento() != null) {
                documentoCliente = p.getDocumento();
            }
        } catch (Throwable ignore) {
            // Si algo falla, simplemente se crea sin documentoCliente
        }

        // 🔹 Crear pedido usando el constructor adecuado
        Pedido p;
        if (documentoCliente != null) {
            p = new Pedido(id, carrito, documentoCliente);
        } else {
            p = new Pedido(id, carrito); // fallback (por si no hay sesión)
        }

        // Guardar en lista local
        pedidos.add(p);

        // 🔹 MUY IMPORTANTE: registrar en Amazen → HistorialPedido
        Amazen.getInstance().addPedido(p);

        return p;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public Optional<Pedido> buscarPorId(String id) {
        return pedidos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    // ===== RF-012: asignación y reasignación de repartidor =====
    public void asignarRepartidor(String idPedido, String documentoRepartidor) {
        Pedido pedido = buscarPorId(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + idPedido));

        Repartidor repartidor = buscarRepartidor(documentoRepartidor);
        if (repartidor.getDisponibilidad() == null
                || repartidor.getDisponibilidad() == Disponibilidad.INACTIVO) {
            throw new IllegalStateException("Repartidor no disponible");
        }
        pedido.asignarRepartidor(repartidor.getDocumento());
        // opcional: marcar EN_RUTA al asignar
        // repartidor.setDisponibilidad(Disponibilidad.EN_RUTA);
    }

    public void reasignarRepartidor(String idPedido, String nuevoDocumentoRepartidor) {
        asignarRepartidor(idPedido, nuevoDocumentoRepartidor);
    }

    // ===== RF-012: incidencias =====
    public void registrarIncidencia(String idPedido, String zona, String tipo, String detalle) {
        Pedido pedido = buscarPorId(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + idPedido));
        pedido.registrarIncidencia(new Incidencia(zona, tipo, detalle));
    }

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
