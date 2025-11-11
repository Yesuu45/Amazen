package co.edu.uniquindio.poo.amazen.Service;

import co.edu.uniquindio.poo.amazen.Model.Persona.Administrador;
import java.io.*;
import java.util.*;

public class AdminFileService {

    private static final String FILE_PATH = "administradores.txt";

    /** Guarda un administrador nuevo */
    public static void guardarAdministrador(Administrador admin) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(String.join(",",
                    admin.getId().toString(),
                    admin.getNombre(),
                    admin.getApellido(),
                    admin.getEmail(),
                    admin.getTelefono(),
                    admin.getCelular(),
                    admin.getDireccion(),
                    admin.getDocumento(),
                    admin.getContrasena()
            ));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Carga todos los administradores registrados */
    public static List<Administrador> cargarAdministradores() {
        List<Administrador> lista = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) return lista; // si no hay archivo, retorna vacío

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 9) {
                    Administrador admin = Administrador.builder()
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
                    lista.add(admin);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /** Verifica si ya existe un administrador por documento */
    public static boolean existeAdministrador(String documento) {
        return cargarAdministradores().stream()
                .anyMatch(a -> a.getDocumento().equals(documento));
    }
}
