package co.edu.uniquindio.poo.amazen.Model;

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
public class Administrador extends Persona implements Cloneable {
    private String antiguedad;
    private String tipoSangre;
    private String eps;
    private String cuentaBancaria;
    private String numeroCuenta;
    private String numeroEmpleadosACargo;


    private static final String FILE_PATH = "administradores.txt";

    public boolean administradorExists(String id){
        return this.getId().equals(id);
    }
    
    public boolean registrarAdministrador(String nombre, String correo, String telefono, String id, String edad, String genero, String nacionalidad, LocalDate fechaNacimiento, String ciudad, String antiguedad, String tipoSangre, String eps, String cuentaBancaria, String numeroCuenta, String numeroEmpleadosACargo, String Contrasena) {
        if (administradorExists(id)) {
            return false;
        }
        Administrador administrador = Administrador.builder()
                .nombre(nombre)
                .correo(correo)
                .telefono(telefono)
                .id(id)
                .edad(Integer.parseInt(edad))
                .genero(genero)
                .nacionalidad(nacionalidad)
                .fechaNacimiento(fechaNacimiento)
                .ciudad(ciudad)
                .antiguedad(antiguedad)
                .tipoSangre(tipoSangre)
                .eps(eps)
                .cuentaBancaria(cuentaBancaria)
                .numeroCuenta(numeroCuenta)
                .numeroEmpleadosACargo(numeroEmpleadosACargo)
                .contrasena(Contrasena)
                .build();
        if (!isValidAdministrador(administrador)) {
            return false;
        }
        return saveAdimistrador(administrador);
    }

    private boolean saveAdimistrador(Administrador administrador) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.write(LocalDateTime.now() + " - " + administrador.toString() + "\n");
            return true;
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
            return false;
        }
    }

    public boolean isValidAdministrador(Administrador administrador){
        return administrador.getId() != null
                && administrador.getNombre() != null
                && administrador.getEps() != null
                && administrador.getCuentaBancaria() != null
                && administrador.getNumeroCuenta() != null
                && administrador.getTelefono() != null
                && administrador.getCorreo() != null
                && administrador.getGenero() != null
                && administrador.getNacionalidad() != null
                && administrador.getFechaNacimiento() != null
                && administrador.getCiudad() != null
                && administrador.getDirecciones() != null
                && !administrador.getDirecciones().isEmpty()
                && administrador.getAntiguedad() != null
                && administrador.getTipoSangre() != null
                && administrador.getNumeroEmpleadosACargo() != null
                && administrador.getContrasena() != null;

    }

    @Override
    public Administrador clone() {
        try {
            // Clonar lista de direcciones
            LinkedList<Direccion> direccionesClonadas = new LinkedList<>();
            if (this.getDirecciones() != null) {
                for (Direccion direccion : this.getDirecciones()) {
                    direccionesClonadas.add(direccion.clone());
                }
            }

            return Administrador.builder()
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

                    // Campos propios de Administrador
                    .antiguedad(this.getAntiguedad())
                    .tipoSangre(this.getTipoSangre())
                    .eps(this.getEps())
                    .cuentaBancaria(this.getCuentaBancaria())
                    .numeroCuenta(this.getNumeroCuenta())
                    .numeroEmpleadosACargo(this.getNumeroEmpleadosACargo())
                    .contrasena(this.getContrasena())
                    .build();

        } catch (Exception e) {
            throw new AssertionError("Error al clonar Administrador", e);
        }
    }


    public boolean login(String id, String contrasena){
        List<Administrador> administradores= getAllAdministradores();
        return  administradores.stream().anyMatch(administrador -> administrador.getId().equals(id) && administrador.getContrasena().equals(contrasena));
    }

    public List<Administrador> getAllAdministradores() {
        List<Administrador> administradores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String administradorData = line.substring(line.indexOf("-") + 3);
                String[] parts = administradorData.split(",");
                if (parts.length >= 2) {
                    String id = parts[0].replace("ID:", "").trim();
                    String contrasena = parts[1].replace("Contrasena:", "").trim();
                    administradores.add(Administrador.builder()
                            .id(id)
                            .contrasena(contrasena)
                            .build());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return administradores;
    }
    
}