package co.edu.uniquindio.poo.amazen.Model.Estado;

import co.edu.uniquindio.poo.amazen.Model.Pedido;

/**
 * Estado de un {@link Pedido} cuando ha sido enviado.
 * Desde aquí puede pasar a ENTREGADO.
 */
public class EstadoEnviado implements EstadoPedido {

    /** Pedido asociado a este estado. */
    protected Pedido pedido;

    /**
     * Crea el estado enviado para el pedido dado.
     *
     * @param pedido pedido asociado
     */
    public EstadoEnviado(Pedido pedido) {
        this.pedido = pedido;
    }

    /** No permite pagar un pedido que ya fue enviado. */
    @Override
    public void pagar() {
        throw new IllegalStateException("❌ Pago ya realizado.");
    }

    /** No permite verificar el pago de un pedido ya enviado. */
    @Override
    public void VerificacionPago() {
        throw new IllegalStateException("❌ Pago ya verificado.");
    }

    /** No permite empaquetar un pedido que ya fue enviado. */
    @Override
    public void Empaquetado() {
        throw new IllegalStateException("❌ Pedido ya empaquetado.");
    }

    /** Indica que el pedido se encuentra en camino. */
    @Override
    public void Enviado() {
        System.out.println("🚚 Pedido en camino.");
    }

    /**
     * Marca el pedido como entregado y cambia el estado a ENTREGADO.
     */
    @Override
    public void Entregado() {
        System.out.println("🎉 Pedido entregado al cliente.");
        pedido.cambiarEstado(new EstadoEntregado(pedido));
    }

    /** No permite cancelar un pedido que ya fue enviado. */
    @Override
    public void cancelar() {
        throw new IllegalStateException("❌ No se puede cancelar el pedido ya fue Enviado.");
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
        return "ENVIADO";
    }
}
