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

public class Usuario extends Persona implements Cloneable {
    private boolean estadoActive;
    private static final String FILE_PATH = "repartidores.txt";


    public boolean registrarUsuario(String nombre, String correo, String telefono, String id ,String edad , String genero ,String nacionalidad ,LocalDate fechaNacimiento ,String ciudad ,boolean estadoActive, String contrasena){
        if (usuarioExists(id)) {
            return false;
        }
        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .correo(correo)
                .telefono(telefono)
                .id(id)        // Add missing id field
                .edad(Integer.parseInt(edad))  // Convert String edad to int
                .genero(genero)
                .nacionalidad(nacionalidad)
                .fechaNacimiento(fechaNacimiento)
                .ciudad(ciudad)
                .estadoActive(estadoActive)
                .direcciones(new LinkedList<>())
                .contrasena(contrasena)
                .build();
        if (!isValidUsuario(usuario)) {
            return false;
        }
        return saveUsuario(usuario);
    }
    
    
    public boolean usuarioExists(String id){
        return this.getId().equals(id);
    }

    public boolean isValidUsuario(Usuario usuario) {
        return usuario != null
                && usuario.getId() != null
                && usuario.getNombre() != null
                && usuario.getCiudad() != null
                && usuario.getEdad() > 0
                && usuario.getDirecciones() != null
                && !usuario.getDirecciones().isEmpty()
                && usuario.getNacionalidad() != null
                && usuario.getCorreo() != null
                && usuario.getTelefono() != null
                && usuario.getGenero() != null
                && usuario.getFechaNacimiento() != null
                && usuario.getContrasena() != null;
    }
    
    public boolean saveUsuario(Usuario usuario) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.write(LocalDateTime.now() + " - " + usuario.toString() + "\n");
            return true;
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
            return false;
        }
    }
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
                    .estadoActivo(this.isEstadoActive())
                    .contrasena(this.getContrasena())
                    .build();

        } catch (Exception e) {
            throw new AssertionError("Error al clonar Repartidor", e);
        }
    }
    
    public boolean login(String id, String contrasena){
        List<Usuario> usuarios= getAllUsuarios();
        return  usuarios.stream().anyMatch(usuario -> usuario.getId().equals(id) && usuario.getContrasena().equals(contrasena));
    }

    public List<Usuario> getAllUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String usuarioData = line.substring(line.indexOf("-") + 3);
                String[] parts = usuarioData.split(",");
                if (parts.length >= 2) {
                    String id = parts[0].replace("ID:", "").trim();
                    String contrasena = parts[1].replace("Contrasena:", "").trim();
                    usuarios.add(Usuario.builder()
                            .id(id)
                            .contrasena(contrasena)
                            .build());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return usuarios;
    }
}
