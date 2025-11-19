package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.Model.Direccion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

/**
 * Controlador de la ventana de mapa interactivo.
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 *     <li>Cargar el archivo {@code mapa.html} en un {@link WebView}.</li>
 *     <li>Exponer este controlador a JavaScript como objeto global {@code app}.</li>
 *     <li>Recibir desde JavaScript la latitud/longitud seleccionadas por el usuario.</li>
 *     <li>Devolver la dirección seleccionada al controlador que abrió el mapa
 *         (usualmente {@link CatalogoViewController}) mediante un callback.</li>
 *     <li>Opcionalmente inicializar marcadores de origen/destino si se envían objetos {@link Direccion}.</li>
 * </ul>
 *
 * <p>Este controlador actúa como puente entre JavaFX y el mapa en HTML/JavaScript:
 * la vista web llama a {@link #setDireccion(double, double)} desde JS, y la aplicación
 * JavaFX llama a {@link #confirmarDireccion()} para cerrar la ventana y devolver
 * el resultado al catálogo.</p>
 */
public class MapaViewController {

    /**
     * Controlador que abrió el mapa y que recibirá la dirección seleccionada
     * (se inyecta manualmente con {@link #setCallbackController(CatalogoViewController)}).
     */
    private CatalogoViewController callbackController;

    /** Componente WebView que muestra el mapa HTML/JavaScript. */
    @FXML private WebView webViewMapa;

    /** Label para mostrar la latitud/longitud seleccionadas como feedback al usuario. */
    @FXML private Label lblResultado;

    /** Última latitud seleccionada en el mapa (actualizada desde JS). */
    private double latSeleccionada;

    /** Última longitud seleccionada en el mapa (actualizada desde JS). */
    private double lngSeleccionada;

    /** Dirección de origen opcional para mostrar un marcador inicial en el mapa. */
    private Direccion origen;

    /** Dirección de destino opcional para mostrar un marcador inicial en el mapa. */
    private Direccion destino;

    // ======================================================================
    // INYECCIÓN DE DEPENDENCIAS / CONTEXTO
    // ======================================================================

    /**
     * Asigna el controlador de catálogo que recibirá la dirección seleccionada.
     *
     * <p>Este método debe ser llamado desde el controlador que abre el mapa
     * (por ejemplo, {@link CatalogoViewController#abrirMapa()}).</p>
     *
     * @param controller instancia de {@link CatalogoViewController} que actuará como callback.
     */
    public void setCallbackController(CatalogoViewController controller) {
        this.callbackController = controller;
    }

    /**
     * Establece una dirección de origen para que el mapa pueda mostrar un marcador inicial.
     *
     * @param origen dirección de origen, puede ser {@code null} si no se requiere.
     */
    public void setOrigen(Direccion origen) {
        this.origen = origen;
    }

    /**
     * Establece una dirección de destino para que el mapa pueda mostrar un marcador inicial.
     *
     * @param destino dirección de destino, puede ser {@code null} si no se requiere.
     */
    public void setDestino(Direccion destino) {
        this.destino = destino;
    }

    // ======================================================================
    // CICLO DE VIDA DE LA VISTA
    // ======================================================================

    /**
     * Inicializa el WebView, carga el archivo HTML del mapa y configura el puente
     * de comunicación con JavaScript.
     *
     * <p>Pasos principales:</p>
     * <ol>
     *     <li>Carga {@code mapa.html} desde los recursos del classpath.</li>
     *     <li>Una vez cargado, expone este controlador como {@code window.app} en JS.</li>
     *     <li>Si existen valores para {@link #origen} o {@link #destino}, invoca las funciones
     *         JavaScript {@code setMarkerOrigen(lat, lng)} y {@code setMarkerDestino(lat, lng)}
     *         para dibujar marcadores iniciales.</li>
     * </ol>
     */
    @FXML
    public void initialize() {
        WebEngine webEngine = webViewMapa.getEngine();

        // Cargar el archivo HTML del mapa desde los recursos
        webEngine.load(getClass()
                .getResource("/co/edu/uniquindio/poo/amazen/mapa.html")
                .toExternalForm());

        // Cuando la página termina de cargar, se configura el "bridge" Java ↔ JS
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                // Exponer este controlador como 'window.app' en JavaScript
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("app", this);

                // Inicializar marcador de origen si está definido
                if (origen != null) {
                    webEngine.executeScript(
                            "setMarkerOrigen(" + origen.getLatitud() + "," + origen.getLongitud() + ")"
                    );
                }
                // Inicializar marcador de destino si está definido
                if (destino != null) {
                    webEngine.executeScript(
                            "setMarkerDestino(" + destino.getLatitud() + "," + destino.getLongitud() + ")"
                    );
                }
            }
        });
    }

    // ======================================================================
    // ACCIONES DE LA VISTA (BOTONES)
    // ======================================================================

    /**
     * Acción del botón "Confirmar dirección".
     *
     * <p>Envía la latitud/longitud seleccionadas al controlador de catálogo mediante
     * {@link CatalogoViewController#establecerDireccionUsuario(double, double, String)}
     * y luego cierra la ventana actual.</p>
     *
     * <p>En caso de que no se haya seleccionado nada, se envía igualmente la última
     * coordenada conocida (por defecto será 0,0 si nunca se tocó el mapa).</p>
     */
    @FXML
    public void confirmarDireccion() {
        if (callbackController != null) {
            callbackController.establecerDireccionUsuario(
                    latSeleccionada,
                    lngSeleccionada,
                    "Dirección seleccionada en mapa"
            );
        }
        // Cerrar la ventana del mapa
        Stage stage = (Stage) lblResultado.getScene().getWindow();
        stage.close();
    }

    // ======================================================================
    // MÉTODO INVOCADO DESDE JAVASCRIPT
    // ======================================================================

    /**
     * Método llamado desde JavaScript cuando el usuario selecciona una posición
     * en el mapa.
     *
     * <p>En el archivo {@code mapa.html}, típicamente se invocará algo como:</p>
     *
     * <pre>
     *   app.setDireccion(latitud, longitud);
     * </pre>
     *
     * <p>Este método actualiza los campos internos {@link #latSeleccionada} y
     * {@link #lngSeleccionada}, y muestra un texto con las coordenadas en
     * {@link #lblResultado} para feedback visual.</p>
     *
     * @param lat latitud seleccionada.
     * @param lng longitud seleccionada.
     */
    public void setDireccion(double lat, double lng) {
        this.latSeleccionada = lat;
        this.lngSeleccionada = lng;
        lblResultado.setText("Lat: " + lat + ", Lon: " + lng);
    }
}
