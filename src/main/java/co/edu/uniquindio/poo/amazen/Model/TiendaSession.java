package co.edu.uniquindio.poo.amazen.Model;

import co.edu.uniquindio.poo.amazen.Model.Persona.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja la sesión actual de la tienda:
 * usuario activo, inventario, carrito y usuarios conectados.
 */
public class TiendaSession {

    private static TiendaSession instancia;

    // Usuario activo actual
    private Persona personaActiva;

    // Inventario de productos
    private final Inventario inventario;

    // Carrito de compras del usuario
    private final CarritoDeCompras carrito;

    // Lista de usuarios activos (identificados por nombre o id)
    private final List<String> usuariosActivos;

    /**
     * Constructor privado para el patrón Singleton.
     */
    private TiendaSession() {
        personaActiva = null;
        inventario = Inventario.getInstance();
        carrito = new CarritoDeCompras();
        usuariosActivos = new ArrayList<>();

        inventario.inicializarData();
    }

    /**
     * Obtiene la instancia única de la sesión de tienda.
     */
    public static TiendaSession getInstance() {
        if (instancia == null) {
            instancia = new TiendaSession();
        }
        return instancia;
    }

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

    /**
     * Registra un usuario como activo en la tienda.
     */
    public void agregarUsuarioActivo(String usuario) {
        if (!usuariosActivos.contains(usuario)) {
            usuariosActivos.add(usuario);
        }
    }

    /**
     * Elimina un usuario de la lista de activos.
     */
    public void removerUsuarioActivo(String usuario) {
        usuariosActivos.remove(usuario);
    }

    /**
     * Indica si el usuario activo es administrador.
     */
    public boolean esAdministrador() { return personaActiva instanceof Administrador; }

    /**
     * Indica si el usuario activo es cliente.
     */
    public boolean esCliente() { return personaActiva instanceof Usuario; }

    /**
     * Indica si el usuario activo es repartidor.
     */
    public boolean esRepartidor() { return personaActiva instanceof Repartidor; }

    /**
     * Cierra la sesión actual.
     */
    public void cerrarSesion() {
        personaActiva = null;
    }
}
