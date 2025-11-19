package co.edu.uniquindio.poo.amazen.Model.Estado;

import co.edu.uniquindio.poo.amazen.Model.Pedido;

/**
 * Estado de un {@link Pedido} cuando ya ha sido entregado al cliente.
 * Representa el cierre normal del ciclo del pedido.
 */
public class EstadoEntregado implements EstadoPedido {

    /** Pedido asociado a este estado. */
    protected Pedido pedido;

    /**
     * Crea el estado entregado para el pedido dado.
     *
     * @param pedido pedido asociado
     */
    public EstadoEntregado(Pedido pedido) {
        this.pedido = pedido;
    }

    /** No permite pagar un pedido que ya fue entregado. */
    @Override
    public void pagar() {
        throw new IllegalStateException("❌ Pedido ya pagado y entregado.");
    }

    /** No permite verificar el pago de un pedido ya entregado. */
    @Override
    public void VerificacionPago() {
        throw new IllegalStateException("❌ Pago ya verificado.");
    }

    /** No permite empaquetar un pedido ya entregado. */
    @Override
    public void Empaquetado() {
        throw new IllegalStateException("❌ Pedido ya empaquetado.");
    }

    /** No permite reenviar un pedido ya entregado. */
    @Override
    public void Enviado() {
        throw new IllegalStateException("❌ Pedido ya enviado.");
    }

    /** Indica que el pedido ya fue entregado. */
    @Override
    public void Entregado() {
        System.out.println("📬 Pedido ya entregado.");
    }

    /**
     * Cancela el pedido después de entregado y lo pasa a estado CANCELADO.
     */
    @Override
    public void cancelar() {
        System.out.println("❌ Pedido cancelado correctamente.");
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
            default -> throw new IllegalArgumentException("⚠️ Acción no válida: " + accion);
        }
    }

    @Override
    public String toString() {
        return "ENTREGADO";
    }
}
