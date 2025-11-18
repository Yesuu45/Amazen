package co.edu.uniquindio.poo.amazen.Model.Strategy;

import co.edu.uniquindio.poo.amazen.ViewController.CatalogoViewController;

public class EstrategiaCatalogoAdmin implements EstrategiaVistaCatalogo {

    @Override
    public void mostrarCatalogo(CatalogoViewController controller) {
        // Mostrar panel para agregar productos
        controller.mostrarPanelAgregarProducto(true);

        // Mostrar botones según privilegios de administrador
        controller.mostrarBotonAgregarCarrito(true); // Administrador también puede agregar productos al carrito
        controller.mostrarBotonClonar(true);          // Botón clonar visible
    }
}
