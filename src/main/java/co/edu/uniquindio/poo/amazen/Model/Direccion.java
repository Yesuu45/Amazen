package co.edu.uniquindio.poo.amazen.Model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class Direccion implements Cloneable {

    private String carrera;
    private String numero;
    private String barrio;
    private String departamento;

    @Override
    public Direccion clone() {
        try {
            return (Direccion) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Error al clonar Direccion", e);
        }
    }
}

