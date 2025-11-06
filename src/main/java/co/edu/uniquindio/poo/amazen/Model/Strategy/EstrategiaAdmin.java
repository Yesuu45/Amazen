package co.edu.uniquindio.poo.amazen.Model.Strategy;

import co.edu.uniquindio.poo.amazen.ViewController.AmazenViewController;

public class EstrategiaAdmin implements EstrategiaVista {

    @Override
    public void configurarVista(AmazenViewController controller) {
        controller.botonCatalogo.setVisible(true);
        controller.botonCarrito.setVisible(false);
        controller.botonHistorial.setVisible(true);
        controller.botonEstado.setVisible(true);

        controller.actualizarTitulo("Panel Administrador - MercadoLibre");
    }
}
