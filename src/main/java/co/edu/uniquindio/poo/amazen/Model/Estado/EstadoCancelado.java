package co.edu.uniquindio.poo.amazen.Model.Estado;

import co.edu.uniquindio.poo.amazen.Model.Pedido;

/**
 * Estado de un {@link Pedido} cuando ha sido cancelado.
 * En este estado no se permiten nuevas transiciones.
 */
public class EstadoCancelado implements EstadoPedido {

    /** Pedido asociado a este estado. */
    private final Pedido pedido;

    /**
     * Crea el estado cancelado para el pedido dado.
     *
     * @param pedido pedido asociado
     */
    public EstadoCancelado(Pedido pedido) {
        this.pedido = pedido;
    }

    /** No permite pagar un pedido cancelado. */
    @Override
    public void pagar() {
        throw new IllegalStateException("❌ Pedido cancelado.");
    }

    /** No permite verificar pago de un pedido cancelado. */
    @Override
    public void VerificacionPago() {
        throw new IllegalStateException("❌ Pedido cancelado.");
    }

    /** No permite empaquetar un pedido cancelado. */
    @Override
    public void Empaquetado() {
        throw new IllegalStateException("❌ Pedido cancelado.");
    }

    /** No permite enviar un pedido cancelado. */
    @Override
    public void Enviado() {
        throw new IllegalStateException("❌ Pedido cancelado.");
    }

    /** No permite entregar un pedido cancelado. */
    @Override
    public void Entregado() {
        throw new IllegalStateException("❌ Pedido cancelado.");
    }

    /**
     * Informa que el pedido ya se encuentra cancelado.
     */
    @Override
    public void cancelar() {
        System.out.println("📌 Pedido ya está cancelado.");
    }

    /**
     * Ejecuta una acción válida para este estado.
     * Solo se permite la acción {@code cancelar}.
     *
     * @param accion nombre de la acción a ejecutar
     */
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
