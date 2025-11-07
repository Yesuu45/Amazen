package co.edu.uniquindio.poo.amazen.Model.Strategy;

import co.edu.uniquindio.poo.amazen.ViewController.AmazenViewController;

public class EsteategiaUsuario implements EstrategiaVista {

    @Override
    public void configurarVista(AmazenViewController controller) {
        controller.mostrarBotonCatalogo(true);
        controller.mostrarBotonCarrito(true);
        controller.mostrarBotonHistorial(true);
        controller.mostrarBotonEstado(false);
        controller.setBotonAdminVisible(false);

        controller.actualizarTitulo("Bienvenido Usuario - MercadoLibre");
    }
}
