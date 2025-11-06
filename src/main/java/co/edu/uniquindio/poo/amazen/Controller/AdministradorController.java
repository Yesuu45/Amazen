package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.Persona.Administrador;
import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Optional;
import java.util.stream.Collectors;

public class AdministradorController {

    // Lista de admins (si la usas en otra vista específica)
    private final ObservableList<Administrador> adminsView = FXCollections.observableArrayList();

    public AdministradorController() {
        Amazen.getInstance(); // fuerza carga de datos iniciales
        syncAdmins();
    }

    public ObservableList<Administrador> listarAdministradores() {
        return adminsView;
    }

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

    // ================= CRUD SOLO ADMIN =================

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

        return true;
    }

    public void eliminarAdministrador(String documento) {
        var store = Amazen.getInstance().getListaPersonas();
        boolean removed = store.removeIf(p -> p instanceof Administrador && p.getDocumento().equalsIgnoreCase(documento));
        if (!removed) throw new IllegalArgumentException("No existe admin con documento " + documento);
        adminsView.removeIf(a -> a.getDocumento().equalsIgnoreCase(documento));
    }

    public Optional<Administrador> login(String documento, String contrasena) {
        return adminsView.stream()
                .filter(a -> a.getDocumento().equalsIgnoreCase(documento) && a.getContrasena().equals(contrasena))
                .findFirst();
    }

    // ============== CRUD GENÉRICO (Usuario/Repartidor/Admin) ==============

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

        return true;
    }

    public void eliminarPersona(String documento) {
        var store = Amazen.getInstance().getListaPersonas();
        boolean removed = store.removeIf(p -> p.getDocumento().equalsIgnoreCase(documento));
        if (!removed) throw new IllegalArgumentException("No existe persona con documento " + documento);

        // Por si era admin, remover también de la lista de admins
        adminsView.removeIf(a -> a.getDocumento().equalsIgnoreCase(documento));
    }
}
