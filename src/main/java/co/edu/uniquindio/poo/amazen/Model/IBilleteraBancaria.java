package co.edu.uniquindio.poo.amazen.Model;

public interface IBilleteraBancaria {
    public boolean   registrarBancaria(BilleteraBancaria billeteraBancaria);
    public boolean validarBilleteraBancaria(BilleteraBancaria billeteraBancaria);
    public boolean eliminarBilleteraBancaria(String numeroCuenta);
    public boolean actualizarBilleteraBamcaria(BilleteraBancaria billeteraBancaria);
}
