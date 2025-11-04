package co.edu.uniquindio.poo.amazen.Model.Persona;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
public class Persona {
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    private String celular;
    private String documento;
    private String contrasena;
    private UUID id;
}
