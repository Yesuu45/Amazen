package co.edu.uniquindio.poo.amazen.Service;

import co.edu.uniquindio.poo.amazen.Model.Persona.Usuario;

import java.io.*;
import java.util.*;

public class UsuarioFileService {

    private static final String FILE_PATH = "usuarios.txt";

    /** SERIALIZAR USUARIO → linea texto */
    private static String serializarUsuario(Usuario usuario) {

        String direccionesString = String.join(";", usuario.getDirecciones());

        return String.join(",",
                usuario.getId().toString(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getCelular(),
                direccionesString,     // AHORA MULTIPLES
                usuario.getDocumento(),
                usuario.getContrasena()
        );
    }

    /** GUARDAR UN USUARIO NUEVO */
    public static void guardarUsuario(Usuario usuario) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(serializarUsuario(usuario));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** SOBRESCRIBIR LISTA COMPLETA */
    private static void guardarUsuariosTodos(List<Usuario> lista) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Usuario u : lista) {
                bw.write(serializarUsuario(u));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** CARGAR USUARIOS */
    public static List<Usuario> cargarUsuarios() {

        List<Usuario> lista = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");

                if (datos.length == 9) {

                    List<String> direcciones = new ArrayList<>();
                    if (!datos[6].isEmpty()) {
                        direcciones = Arrays.asList(datos[6].split(";"));
                    }

                    Usuario usuario = Usuario.builder()
                            .id(UUID.fromString(datos[0]))
                            .nombre(datos[1])
                            .apellido(datos[2])
                            .email(datos[3])
                            .telefono(datos[4])
                            .celular(datos[5])
                            .direcciones(new ArrayList<>(direcciones))
                            .documento(datos[7])
                            .contrasena(datos[8])
                            .build();

                    lista.add(usuario);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lista;
    }


    // -----------------
    // MÉTODOS CRUD
    // -----------------

    /** Comprueba si existe un usuario por documento (ignorando mayúsculas) */
    public static boolean existeUsuario(String documento) {
        if (documento == null) return false;
        return cargarUsuarios().stream()
                .anyMatch(u -> u.getDocumento() != null && u.getDocumento().equalsIgnoreCase(documento));
    }

    /** ACTUALIZAR INFORMACIÓN PERSONAL */
    public static boolean actualizarInformacionUsuario(
            String documento,
            String nombre,
            String apellido,
            String email,
            String telefono,
            String celular
    ) {
        List<Usuario> usuarios = cargarUsuarios();

        for (Usuario u : usuarios) {
            if (u.getDocumento().equals(documento)) {

                u.setNombre(nombre);
                u.setApellido(apellido);
                u.setEmail(email);
                u.setTelefono(telefono);
                u.setCelular(celular);

                guardarUsuariosTodos(usuarios);
                return true;
            }
        }

        return false;
    }


    /** AGREGAR DIRECCIÓN */
    public static boolean agregarDireccion(String documento, String nuevaDireccion) {

        List<Usuario> usuarios = cargarUsuarios();

        for (Usuario u : usuarios) {
            if (u.getDocumento().equals(documento)) {

                u.getDirecciones().add(nuevaDireccion);

                guardarUsuariosTodos(usuarios);
                return true;
            }
        }
        return false;
    }


    /** ELIMINAR DIRECCIÓN */
    public static boolean eliminarDireccion(String documento, String direccion) {

        List<Usuario> usuarios = cargarUsuarios();

        for (Usuario u : usuarios) {
            if (u.getDocumento().equals(documento)) {

                u.getDirecciones().remove(direccion);

                guardarUsuariosTodos(usuarios);
                return true;
            }
        }
        return false;
    }


    /** OBTENER LISTA DE DIRECCIONES */
    public static List<String> obtenerDirecciones(String documento) {

        return cargarUsuarios()
                .stream()
                .filter(u -> u.getDocumento().equals(documento))
                .findFirst()
                .map(Usuario::getDirecciones)
                .orElse(new ArrayList<>());
    }
}
