package co.edu.uniquindio.poo.amazen.Model;

import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Amazen {

    private final Inventario inventario;
    private final HistorialPedido historialPedido;
    private final TiendaSession tiendaSession;
    private final List<Persona> listaPersonas;

    // Singleton (opcional)
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

    public void registrarPersona(Persona persona) {
        listaPersonas.add(persona);
    }

    private void cargarDatosIniciales() {
        // Aquí podrías registrar datos de prueba
        // listaPersonas.add(new Administrador("Carlos", "admin123", "carlos@amaZen.com", "1001"));
        // listaPersonas.add(new Usuario("Maria", "maria123", "maria@correo.com", "1002"));
    }
}
