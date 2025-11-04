package co.edu.uniquindio.poo.amazen.Model.Estado;

import co.edu.uniquindio.poo.amazen.Model.Pedido;

public class EstadoEnviado implements EstadoPedido {
    protected Pedido pedido;

    public EstadoEnviado(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void pagar() {
        throw new IllegalStateException("❌ Pago ya realizado.");
    }

    @Override
    public void VerificacionPago() {
        throw new IllegalStateException("❌ Pago ya verificado.");
    }

    @Override
    public void Empaquetado() {
        throw new IllegalStateException("❌ Pedido ya empaquetado.");
    }

    @Override
    public void Enviado() {
        System.out.println("🚚 Pedido en camino.");
    }

    @Override
    public void Entregado() {
        System.out.println("🎉 Pedido entregado al cliente.");
        pedido.cambiarEstado(new EstadoEntregado(pedido)); // 🔹 Cambia al siguiente estado
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
        return "ENVIADO";
    }
}
