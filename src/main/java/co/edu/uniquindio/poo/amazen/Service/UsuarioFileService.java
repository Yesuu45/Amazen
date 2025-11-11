package co.edu.uniquindio.poo.amazen.Service;

import co.edu.uniquindio.poo.amazen.Model.Persona.Usuario;
import java.io.*;
import java.util.*;

public class UsuarioFileService {

    private static final String FILE_PATH = "usuarios.txt";

    /** Guarda un usuario nuevo */
    public static void guardarUsuario(Usuario usuario) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(String.join(",",
                    usuario.getId().toString(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getEmail(),
                    usuario.getTelefono(),
                    usuario.getCelular(),
                    usuario.getDireccion(),
                    usuario.getDocumento(),
                    usuario.getContrasena()
            ));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Carga todos los usuarios registrados */
    public static List<Usuario> cargarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) return lista; // si no hay archivo, retorna vacío

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 9) {
                    Usuario usuario = Usuario.builder()
                            .id(UUID.fromString(datos[0]))
                            .nombre(datos[1])
                            .apellido(datos[2])
                            .email(datos[3])
                            .telefono(datos[4])
                            .celular(datos[5])
                            .direccion(datos[6])
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

    /** Verifica si ya existe un usuario por documento */
    public static boolean existeUsuario(String documento) {
        return cargarUsuarios().stream()
                .anyMatch(u -> u.getDocumento().equals(documento));
    }
}
