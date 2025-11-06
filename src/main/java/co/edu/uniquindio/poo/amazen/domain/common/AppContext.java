package co.edu.uniquindio.poo.amazen.domain.common;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import co.edu.uniquindio.poo.amazen.domain.usuario.Usuario;
import co.edu.uniquindio.poo.amazen.domain.reparto.Repartidor;
import co.edu.uniquindio.poo.amazen.domain.envio.Envio;
import co.edu.uniquindio.poo.amazen.domain.tarifa.TarifaBase;

/**
 * Singleton que centraliza los datos en memoria del sistema logístico.
 * Reemplaza la función de Inventario/TiendaSession del sistema Amazen original.
 */
public class AppContext {

    private static volatile AppContext instance;
    private static final Logger LOGGER = Logger.getLogger(AppContext.class.getName());

    private final Clock clock;
    private final List<Usuario> usuarios;
    private final List<Repartidor> repartidores;
    private final List<Envio> envios;
    private final List<TarifaBase> tarifas;

    private AppContext(Clock clock) {
        this.clock = clock != null ? clock : Clock.systemDefaultZone();
        this.usuarios = new ArrayList<>();
        this.repartidores = new ArrayList<>();
        this.envios = new ArrayList<>();
        this.tarifas = new ArrayList<>();
        LOGGER.info("AppContext inicializado correctamente.");
    }

    public static AppContext getInstance() {
        if (instance == null) {
            synchronized (AppContext.class) {
                if (instance == null) {
                    instance = new AppContext(Clock.systemDefaultZone());
                }
            }
        }
        return instance;
    }

    public Clock getClock() {
        return clock;
    }

    // === Usuarios ===
    public List<Usuario> getUsuarios() {
        return Collections.unmodifiableList(usuarios);
    }

    public boolean addUsuario(Usuario u) {
        Objects.requireNonNull(u, "Usuario no puede ser nulo");
        return usuarios.add(u);
    }

    // === Repartidores ===
    public List<Repartidor> getRepartidores() {
        return Collections.unmodifiableList(repartidores);
    }

    public boolean addRepartidor(Repartidor r) {
        Objects.requireNonNull(r, "Repartidor no puede ser nulo");
        return repartidores.add(r);
    }

    // === Envíos ===
    public List<Envio> getEnvios() {
        return Collections.unmodifiableList(envios);
    }

    public boolean addEnvio(Envio e) {
        Objects.requireNonNull(e, "Envio no puede ser nulo");
        return envios.add(e);
    }

    // === Tarifas ===
    public List<TarifaBase> getTarifas() {
        return Collections.unmodifiableList(tarifas);
    }

    public boolean addTarifa(TarifaBase t) {
        Objects.requireNonNull(t, "Tarifa no puede ser nula");
        return tarifas.add(t);
    }
}
