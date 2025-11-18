package co.edu.uniquindio.poo.amazen.Model.Estado;

import co.edu.uniquindio.poo.amazen.Model.Pedido;

public class EstadoCancelado implements EstadoPedido {

    private final Pedido pedido;

    public EstadoCancelado(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override public void pagar() { throw new IllegalStateException("❌ Pedido cancelado."); }
    @Override public void VerificacionPago() { throw new IllegalStateException("❌ Pedido cancelado."); }
    @Override public void Empaquetado() { throw new IllegalStateException("❌ Pedido cancelado."); }
    @Override public void Enviado() { throw new IllegalStateException("❌ Pedido cancelado."); }
    @Override public void Entregado() { throw new IllegalStateException("❌ Pedido cancelado."); }

    @Override
    public void cancelar() {
        System.out.println("📌 Pedido ya está cancelado.");
    }

    @Override
    public void ejecutarAccion(String accion) {
        switch (accion.toLowerCase()) {
            case "cancelar" -> cancelar();
            default -> throw new IllegalArgumentException("⚠️ Acción no válida: " + accion);
        }
    }

    @Override
    public String toString() {
        return "CANCELADO";
    }
}
