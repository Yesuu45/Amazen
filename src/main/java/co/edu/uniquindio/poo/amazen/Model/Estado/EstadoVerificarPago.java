package co.edu.uniquindio.poo.amazen.Model.Estado;

import co.edu.uniquindio.poo.amazen.Model.Pedido;

/**
 * Estado de un {@link Pedido} cuando el pago está siendo verificado
 * o ya ha sido validado y está listo para empaquetar.
 */
public class EstadoVerificarPago implements EstadoPedido {

    /** Pedido asociado a este estado. */
    protected Pedido pedido;

    /**
     * Crea el estado de verificación de pago para el pedido dado.
     *
     * @param pedido pedido asociado
     */
    public EstadoVerificarPago(Pedido pedido) {
        this.pedido = pedido;
    }

    /** No permite iniciar un nuevo pago mientras se verifica o ya está verificado. */
    @Override
    public void pagar() {
        throw new IllegalStateException("❌ Pago ya iniciado o verificado.");
    }

    /** Indica que el pago ya fue verificado. */
    @Override
    public void VerificacionPago() {
        System.out.println("✅ Pago verificado correctamente.");
    }

    /**
     * Empaqueta el pedido y cambia el estado a EMPAQUETADO.
     */
    @Override
    public void Empaquetado() {
        System.out.println("📦 Pedido empaquetado");
        pedido.cambiarEstado(new EstadoEmpaquetado(pedido));
    }

    /** No permite enviar antes de empaquetar. */
    @Override
    public void Enviado() {
        throw new IllegalStateException("❌ No se puede enviar antes de empaquetar.");
    }

    /** No permite entregar antes de enviar. */
    @Override
    public void Entregado() {
        throw new IllegalStateException("❌ No se puede entregar antes de enviar.");
    }

    /**
     * Cancela el pedido antes de empaquetar y lo pasa a estado CANCELADO.
     */
    @Override
    public void cancelar() {
        System.out.println("❌ Pedido cancelado antes de empaquetado.");
        pedido.cambiarEstado(new EstadoCancelado(pedido));
    }

    /**
     * Ejecuta una acción válida para este estado.
     *
     * @param accion nombre de la acción a ejecutar
     */
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
        return "VERIFICAR PAGO";
    }
}
