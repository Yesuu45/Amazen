package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.Disponibilidad;
import co.edu.uniquindio.poo.amazen.Model.Persona.Administrador;
import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;
import co.edu.uniquindio.poo.amazen.Model.Persona.Repartidor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdministradorController {

    /** Lista observable de administradores (útil si una vista lista solo admins). */
    private final ObservableList<Administrador> adminsView = FXCollections.observableArrayList();

    public AdministradorController() {
        Amazen.getInstance(); // fuerza carga de datos iniciales del singleton
        syncAdmins();
    }

    /** Retorna la lista observable de administradores (si alguna vista la necesita). */
    public ObservableList<Administrador> listarAdministradores() {
        return adminsView;
    }

    /** Vuelve a sincronizar adminsView con el contenido actual del singleton. */
    private void syncAdmins() {
        adminsView.setAll(
                Amazen.getInstance()
                        .getListaPersonas()
                        .stream()
                        .filter(p -> p instanceof Administrador)
                        .map(p -> (Administrador) p)
                        .collect(Collectors.toList())
        );
    }

    // ===================== CRUD SOLO ADMIN =====================

    public Administrador crearAdministrador(
            String nombre, String apellido, String email, String telefono,
            String direccion, String celular, String documento, String contrasena
    ) {
        var store = Amazen.getInstance().getListaPersonas();

        boolean existe = store.stream().anyMatch(p -> p.getDocumento().equalsIgnoreCase(documento));
        if (existe) throw new IllegalArgumentException("Ya existe una persona con documento " + documento);

        Administrador nuevo = Administrador.builder()
                .nombre(nombre).apellido(apellido).email(email).telefono(telefono)
                .direccion(direccion).celular(celular)
                .documento(documento).contrasena(contrasena)
                .build();

        store.add(nuevo);
        adminsView.add(nuevo);
        syncAdmins(); // asegurar consistencia si hay más vistas
        return nuevo;
    }

    public boolean actualizarAdministrador(
            String documento,
            String nombre, String apellido, String email, String telefono,
            String direccion, String celular, String contrasena
    ) {
        var admin = Amazen.getInstance().getListaPersonas().stream()
                .filter(p -> p instanceof Administrador && p.getDocumento().equalsIgnoreCase(documento))
                .map(p -> (Administrador) p)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe admin con documento " + documento));

        if (nombre != null) admin.setNombre(nombre);
        if (apellido != null) admin.setApellido(apellido);
        if (email != null) admin.setEmail(email);
        if (telefono != null) admin.setTelefono(telefono);
        if (direccion != null) admin.setDireccion(direccion);
        if (celular != null) admin.setCelular(celular);
        if (contrasena != null) admin.setContrasena(contrasena);

        syncAdmins();
        return true;
    }

    public void eliminarAdministrador(String documento) {
        var store = Amazen.getInstance().getListaPersonas();
        boolean removed = store.removeIf(p -> p instanceof Administrador && p.getDocumento().equalsIgnoreCase(documento));
        if (!removed) throw new IllegalArgumentException("No existe admin con documento " + documento);
        adminsView.removeIf(a -> a.getDocumento().equalsIgnoreCase(documento));
        syncAdmins();
    }

    public Optional<Administrador> login(String documento, String contrasena) {
        String doc = documento == null ? "" : documento.trim();
        return adminsView.stream()
                .filter(a -> a.getDocumento().equalsIgnoreCase(doc)
                        && Objects.equals(a.getContrasena(), contrasena))
                .findFirst();
    }

    // ============== CRUD GENÉRICO (Usuario / Repartidor / Admin) ==============

    /** Actualiza campos comunes de cualquier Persona. */
    public boolean actualizarPersona(
            String documento,
            String nombre, String apellido, String email, String telefono,
            String direccion, String celular, String contrasena
    ) {
        var store = Amazen.getInstance().getListaPersonas();

        var persona = store.stream()
                .filter(p -> p.getDocumento().equalsIgnoreCase(documento))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe persona con documento " + documento));

        if (nombre != null) persona.setNombre(nombre);
        if (apellido != null) persona.setApellido(apellido);
        if (email != null) persona.setEmail(email);
        if (telefono != null) persona.setTelefono(telefono);
        if (direccion != null) persona.setDireccion(direccion);
        if (celular != null) persona.setCelular(celular);
        if (contrasena != null) persona.setContrasena(contrasena);

        syncAdmins(); // por si el editado es un Admin
        return true;
    }

    /** Elimina cualquier Persona por documento. */
    public void eliminarPersona(String documento) {
        var store = Amazen.getInstance().getListaPersonas();
        boolean removed = store.removeIf(p -> p.getDocumento().equalsIgnoreCase(documento));
        if (!removed) throw new IllegalArgumentException("No existe persona con documento " + documento);
        adminsView.removeIf(a -> a.getDocumento().equalsIgnoreCase(documento)); // si era admin
        syncAdmins();
    }

    // ===================== DISPONIBILIDAD REPARTIDOR =====================

    /** Cambia la disponibilidad de un repartidor. */
    public boolean cambiarDisponibilidad(String documento, Disponibilidad nueva) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("Documento requerido");
        }
        if (nueva == null) {
            throw new IllegalArgumentException("Disponibilidad no puede ser null");
        }

        var rep = Amazen.getInstance().getListaPersonas().stream()
                .filter(p -> p instanceof Repartidor && p.getDocumento().equalsIgnoreCase(documento))
                .map(p -> (Repartidor) p)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe repartidor con documento " + documento));

        rep.setDisponibilidad(nueva);
        return true;
    }

    /**
     * Alterna ACTIVO/INACTIVO.
     * Si está EN_RUTA, no cambia; si está null, lo activa.
     */
    public Disponibilidad toggleDisponibilidad(String documento) {
        var rep = Amazen.getInstance().getListaPersonas().stream()
                .filter(p -> p instanceof Repartidor && p.getDocumento().equalsIgnoreCase(documento))
                .map(p -> (Repartidor) p)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe repartidor con documento " + documento));

        var actual = rep.getDisponibilidad();
        if (actual == Disponibilidad.EN_RUTA) return actual;

        var nueva = (actual == null || actual == Disponibilidad.INACTIVO)
                ? Disponibilidad.ACTIVO
                : Disponibilidad.INACTIVO;

        rep.setDisponibilidad(nueva);
        return nueva;
    }

    /** Listado auxiliar de repartidores por disponibilidad. */
    public List<Repartidor> listarRepartidoresPorDisponibilidad(Disponibilidad d) {
        return Amazen.getInstance().getListaPersonas().stream()
                .filter(p -> p instanceof Repartidor)
                .map(p -> (Repartidor) p)
                .filter(r -> r.getDisponibilidad() == d)
                .toList();
    }

    /** Helper por si una vista necesita la lista viva de personas. */
    public List<Persona> listarPersonas() {
        return Amazen.getInstance().getListaPersonas();
    }
}
