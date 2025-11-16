package co.edu.uniquindio.poo.amazen.Model;

import co.edu.uniquindio.poo.amazen.Model.Persona.*;
import co.edu.uniquindio.poo.amazen.Service.UsuarioFileService;
import co.edu.uniquindio.poo.amazen.Service.AdminFileService;
import co.edu.uniquindio.poo.amazen.Service.RepartidorFileService;
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

    // ✅ Constructor privado (Singleton)
    private Amazen() {
        this.inventario = Inventario.getInstance();
        this.historialPedido = HistorialPedido.getInstance();
        this.tiendaSession = TiendaSession.getInstance();
        this.listaPersonas = new ArrayList<>();

        // ✅ Primero carga desde archivos si existen
        cargarPersonasDesdeArchivos();

        // ✅ Luego carga siempre los datos quemados (para pruebas)
        cargarDatosIniciales();

        // ✅ Guarda los datos quemados en archivos si aún no existen
        guardarDatosInicialesEnArchivos();
    }

    // ✅ Obtener instancia única
    public static Amazen getInstance() {
        if (instancia == null) {
            instancia = new Amazen();
        }
        return instancia;
    }

    // 🔍 Buscar persona por documento
    public Persona buscarPersonaPorDocumento(String documento) {
        return listaPersonas.stream()
                .filter(p -> p.getDocumento().equalsIgnoreCase(documento))
                .findFirst()
                .orElse(null);
    }

    // ➕ Agregar persona al sistema y al archivo correspondiente
    public void agregarPersona(Persona persona) {
        listaPersonas.add(persona);
        guardarPersonaEnArchivo(persona);
    }

    // 💾 Guardar persona en el archivo según su tipo
    private void guardarPersonaEnArchivo(Persona persona) {
        if (persona instanceof Administrador admin) {
            AdminFileService.guardarAdministrador(admin);
        } else if (persona instanceof Repartidor repartidor) {
            RepartidorFileService.guardarRepartidor(repartidor);
        } else if (persona instanceof Usuario usuario) {
            UsuarioFileService.guardarUsuario(usuario);
        }
    }

    /**
     * ✅ Carga personas desde los archivos.
     */
    private void cargarPersonasDesdeArchivos() {
        List<Usuario> usuariosArchivo = UsuarioFileService.cargarUsuarios();
        List<Administrador> adminsArchivo = AdminFileService.cargarAdministradores();
        List<Repartidor> repartidoresArchivo = RepartidorFileService.cargarRepartidores();

        listaPersonas.addAll(adminsArchivo);
        listaPersonas.addAll(repartidoresArchivo);
        listaPersonas.addAll(usuariosArchivo);

        System.out.println("✅ Datos cargados desde archivos (" +
                (adminsArchivo.size() + repartidoresArchivo.size() + usuariosArchivo.size()) + " personas)");
    }

    /**
     * ✅ Crea usuarios, repartidores y administradores quemados.
     */
    private void cargarDatosIniciales() {
        if (buscarPersonaPorDocumento("111") == null) {
            Administrador admin1 = Administrador.builder()
                    .nombre("Andrés")
                    .apellido("García")
                    .email("admin1@amazen.com")
                    .telefono("1234567890")
                    .direccion("Calle 1 #1-01")
                    .celular("3001234567")
                    .documento("111")
                    .contrasena("123")
                    .id(UUID.randomUUID())
                    .build();
            listaPersonas.add(admin1);
        }

        if (buscarPersonaPorDocumento("222") == null) {
            Administrador admin2 = Administrador.builder()
                    .nombre("Laura")
                    .apellido("Martínez")
                    .email("admin2@amazen.com")
                    .telefono("0987654321")
                    .direccion("Calle 2 #2-02")
                    .celular("3007654321")
                    .documento("222")
                    .contrasena("123")
                    .id(UUID.randomUUID())
                    .build();
            listaPersonas.add(admin2);
        }

        if (buscarPersonaPorDocumento("333") == null) {
            Repartidor repartidor1 = Repartidor.builder()
                    .nombre("Carlos")
                    .apellido("López")
                    .email("repartidor1@amazen.com")
                    .telefono("1122334455")
                    .direccion("Calle 3 #3-03")
                    .celular("3001122334")
                    .documento("333")
                    .contrasena("123")
                    .zonaCobertura("Norte")
                    .disponibilidad(Disponibilidad.ACTIVO)
                    .id(UUID.randomUUID())
                    .build();
            listaPersonas.add(repartidor1);
        }

        if (buscarPersonaPorDocumento("444") == null) {
            Repartidor repartidor2 = Repartidor.builder()
                    .nombre("Sofía")
                    .apellido("Ramírez")
                    .email("repartidor2@amazen.com")
                    .telefono("2233445566")
                    .direccion("Calle 4 #4-04")
                    .celular("3002233445")
                    .documento("444")
                    .contrasena("123")
                    .zonaCobertura("Sur")
                    .disponibilidad(Disponibilidad.INACTIVO)
                    .id(UUID.randomUUID())
                    .build();
            listaPersonas.add(repartidor2);
        }

        if (buscarPersonaPorDocumento("555") == null) {
            Usuario cliente1 = Usuario.builder()
                    .nombre("Juan")
                    .apellido("Pérez")
                    .email("cliente1@amazen.com")
                    .telefono("3344556677")
                    .direccion("Calle 5 #5-05")
                    .celular("3003344556")
                    .documento("555")
                    .contrasena("123")
                    .id(UUID.randomUUID())
                    .build();
            listaPersonas.add(cliente1);
        }

        if (buscarPersonaPorDocumento("666") == null) {
            Usuario cliente2 = Usuario.builder()
                    .nombre("Ana")
                    .apellido("Gómez")
                    .email("cliente2@amazen.com")
                    .telefono("4455667788")
                    .direccion("Calle 6 #6-06")
                    .celular("3004455667")
                    .documento("666")
                    .contrasena("123")
                    .id(UUID.randomUUID())
                    .build();
            listaPersonas.add(cliente2);
        }

        System.out.println("🔥 Datos quemados cargados en memoria (" + listaPersonas.size() + " personas)");
    }

    /**
     * ✅ Guarda los datos actuales (incluidos los quemados) en sus archivos correspondientes.
     */
    private void guardarDatosInicialesEnArchivos() {
        for (Persona persona : listaPersonas) {
            guardarPersonaEnArchivo(persona);
        }
        System.out.println("💾 Datos guardados en archivos correctamente.");
    }

    // === MÉTODOS DEL HISTORIAL ===

    public List<Pedido> getListaPedidos() {
        List<Pedido> base = historialPedido.getPedidos();
        return base == null ? List.of() : Collections.unmodifiableList(base);
    }

    public void addPedido(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido requerido");
        }
        historialPedido.agregarPedido(pedido);
    }

    public boolean removePedidoById(String id) {
        if (id == null) return false;
        return historialPedido.eliminarPedidoPorId(id);
    }


}
