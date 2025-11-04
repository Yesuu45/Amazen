package co.edu.uniquindio.poo.amazen.Model.Pago;

public class Pasarela implements Pago{
    private PasarelaAPI api;
    private String nombreServicio;

    public Pasarela(String nombreServicio) {
        this.api = new PasarelaAPI();
        this.nombreServicio = nombreServicio;
    }

    @Override
    public String procesarPago(double monto) {
        return api.realizarTransaccion(nombreServicio, monto);
    }
}
