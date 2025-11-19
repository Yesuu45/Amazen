package co.edu.uniquindio.poo.amazen.Model.Estado;

import co.edu.uniquindio.poo.amazen.Model.Pedido;

/**
 * Estado de un {@link Pedido} cuando el pago ha sido realizado
 * pero aún está pendiente de verificación.
 */
public class EstadoPagado implements EstadoPedido {

    /** Pedido asociado a este estado. */
    protected Pedido pedido;

    /**
     * Crea el estado pagado para el pedido dado.
     *
     * @param pedido pedido asociado
     */
    public EstadoPagado(Pedido pedido) {
        this.pedido = pedido;
    }

    /** Indica que el pago ya fue realizado. */
    @Override
    public void pagar() {
        System.out.println("💳 Pago ya realizado, esperando verificación.");
    }

    /**
     * Verifica el pago y cambia el estado a VERIFICAR PAGO.
     */
    @Override
    public void VerificacionPago() {
        System.out.println("✔️ Pago verificado correctamente.");
        pedido.cambiarEstado(new EstadoVerificarPago(pedido));
    }

    /** No permite empaquetar antes de verificar el pago. */
    @Override
    public void Empaquetado() {
        throw new IllegalStateException("❌ No se puede empaquetar antes de verificar el pago.");
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
     * Cancela el pedido antes de ser procesado y lo pasa a estado CANCELADO.
     */
    @Override
    public void cancelar() {
        System.out.println("⛔ Pedido cancelado antes de ser procesado.");
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
        return "PAGADO";
    }
}
