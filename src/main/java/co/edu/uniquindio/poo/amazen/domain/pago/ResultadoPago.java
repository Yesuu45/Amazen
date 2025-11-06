package co.edu.uniquindio.poo.amazen.domain.pago;

/**
 * Resultado simple del proceso de pago.
 */
public class ResultadoPago {
    private boolean aprobado;
    private String referencia;
    private String mensaje;

    public ResultadoPago(boolean aprobado, String referencia, String mensaje) {
        this.aprobado = aprobado;
        this.referencia = referencia;
        this.mensaje = mensaje;
    }

    public boolean isAprobado() { return aprobado; }
    public String getReferencia() { return referencia; }
    public String getMensaje() { return mensaje; }

    @Override
    public String toString() {
        return (aprobado ? "✅ " : "❌ ") + mensaje + " (" + referencia + ")";
    }
}
