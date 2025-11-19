package co.edu.uniquindio.poo.amazen.Model.Persona;

import co.edu.uniquindio.poo.amazen.Model.Disponibilidad;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class  Repartidor extends Persona {

    private String zonaCobertura;
    private Disponibilidad disponibilidad;
}
