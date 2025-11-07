package co.edu.uniquindio.poo.amazen.Model.Strategy;

import co.edu.uniquindio.poo.amazen.ViewController.AmazenViewController;

public class EstrategiaRepartidor implements EstrategiaVista{

    @Override
    public void configurarVista(AmazenViewController controller) {
        controller.mostrarBotonCatalogo(false);
        controller.mostrarBotonCarrito(false);
        controller.mostrarBotonHistorial(false);
        controller.mostrarBotonEstado(true);

        controller.actualizarTitulo("Panel Repartidor - MercadoLibre");
    }
}
