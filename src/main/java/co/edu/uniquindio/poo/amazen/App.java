package co.edu.uniquindio.poo.amazen;

import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.HistorialPedido;
import co.edu.uniquindio.poo.amazen.Model.Pedido;
import co.edu.uniquindio.poo.amazen.Model.Persona.Administrador;
import co.edu.uniquindio.poo.amazen.Model.Persona.Repartidor;
import co.edu.uniquindio.poo.amazen.Model.Persona.Usuario;
import co.edu.uniquindio.poo.amazen.Model.Disponibilidad;
// Si tu proyecto tiene CarritoDeCompras, deja este import; si no, comenta la línea y la creación del carrito.
// import co.edu.uniquindio.poo.amazen.Model.CarritoDeCompras;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Versión con datos DEMO seguros.
 * - Si algo del demo falla, lo registra en consola y la app igual arranca.
 * - No usa clases opcionales (Incidencia, fechas, etc.).
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        cargarDatosDemoSeguro(); // no detiene el arranque si falla

        final String fxml = "/co/edu/uniquindio/poo/amazen/login.fxml";
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml));
        Scene scene = new Scene(loader.load(), 700, 500);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Inserta datos "quemados" mínimos (personas + pedidos simples).
     * Todo dentro de try/catch para no bloquear la app si algo no existe.
     */
    private void cargarDatosDemoSeguro() {
        try {
            Amazen sistema = Amazen.getInstance();

            // ===== Personas =====
            Administrador admin = Administrador.builder()
                    .nombre("Laura").apellido("Ríos").email("admin@demo.com")
                    .telefono("111111").direccion("Oficina Central")
                    .celular("3001111111").documento("100").contrasena("123").build();

            Usuario usuario = Usuario.builder()
                    .nombre("Carlos").apellido("Torres").email("carlos@demo.com")
                    .telefono("222222").direccion("Calle 10 #20-30")
                    .celular("3002222222").documento("200").contrasena("123").build();

            Repartidor rep1 = Repartidor.builder()
                    .nombre("Diana").apellido("Suárez").email("diana@demo.com")
                    .telefono("333333").direccion("Zona Norte")
                    .celular("3003333333").documento("300").contrasena("123").build();
            rep1.setZonaCobertura("Norte");
            rep1.setDisponibilidad(Disponibilidad.ACTIVO);

            Repartidor rep2 = Repartidor.builder()
                    .nombre("Pedro").apellido("Marín").email("pedro@demo.com")
                    .telefono("444444").direccion("Zona Sur")
                    .celular("3004444444").documento("400").contrasena("123").build();
            rep2.setZonaCobertura("Sur");
            rep2.setDisponibilidad(Disponibilidad.EN_RUTA);

            sistema.getListaPersonas().add(admin);
            sistema.getListaPersonas().add(usuario);
            sistema.getListaPersonas().add(rep1);
            sistema.getListaPersonas().add(rep2);

            // ===== Pedidos (mínimos) =====
            HistorialPedido hist = HistorialPedido.getInstance();
            for (int i = 1; i <= 8; i++) {
                // Si tienes CarritoDeCompras, úsalo; si no, pasa null (tu Pedido lo tolera).
                // CarritoDeCompras carrito = new CarritoDeCompras();
                // Pedido p = new Pedido("DEMO" + i, carrito);
                Pedido p = new Pedido("DEMO" + i, /*carrito*/ null);

                // Opcional: si estos métodos existen en tu Pedido/Estado, puedes activarlos.
                // En caso de duda, DEJA COMENTADO para que no rompa.
                // p.verificarPago();
                // p.empaquetar();
                // p.enviar();
                // if (i % 3 == 0) p.entregar();

                hist.registrarPedido(p);
            }

            System.out.println("[DEMO] Personas: " + sistema.getListaPersonas().size()
                    + " | Pedidos: " + hist.obtenerPedidos().size());

        } catch (Throwable t) {
            System.err.println("[DEMO] Falló la carga de datos demo. Continuando sin demo.");
            t.printStackTrace();
        }
    }

    public static void main(String[] args) { launch(args); }
}
