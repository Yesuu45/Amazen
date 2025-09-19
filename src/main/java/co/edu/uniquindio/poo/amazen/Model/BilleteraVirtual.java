package co.edu.uniquindio.poo.amazen.Model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder

public class BilleteraVirtual extends Billetera implements IBilleteraVirtual{
    private String Correo;
    private String numeroTelefono;
    private Proveedor proveedor;

    @Override
    public boolean registrarVirtual(BilleteraVirtual billeteraVirtual) {
        return false;
    }

    @Override
    public boolean validarBilleteraVirtual(BilleteraVirtual billeteraVirtual) {
        return false;
    }

    @Override
    public boolean eliminarBilleteraVirtual(String numeroTelefono) {
        return false;
    }

    @Override
    public boolean actualizarBilleteraVirtual(BilleteraVirtual billeteraVirtual) {
        return false;
    }
}
