package co.edu.uniquindio.poo.amazen.Model;

import co.edu.uniquindio.poo.amazen.Model.Persona.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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

        cargarDatosIniciales();
    }

    public static Amazen getInstance() {
        if (instancia == null) {
            instancia = new Amazen();
        }
        return instancia;
    }

    public Persona buscarPersonaPorDocumento(String documento) {
        for (Persona p : listaPersonas) {
            if (p.getDocumento().equals(documento)) {
                return p;
            }
        }
        return null;
    }

    public void agregarPersona(Persona persona) {
        listaPersonas.add(persona);
    }

    /**
     * Carga usuarios iniciales para pruebas
     */
    private void cargarDatosIniciales() {
        // Administradores
        Administrador admin1 = Administrador.builder()
                .nombre("Andrés").apellido("García")
                .email("admin1@amazen.com").telefono("1234567890")
                .direccion("Calle 1 #1-01").celular("3001234567")
                .documento("11111111").contrasena("admin123")
                .id(UUID.randomUUID())
                .build();

        Administrador admin2 = Administrador.builder()
                .nombre("Laura").apellido("Martínez")
                .email("admin2@amazen.com").telefono("0987654321")
                .direccion("Calle 2 #2-02").celular("3007654321")
                .documento("22222222").contrasena("admin456")
                .id(UUID.randomUUID())
                .build();

        // Repartidores
        Repartidor repartidor1 = Repartidor.builder()
                .nombre("Carlos").apellido("López")
                .email("repartidor1@amazen.com").telefono("1122334455")
                .direccion("Calle 3 #3-03").celular("3001122334")
                .documento("33333333").contrasena("repartidor123")
                .ZonaCobertura("Norte")
                .disponibilidad(Disponibilidad.ACTIVO) // <--- NUEVO
                .id(UUID.randomUUID())
                .build();

        Repartidor repartidor2 = Repartidor.builder()
                .nombre("Sofía").apellido("Ramírez")
                .email("repartidor2@amazen.com").telefono("2233445566")
                .direccion("Calle 4 #4-04").celular("3002233445")
                .documento("44444444").contrasena("repartidor456")
                .ZonaCobertura("Sur")
                .disponibilidad(Disponibilidad.INACTIVO) // <--- NUEVO
                .id(UUID.randomUUID())
                .build();


        // Usuarios comunes (clientes)
        Usuario cliente1 = Usuario.builder()
                .nombre("Juan").apellido("Pérez")
                .email("cliente1@amazen.com").telefono("3344556677")
                .direccion("Calle 5 #5-05").celular("3003344556")
                .documento("55555555").contrasena("cliente123")
                .id(UUID.randomUUID())
                .build();

        Usuario cliente2 = Usuario.builder()
                .nombre("Ana").apellido("Gómez")
                .email("cliente2@amazen.com").telefono("4455667788")
                .direccion("Calle 6 #6-06").celular("3004455667")
                .documento("66666666").contrasena("cliente456")
                .id(UUID.randomUUID())
                .build();

        // Agregar personas a la lista
        listaPersonas.add(admin1);
        listaPersonas.add(admin2);
        listaPersonas.add(repartidor1);
        listaPersonas.add(repartidor2);
        listaPersonas.add(cliente1);
        listaPersonas.add(cliente2);

        System.out.println("✅ Usuarios iniciales cargados correctamente en Amazen.");
    }
}
