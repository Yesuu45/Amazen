package co.edu.uniquindio.poo.amazen.Model.Strategy;

import co.edu.uniquindio.poo.amazen.ViewController.CatalogoViewController;

public class EstrategiaCatalogoAdmin implements EstrategiaVistaCatalogo {
    @Override
    public void mostrarCatalogo(CatalogoViewController controller) {
        controller.mostrarPanelAgregarProducto(true);  // Correcto
        controller.mostrarBotonAgregarCarrito(true);  // Oculta botón carrito
        controller.mostrarBotonClonar(true);           // Clonar visible
    }
}
