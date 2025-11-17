package co.edu.uniquindio.poo.amazen.Model.Persona;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.Builder; // <- importante para @Builder.Default

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
public class Persona {
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String celular;
    private String documento;
    private String contrasena;

    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Builder.Default
    private List<String> direcciones = new ArrayList<>();
}
