package co.edu.uniquindio.poo.amazen.Model.Estado;

import co.edu.uniquindio.poo.amazen.Model.Pedido;

public abstract class BaseEstado implements EstadoPedido {
    protected final Pedido ctx;

    protected BaseEstado(Pedido ctx) { this.ctx = ctx; }

    protected void noPermitida(String nombre) {
        throw new IllegalStateException("Transición '" + nombre + "' no permitida desde " + this.getClass().getSimpleName());
    }

    @Override public void pagar()             { noPermitida("pagar"); }
    @Override public void VerificacionPago()  { noPermitida("verificar"); }
    @Override public void Empaquetado()       { noPermitida("empaquetar"); }
    @Override public void Enviado()           { noPermitida("enviar"); }
    @Override public void Entregado()         { noPermitida("entregar"); }
}
