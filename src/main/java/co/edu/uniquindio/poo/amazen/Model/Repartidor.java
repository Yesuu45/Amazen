package co.edu.uniquindio.poo.amazen.Model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
public class Repartidor extends Persona implements Cloneable {

    private String antiguedad;
    private String tipoSangre;
    private String eps;
    private String cuentaBancaria;
    private String numeroCuenta;
    private boolean estadoActivo;

    @Override
    public Repartidor clone() {
        try {
            // Clonar lista de direcciones
            LinkedList<Direccion> direccionesClonadas = new LinkedList<>();
            if (this.getDirecciones() != null) {
                for (Direccion direccion : this.getDirecciones()) {
                    direccionesClonadas.add(direccion.clone());
                }
            }

            return Repartidor.builder()
                    .nombre(this.getNombre())
                    .correo(this.getCorreo())
                    .telefono(this.getTelefono())
                    .id(this.getId())
                    .edad(this.getEdad())
                    .genero(this.getGenero())
                    .nacionalidad(this.getNacionalidad())
                    .fechaNacimiento(this.getFechaNacimiento())
                    .ciudad(this.getCiudad())
                    .direcciones(direccionesClonadas)

                    // Campos propios de Repartidor
                    .antiguedad(this.getAntiguedad())
                    .tipoSangre(this.getTipoSangre())
                    .eps(this.getEps())
                    .cuentaBancaria(this.getCuentaBancaria())
                    .numeroCuenta(this.getNumeroCuenta())
                    .estadoActivo(this.isEstadoActivo())
                    .contrasena(this.getContrasena())

                    .build();

        } catch (Exception e) {
            throw new AssertionError("Error al clonar Repartidor", e);
        }
    }

    public boolean registrarRepartidor(String nombre, String correo, String telefono
            , String id
            , String edad
            , String genero
            , String nacionalidad
            , String fechaNacimiento
            , String ciudad
            , LinkedList<Direccion> direcciones
            , String antiguedad
            , String tipoSangre
            , String eps
            , String cuentaBancaria
            , String numeroCuenta
            , boolean estadoActivo
            ,String contrasena) {

        if (repartidorExists(id)) {
            return false;
        }

        Repartidor repartidor = Repartidor.builder()
                .nombre(nombre)
                .correo(correo)
                .telefono(telefono)
                .id(id)
                .edad(Integer.parseInt(edad))
                .genero(genero)
                .nacionalidad(nacionalidad)
                .fechaNacimiento(LocalDate.parse(fechaNacimiento))
                .ciudad(ciudad)
                .direcciones(new LinkedList<>())
                .antiguedad(antiguedad)
                .tipoSangre(tipoSangre)
                .eps(eps)
                .cuentaBancaria(cuentaBancaria)
                .numeroCuenta(numeroCuenta)
                .estadoActivo(estadoActivo)
                .contrasena(contrasena)
            .build();
    
        if (!isValidRerpartidor(repartidor)) {
            return false;
        }
        
        return saveRepartidor(repartidor);
    }


    public boolean repartidorExists(String id) {
        // Since getId() returns a String, not a collection
        return this.getId().equals(id);
    }
    
    public boolean isValidRerpartidor(Repartidor repartidor){
        return repartidor.getId() != null
                && repartidor.getNombre() != null
                && repartidor.getEps() != null
                && repartidor.getCuentaBancaria() != null
                && repartidor.getNumeroCuenta() != null
                && repartidor.getTelefono() != null
                && repartidor.getCorreo() != null
                && repartidor.getGenero() != null
                && repartidor.getNacionalidad() != null
                && repartidor.getFechaNacimiento() != null
                && repartidor.getCiudad() != null
                && repartidor.getDirecciones() != null
                && !repartidor.getDirecciones().isEmpty()
                && repartidor.getAntiguedad() != null
                && repartidor.getTipoSangre() != null
                && repartidor.getContrasena() != null;
    }

    private static final String FILE_PATH = "repartidores.txt"; 

    private boolean saveRepartidor(Repartidor repartidor) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.write(LocalDateTime.now() + " - " + repartidor.toString() + "\n");
            return true;
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
            return false;
        }
    }



    public boolean login(String id, String contrasena){
        List<Repartidor> repartidores= getAllRepartidores();
        return  repartidores.stream().anyMatch(repartidor -> repartidor.getId().equals(id) && repartidor.getContrasena().equals(contrasena));
    }

    public List<Repartidor> getAllRepartidores() {
        List<Repartidor> repartidores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String repartidorData = line.substring(line.indexOf("-") + 3);
                String[] parts = repartidorData.split(",");
                if (parts.length >= 2) {
                    String id = parts[0].replace("ID:", "").trim();
                    String contrasena = parts[1].replace("Contrasena:", "").trim();
                    repartidores.add(Repartidor.builder()
                            .id(id)
                            .contrasena(contrasena)
                            .build());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return repartidores;
    }
}
