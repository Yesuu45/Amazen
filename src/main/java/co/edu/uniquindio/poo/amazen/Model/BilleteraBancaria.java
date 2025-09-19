package co.edu.uniquindio.poo.amazen.Model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Setter
public class BilleteraBancaria extends Billetera implements IBilleteraBancaria{
    private String numeroCuenta;
    private TipoCuenta tipoCuenta;
    private String correoElectronico;
    private String claveSeguridad;

    @Override
    public boolean registrarBancaria(BilleteraBancaria billeteraBancaria) {
        return false;
    }

    @Override
    public boolean validarBilleteraBancaria(BilleteraBancaria billeteraBancaria) {
        return false;
    }

    @Override
    public boolean eliminarBilleteraBancaria(String numeroCuenta) {
        return false;
    }

    @Override
    public boolean actualizarBilleteraBamcaria(BilleteraBancaria billeteraBancaria) {
        return false;
    }
}
