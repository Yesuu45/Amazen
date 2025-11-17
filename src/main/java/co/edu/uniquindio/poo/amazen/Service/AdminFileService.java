package co.edu.uniquindio.poo.amazen.Service;

import co.edu.uniquindio.poo.amazen.Model.Persona.Administrador;
import java.io.*;
import java.util.*;

public class AdminFileService {

    private static final String FILE_PATH = "administradores.txt";

    /** SERIALIZAR ADMIN */
    private static String serializarAdmin(Administrador admin) {

        String direccionesString = String.join(";", admin.getDirecciones());

        return String.join(",",
                admin.getId().toString(),
                admin.getNombre(),
                admin.getApellido(),
                admin.getEmail(),
                admin.getTelefono(),
                admin.getCelular(),
                direccionesString,     // ← MULTIPLES DIRECCIONES
                admin.getDocumento(),
                admin.getContrasena()
        );
    }

    /** GUARDAR ADMINISTRADOR */
    public static void guardarAdministrador(Administrador admin) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(serializarAdmin(admin));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** CARGAR ADMINISTRADORES */
    public static List<Administrador> cargarAdministradores() {
        List<Administrador> lista = new ArrayList<>();
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

                    Administrador admin = Administrador.builder()
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

                    lista.add(admin);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /** VERIFICAR EXISTENCIA */
    public static boolean existeAdministrador(String documento) {
        return cargarAdministradores().stream()
                .anyMatch(a -> a.getDocumento().equalsIgnoreCase(documento));
    }
}
