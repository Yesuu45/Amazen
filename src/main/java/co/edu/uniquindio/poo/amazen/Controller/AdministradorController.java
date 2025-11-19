package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.Disponibilidad;
import co.edu.uniquindio.poo.amazen.Model.Persona.Administrador;
import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;
import co.edu.uniquindio.poo.amazen.Model.Persona.Repartidor;
import co.edu.uniquindio.poo.amazen.Model.Persona.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Controla la gestión de personas (administradores, usuarios y repartidores)
 * y algunas operaciones básicas de autenticación.
 */
public class AdministradorController {

    /**
     * Intenta autenticar una persona por documento y contraseña.
     *
     * @param documento documento de la persona
     * @param clave     contraseña
     * @return persona encontrada o {@link Optional#empty()} si no coincide
     */
    public Optional<Persona> login(String documento, String clave) {
        return Amazen.getInstance().getListaPersonas().stream()
                .filter(p -> p.getDocumento().equals(documento) && p.getContrasena().equals(clave))
                .findFirst();
    }

    /**
     * Crea y registra un nuevo administrador.
     *
     * @param nombre      nombre
     * @param apellido    apellido
     * @param email       correo electrónico
     * @param telefono    teléfono fijo
     * @param direcciones lista de direcciones
     * @param celular     celular
     * @param documento   documento único
     * @param clave       contraseña
     * @return administrador creado
     */
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
                .direcciones(direcciones)
                .build();

        Amazen.getInstance().getListaPersonas().add(admin);
        return admin;
    }

    /**
     * Actualiza los datos de un administrador existente.
     *
     * @param documento   documento del administrador
     * @param nombre      nuevo nombre (o {@code null} para no cambiar)
     * @param apellido    nuevo apellido
     * @param email       nuevo correo
     * @param telefono    nuevo teléfono
     * @param direcciones nuevas direcciones
     * @param celular     nuevo celular
     * @param clave       nueva contraseña
     * @return {@code true} si se actualizó correctamente
     */
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

    /**
     * Actualiza los datos de una persona (sin importar su rol).
     *
     * @param documento   documento de la persona
     * @param nombre      nuevo nombre (o {@code null} para no cambiar)
     * @param apellido    nuevo apellido
     * @param email       nuevo correo
     * @param telefono    nuevo teléfono
     * @param direcciones nuevas direcciones
     * @param celular     nuevo celular
     * @param clave       nueva contraseña
     * @return {@code true} si se actualizó correctamente
     */
    public boolean actualizarPersona(String documento,
                                     String nombre, String apellido, String email, String telefono,
                                     List<String> direcciones, String celular, String clave) {

        Persona p = findByDocumentoOrThrow(documento);
        patchPersona(p, nombre, apellido, email, telefono, direcciones, celular, clave);
        return true;
    }

    /**
     * Crea y registra un nuevo usuario.
     *
     * @param nombre      nombre
     * @param apellido    apellido
     * @param email       correo
     * @param telefono    teléfono
     * @param direcciones direcciones
     * @param celular     celular
     * @param documento   documento único
     * @param clave       contraseña
     * @return usuario creado
     */
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
                .direcciones(direcciones)
                .build();

        Amazen.getInstance().getListaPersonas().add(u);
        return u;
    }

    /**
     * Crea y registra un nuevo repartidor.
     *
     * @param nombre         nombre
     * @param apellido       apellido
     * @param email          correo
     * @param telefono       teléfono
     * @param direcciones    direcciones
     * @param celular        celular
     * @param documento      documento único
     * @param clave          contraseña
     * @param zonaCobertura  zona de cobertura
     * @param disponibilidad disponibilidad inicial
     * @return repartidor creado
     */
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

    /**
     * Actualiza los datos de un repartidor existente.
     *
     * @param documento      documento del repartidor
     * @param nombre         nuevo nombre
     * @param apellido       nuevo apellido
     * @param email          nuevo correo
     * @param telefono       nuevo teléfono
     * @param direcciones    nuevas direcciones
     * @param celular        nuevo celular
     * @param clave          nueva contraseña
     * @param zonaCobertura  nueva zona de cobertura
     * @param disponibilidad nueva disponibilidad
     * @return {@code true} si se actualizó correctamente
     */
    public boolean actualizarRepartidor(String documento,
                                        String nombre, String apellido, String email, String telefono,
                                        List<String> direcciones, String celular, String clave,
                                        String zonaCobertura, Disponibilidad disponibilidad) {

        Persona p = findByDocumentoOrThrow(documento);
        if (!(p instanceof Repartidor r)) {
            throw new IllegalArgumentException("No es repartidor");
        }

        patchPersona(r, nombre, apellido, email, telefono, direcciones, celular, clave);

        if (zonaCobertura != null && !zonaCobertura.isBlank()) {
            r.setZonaCobertura(zonaCobertura);
        }

        if (disponibilidad != null) {
            r.setDisponibilidad(disponibilidad);
        }
        return true;
    }

    /**
     * Aplica cambios parciales sobre una persona (solo actualiza campos no nulos).
     */
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

        if (direcciones != null) {
            p.setDirecciones(direcciones);
        }

        if (celular != null) p.setCelular(celular);
        if (clave != null) p.setContrasena(clave);
    }

    /**
     * Busca una persona por documento o lanza excepción si no existe.
     *
     * @param documento documento buscado
     * @return persona encontrada
     * @throws IllegalArgumentException si no se encuentra
     */
    private Persona findByDocumentoOrThrow(String documento) {
        return Amazen.getInstance().getListaPersonas().stream()
                .filter(p -> p.getDocumento().equals(documento))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe persona con documento " + documento));
    }

    /**
     * Verifica si ya existe una persona con el documento dado y lanza excepción si es así.
     *
     * @param documento documento a validar
     * @throws IllegalArgumentException si el documento ya está registrado
     */
    private void throwIfDocumentoExiste(String documento) {
        boolean exists = Amazen.getInstance().getListaPersonas().stream()
                .anyMatch(p -> p.getDocumento().equals(documento));

        if (exists) {
            throw new IllegalArgumentException("El documento ya está registrado");
        }
    }

    /**
     * Valida de forma básica el formato del correo electrónico.
     *
     * @param email correo a validar
     * @throws IllegalArgumentException si es nulo, vacío o no contiene '@'
     */
    private void validarEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
    }

    /**
     * Elimina una persona por documento.
     *
     * @param documento documento de la persona a eliminar
     * @return {@code true} si se eliminó de la lista
     */
    public boolean eliminarPersona(String documento) {
        Persona p = findByDocumentoOrThrow(documento);
        return Amazen.getInstance().getListaPersonas().remove(p);
    }

}
