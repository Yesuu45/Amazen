package co.edu.uniquindio.poo.amazen.Model;

import co.edu.uniquindio.poo.amazen.Model.Persona.*;

import java.util.ArrayList;
import java.util.List;

public class TiendaSession {

    private static TiendaSession instancia;

    // Usuario activo actual
    private Persona personaActiva;

    // Inventario de productos
    private final Inventario inventario;

    // Carrito de compras del usuario
    private final CarritoDeCompras carrito;

    // Lista de usuarios activos (opcional)
    private final List<String> usuariosActivos;

    // Constructor privado para singleton
    private TiendaSession() {
        personaActiva = null;
        inventario = Inventario.getInstance();
        carrito = new CarritoDeCompras();
        usuariosActivos = new ArrayList<>();

        // Inicializar inventario con datos de ejemplo
        inventario.inicializarData();
    }

    public static TiendaSession getInstance() {
        if (instancia == null) {
            instancia = new TiendaSession();
        }
        return instancia;
    }

    // =================== GETTERS / SETTERS ===================
    public Persona getPersonaActiva() {
        return personaActiva;
    }

    public void setPersonaActiva(Persona personaActiva) {
        this.personaActiva = personaActiva;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public CarritoDeCompras getCarrito() {
        return carrito;
    }

    public List<String> getUsuariosActivos() {
        return usuariosActivos;
    }

    public void agregarUsuarioActivo(String usuario) {
        if (!usuariosActivos.contains(usuario)) {
            usuariosActivos.add(usuario);
        }
    }

    public void removerUsuarioActivo(String usuario) {
        usuariosActivos.remove(usuario);
    }

    // =================== MÉTODOS DE TIPO DE USUARIO ===================
    public boolean esAdministrador() { return personaActiva instanceof Administrador; }

    public boolean esCliente() { return personaActiva instanceof Usuario; }

    public boolean esRepartidor() { return personaActiva instanceof Repartidor; }

    // =================== MÉTODO CERRAR SESIÓN ===================
    public void cerrarSesion() {
        personaActiva = null;
    }
}
