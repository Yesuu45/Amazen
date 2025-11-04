package co.edu.uniquindio.poo.amazen.Model.Pago;

public class PagoTarjeta implements Pago{
    private String numeroTarjeta;

    public PagoTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    @Override
    public String procesarPago(double monto) {
        return "Pago con tarjeta (" + numeroTarjeta + ") aprobado por $" + String.format("%.2f", monto) + " en espera de confirmacion ";
    }
}
