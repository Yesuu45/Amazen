package co.edu.uniquindio.poo.amazen.Model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.LinkedList;

@Getter
@Setter
@SuperBuilder
public class Persona {
    private String nombre;
    private String correo;
    private String telefono;
    private String id;
    private int edad;
    private String genero;
    private String nacionalidad;
    private LocalDate fechaNacimiento;
    private LinkedList<Direccion> direcciones;
    private String ciudad;
    private String contrasena;



}
