package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.Disponibilidad;
import co.edu.uniquindio.poo.amazen.Model.Persona.Administrador;
import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;
import co.edu.uniquindio.poo.amazen.Model.Persona.Repartidor;
import co.edu.uniquindio.poo.amazen.Model.Persona.Usuario;

import java.util.List;
import java.util.Optional;

public class AdministradorController {

    // ========================= LOGIN =========================
    public Optional<Persona> login(String documento, String clave) {
        return Amazen.getInstance().getListaPersonas().stream()
                .filter(p -> p.getDocumento().equals(documento) && p.getContrasena().equals(clave))
                .findFirst();
    }

    // ========================= CREAR ADMIN =========================
    public Administrador crearAdministrador(String nombre, String apellido, String email, String telefono,
                                            List<String> direcciones, String celular,
                                            String documento, String clave) {

        throwIfDocumentoExiste(documento);
        validarEmail(email);

        Administrador admin = Administrador.builder()
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .telefono(telefono)
                .celular(celular)
                .documento(documento)
                .contrasena(clave)
                .direcciones(direcciones)       // <── AHORA ES LISTA
                .build();

        Amazen.getInstance().getListaPersonas().add(admin);
        return admin;
    }

    // ========================= ACTUALIZAR ADMIN =========================
    public boolean actualizarAdministrador(String documento,
                                           String nombre, String apellido, String email, String telefono,
                                           List<String> direcciones, String celular, String clave) {

        Persona p = findByDocumentoOrThrow(documento);

        if (!(p instanceof Administrador admin)) {
            throw new IllegalArgumentException("No es administrador");
        }

        patchPersona(admin, nombre, apellido, email, telefono, direcciones, celular, clave);
        return true;
    }

    // ======================= ACTUALIZAR PERSONA =========================
    public boolean actualizarPersona(String documento,
                                     String nombre, String apellido, String email, String telefono,
                                     List<String> direcciones, String celular, String clave) {

        Persona p = findByDocumentoOrThrow(documento);

        patchPersona(p, nombre, apellido, email, telefono, direcciones, celular, clave);
        return true;
    }

    // ======================= CREAR USUARIO =========================
    public Usuario crearUsuario(String nombre, String apellido, String email, String telefono,
                                List<String> direcciones, String celular,
                                String documento, String clave) {

        throwIfDocumentoExiste(documento);
        validarEmail(email);

        Usuario u = Usuario.builder()
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .telefono(telefono)
                .celular(celular)
                .documento(documento)
                .contrasena(clave)
                .direcciones(direcciones)      // <── LISTA
                .build();

        Amazen.getInstance().getListaPersonas().add(u);
        return u;
    }

    // ======================= CREAR REPARTIDOR =========================
    public Repartidor crearRepartidor(String nombre, String apellido, String email, String telefono,
                                      List<String> direcciones, String celular,
                                      String documento, String clave,
                                      String zonaCobertura, Disponibilidad disponibilidad) {

        throwIfDocumentoExiste(documento);
        validarEmail(email);

        Repartidor r = Repartidor.builder()
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .telefono(telefono)
                .celular(celular)
                .documento(documento)
                .contrasena(clave)
                .direcciones(direcciones)
                .build();

        r.setZonaCobertura(zonaCobertura);
        r.setDisponibilidad(disponibilidad);

        Amazen.getInstance().getListaPersonas().add(r);
        return r;
    }

    // ======================= ACTUALIZAR REPARTIDOR =========================
    public boolean actualizarRepartidor(String documento,
                                        String nombre, String apellido, String email, String telefono,
                                        List<String> direcciones, String celular, String clave,
                                        String zonaCobertura, Disponibilidad disponibilidad) {

        Persona p = findByDocumentoOrThrow(documento);
        if (!(p instanceof Repartidor r)) throw new IllegalArgumentException("No es repartidor");

        patchPersona(r, nombre, apellido, email, telefono, direcciones, celular, clave);

        if (zonaCobertura != null && !zonaCobertura.isBlank()) {
            r.setZonaCobertura(zonaCobertura);
        }

        if (disponibilidad != null) {
            r.setDisponibilidad(disponibilidad);
        }
        return true;
    }

    // ======================= HELPER: PATCH =========================
    private void patchPersona(Persona p,
                              String nombre, String apellido, String email, String telefono,
                              List<String> direcciones, String celular, String clave) {

        if (nombre != null) p.setNombre(nombre);
        if (apellido != null) p.setApellido(apellido);

        if (email != null) {
            validarEmail(email);
            p.setEmail(email);
        }

        if (telefono != null) p.setTelefono(telefono);

        // 🔥 AHORA ACTUALIZA TODAS LAS DIRECCIONES
        if (direcciones != null) {
            p.setDirecciones(direcciones);
        }

        if (celular != null) p.setCelular(celular);
        if (clave != null) p.setContrasena(clave);
    }

    // ========================= OTROS HELPERS =========================
    private Persona findByDocumentoOrThrow(String documento) {
        return Amazen.getInstance().getListaPersonas().stream()
                .filter(p -> p.getDocumento().equals(documento))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe persona con documento " + documento));
    }

    private void throwIfDocumentoExiste(String documento) {
        boolean exists = Amazen.getInstance().getListaPersonas().stream()
                .anyMatch(p -> p.getDocumento().equals(documento));

        if (exists) throw new IllegalArgumentException("El documento ya está registrado");
    }

    private void validarEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
    }

    // ======================= ELIMINAR PERSONA =========================
    public boolean eliminarPersona(String documento) {
        Persona p = findByDocumentoOrThrow(documento);
        return Amazen.getInstance().getListaPersonas().remove(p);
    }

}
