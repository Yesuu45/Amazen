package co.edu.uniquindio.poo.amazen.Model.Estado;

import co.edu.uniquindio.poo.amazen.Model.Pedido;

public class EstadoVerificarPago implements EstadoPedido {
    protected Pedido pedido;

    public EstadoVerificarPago(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void pagar() {
        throw new IllegalStateException("❌ Pago ya iniciado o verificado.");
    }

    @Override
    public void VerificacionPago() {
        System.out.println("✅ Pago verificado correctamente.");
    }

    @Override
    public void Empaquetado() {
        System.out.println("📦 Pedido empaquetado");
        pedido.cambiarEstado(new EstadoEmpaquetado(pedido));
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
        return "VERIFICAR PAGO";
    }

}
