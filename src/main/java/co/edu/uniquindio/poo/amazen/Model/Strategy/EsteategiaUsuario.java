package co.edu.uniquindio.poo.amazen.Model.Strategy;

import co.edu.uniquindio.poo.amazen.ViewController.AmazenViewController;

public class EsteategiaUsuario implements EstrategiaVista{
    @Override
    public void configurarVista(AmazenViewController controller) {
        controller.botonCatalogo.setVisible(true);
        controller.botonCarrito.setVisible(true);
        controller.botonHistorial.setVisible(true);
        controller.botonEstado.setVisible(false);

        controller.actualizarTitulo("Bienvenido Usuario - MercadoLibre");
    }
}
