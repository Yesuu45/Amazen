package co.edu.uniquindio.poo.amazen.Model.Persona;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Setter

public class Repartidor extends Persona {
    private String ZonaCobertura;
}
