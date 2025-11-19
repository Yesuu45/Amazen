package co.edu.uniquindio.poo.amazen.Model.Estado;

import co.edu.uniquindio.poo.amazen.Model.Pedido;

/**
 * Estado de un {@link Pedido} cuando ya ha sido empaquetado.
 * Desde aquí puede pasar a ENVIADO.
 */
public class EstadoEmpaquetado implements EstadoPedido {

    /** Pedido asociado a este estado. */
    protected Pedido pedido;

    /**
     * Crea el estado empaquetado para el pedido dado.
     *
     * @param pedido pedido asociado
     */
    public EstadoEmpaquetado(Pedido pedido) {
        this.pedido = pedido;
    }

    /** No permite volver a pagar un pedido empaquetado. */
    @Override
    public void pagar() {
        throw new IllegalStateException("❌ Pago ya confirmado.");
    }

    /** No permite volver a verificar el pago de un pedido empaquetado. */
    @Override
    public void VerificacionPago() {
        throw new IllegalStateException("❌ Pago ya verificado.");
    }

    /** Indica que el pedido ya se encuentra empaquetado. */
    @Override
    public void Empaquetado() {
        System.out.println("📦 Pedido ya empaquetado.");
    }

    /**
     * Envía el pedido y cambia el estado a ENVIADO.
     */
    @Override
    public void Enviado() {
        System.out.println("🚚 Pedido en camino.");
        pedido.cambiarEstado(new EstadoEnviado(pedido));
    }

    /** No permite entregar un pedido que aún no ha sido enviado. */
    @Override
    public void Entregado() {
        throw new IllegalStateException("❌ No se puede entregar antes de enviar.");
    }

    /** No permite cancelar un pedido que ya está empaquetado. */
    @Override
    public void cancelar() {
        throw new IllegalStateException("❌ No se puede cancelar un pedido que ya está empaquetado.");
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
        return "EMPAQUETADO";
    }
}
