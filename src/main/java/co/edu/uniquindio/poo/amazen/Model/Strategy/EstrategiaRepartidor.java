package co.edu.uniquindio.poo.amazen.Model.Strategy;

import co.edu.uniquindio.poo.amazen.ViewController.AmazenViewController;

public class EstrategiaRepartidor implements EstrategiaVista{

    @Override
    public void configurarVista(AmazenViewController controller) {
        controller.mostrarBotonCatalogo(false);
        controller.mostrarBotonCarrito(false);
        controller.mostrarBotonHistorial(false);
        controller.mostrarBotonEstado(true);
        controller.botonMisEnvios.setVisible(true);
        controller.botonMisEnvios.setDisable(false);


        controller.botonCatalogo.setVisible(false);
        controller.botonCarrito.setVisible(false);
        controller.botonHistorial.setVisible(false);
        controller.botonEstado.setVisible(false);
        controller.setBotonAdminVisible(false);

        controller.actualizarTitulo("Panel Repartidor - Amazen");



    }
}
