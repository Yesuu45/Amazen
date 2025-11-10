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

    // ====== LOGIN (si ya lo tienes, conserva tu implementación) ======
    public Optional<Persona> login(String documento, String clave) {
        return Amazen.getInstance().getListaPersonas().stream()
                .filter(p -> p.getDocumento().equals(documento) && p.getContrasena().equals(clave))
                .findFirst();
    }

    // ====== CREAR ADMIN (usa Lombok builders) ======
    public Administrador crearAdministrador(String nombre, String apellido, String email, String telefono,
                                            String direccion, String celular, String documento, String clave) {
        throwIfDocumentoExiste(documento);
        validarEmail(email);

        Administrador a = Administrador.builder()
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .telefono(telefono)
                .direccion(direccion)
                .celular(celular)
                .documento(documento)
                .contrasena(clave)
                .build();

        Amazen.getInstance().getListaPersonas().add(a);
        return a;
    }

    // ====== ACTUALIZAR ADMIN ======
    public boolean actualizarAdministrador(String documento,
                                           String nombre, String apellido, String email, String telefono,
                                           String direccion, String celular, String clave) {
        Persona p = findByDocumentoOrThrow(documento);
        if (!(p instanceof Administrador a)) {
            throw new IllegalArgumentException("No es administrador");
        }
        patchPersona(a, nombre, apellido, email, telefono, direccion, celular, clave);
        return true;
    }

    // ====== ACTUALIZAR PERSONA (Usuario) ======
    public boolean actualizarPersona(String documento,
                                     String nombre, String apellido, String email, String telefono,
                                     String direccion, String celular, String clave) {
        Persona p = findByDocumentoOrThrow(documento);
        patchPersona(p, nombre, apellido, email, telefono, direccion, celular, clave);
        return true;
    }

    // ====== ELIMINAR ADMIN/PERSONA ======
    public boolean eliminarAdministrador(String documento) { return eliminarPersona(documento); }

    public boolean eliminarPersona(String documento) {
        List<Persona> lista = Amazen.getInstance().getListaPersonas();
        return lista.removeIf(p -> p.getDocumento().equals(documento));
    }

    // ====== CAMBIAR DISPONIBILIDAD (Repartidor) ======
    public boolean cambiarDisponibilidad(String documento, Disponibilidad nueva) {
        Persona p = findByDocumentoOrThrow(documento);
        if (!(p instanceof Repartidor r)) throw new IllegalArgumentException("La persona no es repartidor");
        r.setDisponibilidad(nueva);
        return true;
    }

    // ====== RF-010: CREAR USUARIO (builder) ======
    public Usuario crearUsuario(String nombre, String apellido, String email, String telefono,
                                String direccion, String celular, String documento, String clave) {
        throwIfDocumentoExiste(documento);
        validarEmail(email);

        Usuario u = Usuario.builder()
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .telefono(telefono)
                .direccion(direccion)
                .celular(celular)
                .documento(documento)
                .contrasena(clave)
                .build();

        Amazen.getInstance().getListaPersonas().add(u);
        return u;
    }

    // ====== RF-011: CREAR REPARTIDOR (builder + zonaCobertura) ======
    public Repartidor crearRepartidor(String nombre, String apellido, String email, String telefono,
                                      String direccion, String celular, String documento, String clave,
                                      String zonaCobertura, Disponibilidad disponibilidad) {
        throwIfDocumentoExiste(documento);
        validarEmail(email);
        if (zonaCobertura == null || zonaCobertura.isBlank()) {
            throw new IllegalArgumentException("La zona de cobertura es requerida");
        }
        if (disponibilidad == null) disponibilidad = Disponibilidad.INACTIVO;

        Repartidor r = Repartidor.builder()
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .telefono(telefono)
                .direccion(direccion)
                .celular(celular)
                .documento(documento)
                .contrasena(clave)
                .build();

        // OJO: tu clase tiene "ZonaCobertura" (con Z mayúscula) -> el setter de Lombok es setZonaCobertura
        r.setZonaCobertura(zonaCobertura);
        r.setDisponibilidad(disponibilidad);

        Amazen.getInstance().getListaPersonas().add(r);
        return r;
    }

    // ====== RF-011: ACTUALIZAR REPARTIDOR (incluye zona y disponibilidad) ======
    public boolean actualizarRepartidor(String documento,
                                        String nombre, String apellido, String email, String telefono,
                                        String direccion, String celular, String clave,
                                        String zonaCobertura, Disponibilidad disponibilidad) {
        Persona p = findByDocumentoOrThrow(documento);
        if (!(p instanceof Repartidor r)) throw new IllegalArgumentException("No es repartidor");

        patchPersona(r, nombre, apellido, email, telefono, direccion, celular, clave);

        if (zonaCobertura != null && !zonaCobertura.isBlank()) {
            r.setZonaCobertura(zonaCobertura); // coincide con tu modelo actual
        }
        if (disponibilidad != null) {
            r.setDisponibilidad(disponibilidad);
        }
        return true;
    }

    // ====== Helpers ======
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

    private void patchPersona(Persona p, String nombre, String apellido, String email, String telefono,
                              String direccion, String celular, String clave) {
        if (nombre != null)    p.setNombre(nombre);
        if (apellido != null)  p.setApellido(apellido);
        if (email != null)     { validarEmail(email); p.setEmail(email); }
        if (telefono != null)  p.setTelefono(telefono);
        if (direccion != null) p.setDireccion(direccion);
        if (celular != null)   p.setCelular(celular);
        if (clave != null)     p.setContrasena(clave);
    }
}
