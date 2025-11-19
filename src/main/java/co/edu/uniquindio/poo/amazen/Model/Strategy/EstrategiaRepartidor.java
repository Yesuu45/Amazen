package co.edu.uniquindio.poo.amazen.Model.Strategy;

import co.edu.uniquindio.poo.amazen.ViewController.AmazenViewController;

public class EstrategiaRepartidor implements EstrategiaVista {

    @Override
    public void configurarVista(AmazenViewController controller) {

        // El repartidor NO usa catálogo, carrito ni historial de compras
        controller.mostrarBotonCatalogo(false);
        controller.mostrarBotonCarrito(false);
        controller.mostrarBotonHistorial(false);

        // Sí ve el estado general de envíos (si tienes esa vista) y su panel de envíos
        controller.mostrarBotonEstado(true);
        controller.mostrarBotonMisEnvios(true);

        // Nunca ve cosas de admin
        controller.setBotonAdminVisible(false);

        // (Opcional) si quieres que pueda editar su perfil:
        controller.mostrarBotonGestionPerfil(true);

        controller.actualizarTitulo("Panel Repartidor - Amazen");
    }
}
