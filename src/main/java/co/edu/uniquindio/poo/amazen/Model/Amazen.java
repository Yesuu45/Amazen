package co.edu.uniquindio.poo.amazen.Model;

import co.edu.uniquindio.poo.amazen.Model.Persona.*;
import co.edu.uniquindio.poo.amazen.Service.AdminFileService;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Amazen {

    private final Inventario inventario;
    private final HistorialPedido historialPedido;
    private final TiendaSession tiendaSession;
    private final List<Persona> listaPersonas;

    private static Amazen instancia;

    private Amazen() {
        this.inventario = Inventario.getInstance();
        this.historialPedido = HistorialPedido.getInstance();
        this.tiendaSession = TiendaSession.getInstance();
        this.listaPersonas = new ArrayList<>();

        cargarAdminUnico();
        guardarAdmin();
    }

    public static Amazen getInstance() {
        if (instancia == null) {
            instancia = new Amazen();
        }
        return instancia;
    }

    public Persona buscarPersonaPorDocumento(String documento) {
        return listaPersonas.stream()
                .filter(p -> p.getDocumento().equalsIgnoreCase(documento))
                .findFirst()
                .orElse(null);
    }

    /**
     * Crea SOLO el administrador del sistema.
     */
    private void cargarAdminUnico() {

        if (buscarPersonaPorDocumento("111") == null) {
            Administrador admin = Administrador.builder()
                    .nombre("Andrés")
                    .apellido("García")
                    .email("admin1@amazen.com")
                    .telefono("1234567890")
                    .celular("3001234567")
                    .documento("111")
                    .contrasena("123")
                    .direcciones(List.of("Calle 1 #1-01"))
                    .id(UUID.randomUUID())
                    .build();

            listaPersonas.add(admin);
        }

        System.out.println("🔥 Sistema iniciado con un único administrador.");
    }

    /**
     * Guarda únicamente el admin.
     */
    private void guardarAdmin() {
        for (Persona persona : listaPersonas) {
            if (persona instanceof Administrador admin) {
                AdminFileService.guardarAdministrador(admin);
            }
        }
    }

    // ------- Pedidos -------

    public List<Pedido> getListaPedidos() {
        List<Pedido> base = historialPedido.getPedidos();
        return base == null ? List.of() : Collections.unmodifiableList(base);
    }

    public void addPedido(Pedido pedido) {
        if (pedido == null) throw new IllegalArgumentException("Pedido requerido");
        historialPedido.agregarPedido(pedido);
    }

    public boolean removePedidoById(String id) {
        if (id == null) return false;
        return historialPedido.eliminarPedidoPorId(id);
    }
}
