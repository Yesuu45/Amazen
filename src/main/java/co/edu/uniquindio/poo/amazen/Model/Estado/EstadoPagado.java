package co.edu.uniquindio.poo.amazen.Model.Estado;

import co.edu.uniquindio.poo.amazen.Model.Pedido;

public class EstadoPagado implements EstadoPedido {
    protected Pedido pedido;

    public EstadoPagado(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void pagar() {
        System.out.println("💳 Pago ya realizado, esperando verificación.");
    }

    @Override
    public void VerificacionPago() {
        System.out.println("✔️ Pago verificado correctamente.");
        pedido.cambiarEstado(new EstadoVerificarPago(pedido));
    }

    @Override
    public void Empaquetado() {
        throw new IllegalStateException("❌ No se puede empaquetar antes de verificar el pago.");
    }

    @Override
    public void Enviado() {
        throw new IllegalStateException("❌ No se puede enviar antes de empaquetar.");
    }

    @Override
    public void Entregado() {
        throw new IllegalStateException("❌ No se puede entregar antes de enviar.");
    }

    @Override
    public void cancelar() {
        System.out.println("⛔ Pedido cancelado antes de ser procesado.");
        pedido.cambiarEstado(new EstadoCancelado(pedido));
    }

    @Override
    public void ejecutarAccion(String accion) {
        switch (accion.toLowerCase()) {
            case "pagar" -> pagar();
            case "verificacionpago" -> VerificacionPago();
            case "empaquetado" -> Empaquetado();
            case "enviado" -> Enviado();
            case "entregado" -> Entregado();
            case "cancelado" -> cancelar();    // 🔹 NUEVO
            default -> throw new IllegalArgumentException("⚠️ Acción no válida: " + accion);
        }
    }

    @Override
    public String toString() {
        return "PAGADO";
    }
}
