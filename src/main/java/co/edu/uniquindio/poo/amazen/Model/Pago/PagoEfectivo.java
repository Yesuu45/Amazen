package co.edu.uniquindio.poo.amazen.Model.Pago;

public class PagoEfectivo implements Pago {
    @Override
    public String procesarPago(double monto) {
        return "Pago en efectivo recibido por $" + String.format("%.2f", monto) + " en espera de confirmacion ";
    }
}
