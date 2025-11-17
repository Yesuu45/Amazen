package co.edu.uniquindio.poo.amazen.Service;

import co.edu.uniquindio.poo.amazen.Model.Persona.Repartidor;
import co.edu.uniquindio.poo.amazen.Model.Disponibilidad;

import java.io.*;
import java.util.*;

public class RepartidorFileService {

    private static final String FILE_PATH = "repartidores.txt";

    /** SERIALIZAR REPARTIDOR */
    private static String serializarRepartidor(Repartidor r) {
        String direccionesString = String.join(";", r.getDirecciones());

        return String.join(",",
                r.getId().toString(),
                r.getNombre(),
                r.getApellido(),
                r.getEmail(),
                r.getTelefono(),
                r.getCelular(),
                direccionesString,       // ← MULTIPLES DIRECCIONES
                r.getDocumento(),
                r.getZonaCobertura(),
                r.getDisponibilidad().name()
        );
    }

    /** GUARDAR REPARTIDOR */
    public static void guardarRepartidor(Repartidor repartidor) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(serializarRepartidor(repartidor));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** CARGAR REPARTIDORES */
    public static List<Repartidor> cargarRepartidores() {
        List<Repartidor> lista = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");

                if (datos.length == 10) {

                    List<String> direcciones = new ArrayList<>();
                    if (!datos[6].isEmpty()) {
                        direcciones = Arrays.asList(datos[6].split(";"));
                    }

                    Repartidor r = Repartidor.builder()
                            .id(UUID.fromString(datos[0]))
                            .nombre(datos[1])
                            .apellido(datos[2])
                            .email(datos[3])
                            .telefono(datos[4])
                            .celular(datos[5])
                            .direcciones(new ArrayList<>(direcciones))
                            .documento(datos[7])
                            .zonaCobertura(datos[8])
                            .disponibilidad(Disponibilidad.valueOf(datos[9]))
                            .build();

                    lista.add(r);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /** VERIFICAR EXISTENCIA */
    public static boolean existeRepartidor(String documento) {
        return cargarRepartidores().stream()
                .anyMatch(r -> r.getDocumento().equalsIgnoreCase(documento));
    }
}
