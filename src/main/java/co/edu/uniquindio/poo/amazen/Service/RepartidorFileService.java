package co.edu.uniquindio.poo.amazen.Service;

import co.edu.uniquindio.poo.amazen.Model.Persona.Repartidor;
import co.edu.uniquindio.poo.amazen.Model.Disponibilidad;

import java.io.*;
import java.util.*;

public class RepartidorFileService {

    private static final String FILE_PATH = "repartidores.txt";

    /** Guarda un repartidor nuevo */
    public static void guardarRepartidor(Repartidor repartidor) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(String.join(",",
                    repartidor.getId().toString(),
                    repartidor.getNombre(),
                    repartidor.getApellido(),
                    repartidor.getEmail(),
                    repartidor.getTelefono(),
                    repartidor.getCelular(),
                    repartidor.getDireccion(),
                    repartidor.getDocumento(),
                    repartidor.getZonaCobertura(),
                    repartidor.getDisponibilidad().name()
            ));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Carga todos los repartidores registrados */
    public static List<Repartidor> cargarRepartidores() {
        List<Repartidor> lista = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 10) {
                    Repartidor repartidor = Repartidor.builder()
                            .id(UUID.fromString(datos[0]))
                            .nombre(datos[1])
                            .apellido(datos[2])
                            .email(datos[3])
                            .telefono(datos[4])
                            .celular(datos[5])
                            .direccion(datos[6])
                            .documento(datos[7])
                            .zonaCobertura(datos[8])
                            .disponibilidad(Disponibilidad.valueOf(datos[9]))
                            .build();
                    lista.add(repartidor);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /** Verifica si ya existe un repartidor por documento */
    public static boolean existeRepartidor(String documento) {
        return cargarRepartidores().stream()
                .anyMatch(r -> r.getDocumento().equals(documento));
    }
}
