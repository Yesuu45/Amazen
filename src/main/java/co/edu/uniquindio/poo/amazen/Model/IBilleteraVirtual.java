package co.edu.uniquindio.poo.amazen.Model;

import java.util.UUID;

public interface IBilleteraVirtual {

    public boolean   registrarVirtual(BilleteraVirtual billeteraVirtual);
    public boolean validarBilleteraVirtual(BilleteraVirtual billeteraVirtual);
    public boolean eliminarBilleteraVirtual(String numeroTelefono);
    public boolean actualizarBilleteraVirtual(BilleteraVirtual billeteraVirtual);

    }
