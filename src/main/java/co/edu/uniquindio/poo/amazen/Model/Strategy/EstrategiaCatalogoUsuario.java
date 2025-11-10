package co.edu.uniquindio.poo.amazen.Model.Strategy;

import co.edu.uniquindio.poo.amazen.ViewController.CatalogoViewController;

public class EstrategiaCatalogoUsuario implements EstrategiaVistaCatalogo {
    @Override
    public void mostrarCatalogo(CatalogoViewController controller) {
        controller.mostrarPanelAgregarProducto(false);   // Ocultar panel de agregar
        controller.mostrarBotonAgregarCarrito(true);     // Mostrar botón de carrito
        controller.mostrarBotonClonar(false);            // Ocultar clonación
    }
}
