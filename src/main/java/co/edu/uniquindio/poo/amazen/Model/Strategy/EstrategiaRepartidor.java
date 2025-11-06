package co.edu.uniquindio.poo.amazen.Model.Strategy;

import co.edu.uniquindio.poo.amazen.ViewController.AmazenViewController;

public class EstrategiaRepartidor implements EstrategiaVista{

    @Override
    public void configurarVista(AmazenViewController controller) {
        controller.botonCatalogo.setVisible(false);
        controller.botonCarrito.setVisible(false);
        controller.botonHistorial.setVisible(false);
        controller.botonEstado.setVisible(true);

        controller.actualizarTitulo("Panel Repartidor - MercadoLibre");
    }
}
