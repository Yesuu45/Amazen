package co.edu.uniquindio.poo.amazen.Model.Pago;

public class PasarelaAPI {
    public String realizarTransaccion(String nombreServicio, double monto) {
        return "Transacción procesada con servicio externo [" + nombreServicio + "], monto $" + String.format("%.2f", monto) + " en espera de confirmacion ";
    }
}
