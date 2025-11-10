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
 * App principal con datos DEMO “seguros”.
 * - Si algo del demo falla, lo registra en consola y la app igual arranca.
 * - Genera personas (admin/usuario/repartidores) y 8 pedidos con estados variados.
 * - Asigna automáticamente algunos pedidos y registra incidencias si existe la clase Incidencia.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carga de datos quemados (no bloquea el arranque si algo falla):
        cargarDatosDemoSeguro();

        final String fxml = "/co/edu/uniquindio/poo/amazen/login.fxml";
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml));
        Scene scene = new Scene(loader.load(), 700, 500);

        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Inserta datos de demostración (personas + pedidos).
     * Todo el bloque va en try/catch para evitar que un fallo de demo impida abrir la app.
     */
    private void cargarDatosDemoSeguro() {
        try {
            Amazen sistema = Amazen.getInstance();

            // ===== Personas DEMO =====
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

            // ===== Pedidos DEMO (estados/asinaciones/incidencias) =====
            HistorialPedido hist = HistorialPedido.getInstance();
            for (int i = 1; i <= 8; i++) {
                // Si tienes CarritoDeCompras, puedes crearlo y pasarle items; si no, deja null.
                // CarritoDeCompras carrito = new CarritoDeCompras();
                // Pedido p = new Pedido("DEMO" + i, carrito);
                Pedido p = new Pedido("DEMO" + i, /*carrito*/ null);

                // Avanzar estados en orden seguro (PAGADO -> VERIFICAR -> EMPAQUETAR -> ENVIAR -> ENTREGAR)
                int etapa = i % 5; // reparte los pedidos en distintos estados
                if (etapa >= 1) p.procesar("verificacionpago");
                if (etapa >= 2) p.procesar("empaquetado");
                if (etapa >= 3) p.procesar("enviado");
                if (etapa >= 4) p.procesar("entregado"); // también marca fechaEntrega en tu Pedido

                // Asignar repartidor a los 3 primeros para que se vea la columna Asignación
                if (i <= 3) {
                    p.asignarRepartidor("300"); // Diana Suárez (Norte)
                }

                // Registrar incidencias si existe la clase Incidencia (reflexión para no romper si no está)
                try {
                    if (i == 3 || i == 6) {
                        Class<?> k = Class.forName("co.edu.uniquindio.poo.amazen.Model.Incidencia");
                        Object inc = k.getConstructor(String.class, String.class, String.class)
                                .newInstance("Norte", "No responde", "Cliente no contesta");
                        Pedido.class.getMethod("registrarIncidencia", k).invoke(p, inc);
                    }
                } catch (Throwable ignore) {
                    // Si no existe Incidencia o su firma cambia, simplemente no se registran
                }

                hist.registrarPedido(p);
            }

            System.out.println("[DEMO] Personas: " + sistema.getListaPersonas().size()
                    + " | Pedidos creados: " + hist.obtenerPedidos().size());

        } catch (Throwable t) {
            System.err.println("[DEMO] Falló la carga de datos demo. Continuando sin demo.");
            t.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
