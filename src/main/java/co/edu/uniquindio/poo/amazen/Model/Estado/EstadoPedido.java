package co.edu.uniquindio.poo.amazen.Model.Estado;

public interface EstadoPedido {
    void pagar();
    void VerificacionPago();
    void Empaquetado();
    void Enviado();
    void Entregado();
    void ejecutarAccion(String accion);
}
