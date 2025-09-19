package co.edu.uniquindio.poo.amazen.Model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Builder
@Getter
@Setter
public  class Billetera implements  IBilletera {
    private float monto;
    private String nombreTitular;
    private TipoBilletera tipoBilletera;
    private LocalDate fechaRegistro;
    private boolean estado;
    private float cantidad;


    @Override
    public boolean recargarBilletera(float monto) {
        if(validarMonto(cantidad)){
            this.monto +=cantidad;
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean validarMonto(float monto) {
        return false;
    }
}
