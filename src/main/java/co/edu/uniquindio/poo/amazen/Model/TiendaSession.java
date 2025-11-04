package co.edu.uniquindio.poo.amazen.Model;

public class TiendaSession {
    private static TiendaSession instancia;

    private final Inventario inventario;
    private final CarritoDeCompras carrito;

    private TiendaSession() {
        inventario = Inventario.getInstance();
        inventario.inicializarData();
        carrito = new CarritoDeCompras();
    }

    public static TiendaSession getInstance() {
        if (instancia == null) {
            instancia = new TiendaSession();
        }
        return instancia;
    }

    public Inventario getInventario() { return inventario; }
    public CarritoDeCompras getCarrito() { return carrito; }
}
