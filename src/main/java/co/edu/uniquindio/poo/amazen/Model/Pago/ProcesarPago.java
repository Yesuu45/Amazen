package co.edu.uniquindio.poo.amazen.Model.Pago;

public class ProcesarPago {
    private Pago metodoPago;

    public void setMetodoPago(Pago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String ejecutarPago(double monto) {
        if (metodoPago == null) return "No se seleccionó un método de pago.";
        return metodoPago.procesarPago(monto);
    }
}
