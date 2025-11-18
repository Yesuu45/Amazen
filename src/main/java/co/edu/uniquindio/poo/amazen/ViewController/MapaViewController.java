package co.edu.uniquindio.poo.amazen.ViewController;

import co.edu.uniquindio.poo.amazen.Model.Direccion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class MapaViewController {

    private CatalogoViewController callbackController;

    @FXML private WebView webViewMapa;
    @FXML private Label lblResultado;

    private double latSeleccionada;
    private double lngSeleccionada;

    private Direccion origen;
    private Direccion destino;

    public void setCallbackController(CatalogoViewController controller) {
        this.callbackController = controller;
    }

    public void setOrigen(Direccion origen) { this.origen = origen; }
    public void setDestino(Direccion destino) { this.destino = destino; }

    @FXML
    public void initialize() {
        WebEngine webEngine = webViewMapa.getEngine();
        webEngine.load(getClass().getResource("/co/edu/uniquindio/poo/amazen/mapa.html").toExternalForm());

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("app", this);

                // Inicializar origen y destino en el mapa
                if (origen != null) {
                    webEngine.executeScript("setMarkerOrigen(" + origen.getLatitud() + "," + origen.getLongitud() + ")");
                }
                if (destino != null) {
                    webEngine.executeScript("setMarkerDestino(" + destino.getLatitud() + "," + destino.getLongitud() + ")");
                }
            }
        });
    }

    @FXML
    public void confirmarDireccion() {
        if (callbackController != null) {
            callbackController.establecerDireccionUsuario(latSeleccionada, lngSeleccionada,
                    "Dirección seleccionada en mapa");
        }
        Stage stage = (Stage) lblResultado.getScene().getWindow();
        stage.close();
    }

    // Llamado desde JS
    public void setDireccion(double lat, double lng) {
        this.latSeleccionada = lat;
        this.lngSeleccionada = lng;
        lblResultado.setText("Lat: " + lat + ", Lon: " + lng);
    }
}

