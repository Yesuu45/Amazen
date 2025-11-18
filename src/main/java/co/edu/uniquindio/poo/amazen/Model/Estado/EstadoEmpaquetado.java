package co.edu.uniquindio.poo.amazen.Model.Estado;

import co.edu.uniquindio.poo.amazen.Model.Pedido;

public class EstadoEmpaquetado implements EstadoPedido {
    protected Pedido pedido;

    public EstadoEmpaquetado(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void pagar() {
        throw new IllegalStateException("❌ Pago ya confirmado.");
    }

    @Override
    public void VerificacionPago() {
        throw new IllegalStateException("❌ Pago ya verificado.");
    }

    @Override
    public void Empaquetado() {
        System.out.println("📦 Pedido ya empaquetado.");
    }

    @Override
    public void Enviado() {
        System.out.println("🚚 Pedido en camino.");
        pedido.cambiarEstado(new EstadoEnviado(pedido));
    }

    @Override
    public void Entregado() {
        throw new IllegalStateException("❌ No se puede entregar antes de enviar.");
    }

    @Override
    public void cancelar() {
        throw new IllegalStateException("❌ No se puede cancelar un pedido que ya está empaquetado.");
    }

    @Override
    public void ejecutarAccion(String accion) {
        switch (accion.toLowerCase()) {
            case "pagar" -> pagar();
            case "verificacionpago" -> VerificacionPago();
            case "empaquetado" -> Empaquetado();
            case "enviado" -> Enviado();
            case "entregado" -> Entregado();
            case "cancelado" -> cancelar();
            default -> throw new IllegalArgumentException("⚠️ Acción no válida: " + accion);
        }
    }

    @Override
    public String toString() {
        return "EMPAQUETADO";
    }
}
