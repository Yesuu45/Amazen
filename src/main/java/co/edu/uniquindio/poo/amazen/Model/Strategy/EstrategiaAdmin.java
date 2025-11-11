package co.edu.uniquindio.poo.amazen.Model.Strategy;

import co.edu.uniquindio.poo.amazen.ViewController.AmazenViewController;

public class EstrategiaAdmin implements EstrategiaVista {

    @Override
    public void configurarVista(AmazenViewController controller) {
        controller.mostrarBotonCatalogo(true);
        controller.mostrarBotonCarrito(false);
        controller.mostrarBotonHistorial(true);
        controller.mostrarBotonEstado(true);
        controller.setBotonAdminVisible(true);
        controller.mostrarBotonMisEnvios(false);


        controller.actualizarTitulo("Panel Administrador - MercadoLibre");
    }
}
