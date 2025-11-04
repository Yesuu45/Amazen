package co.edu.uniquindio.poo.amazen.Model.Estado;

import co.edu.uniquindio.poo.amazen.Model.Pedido;

public class EstadoEntregado implements EstadoPedido {
    protected Pedido pedido;

    public EstadoEntregado(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void pagar() {
        throw new IllegalStateException("❌ Pedido ya pagado y entregado.");
    }

    @Override
    public void VerificacionPago() {
        throw new IllegalStateException("❌ Pago ya verificado.");
    }

    @Override
    public void Empaquetado() {
        throw new IllegalStateException("❌ Pedido ya empaquetado.");
    }

    @Override
    public void Enviado() {
        throw new IllegalStateException("❌ Pedido ya enviado.");
    }

    @Override
    public void Entregado() {
        System.out.println("📬 Pedido ya entregado.");
    }

    @Override
    public void ejecutarAccion(String accion) {
        switch (accion.toLowerCase()) {
            case "pagar" -> pagar();
            case "verificacionpago" -> VerificacionPago();
            case "empaquetado" -> Empaquetado();
            case "enviado" -> Enviado();
            case "entregado" -> Entregado();
            default -> throw new IllegalArgumentException("⚠️ Acción no válida: " + accion);
        }
    }

    @Override
    public String toString() {
        return "ENTREGADO";
    }
}
