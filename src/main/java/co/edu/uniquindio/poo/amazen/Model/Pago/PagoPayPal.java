package co.edu.uniquindio.poo.amazen.Model.Pago;

public class PagoPayPal  implements Pago{
    private String correo;

    public PagoPayPal(String correo) {
        this.correo = correo;
    }

    @Override
    public String procesarPago(double monto) {
        return "Pago realizado con PayPal (" + correo + ") por $" + String.format("%.2f", monto) + " en espera de confirmacion ";
    }
}
