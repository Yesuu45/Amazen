package co.edu.uniquindio.poo.amazen.Model;

/**
 * Representa un envío entre dos direcciones,
 * con peso, volumen y cálculo de costo.
 */
public class Envio {

    private Direccion origen;
    private Direccion destino;
    private double peso;    // en kg
    private double volumen; // en m3

    private static final double TARIFA_BASE = 5.0;
    private static final double TARIFA_KM = 2.0;
    private static final double TARIFA_PESO = 1.5;
    private static final double TARIFA_VOLUMEN = 3.0;

    /**
     * Crea un envío con origen, destino, peso y volumen.
     */
    public Envio(Direccion origen, Direccion destino, double peso, double volumen) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
        this.volumen = volumen;
    }

    /**
     * Calcula la distancia entre origen y destino usando
     * la fórmula de Haversine.
     *
     * @return distancia en kilómetros
     */
    public double calcularDistanciaKm() {
        double lat1 = Math.toRadians(origen.getLatitud());
        double lon1 = Math.toRadians(origen.getLongitud());
        double lat2 = Math.toRadians(destino.getLatitud());
        double lon2 = Math.toRadians(destino.getLongitud());

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double radioTierra = 6371; // km
        return radioTierra * c;
    }

    /**
     * Calcula el precio del envío en función de distancia, peso y volumen.
     *
     * @return valor total del envío
     */
    public double calcularPrecio() {
        double distancia = calcularDistanciaKm();
        return TARIFA_BASE + (distancia * TARIFA_KM) + (peso * TARIFA_PESO) + (volumen * TARIFA_VOLUMEN);
    }

    public Direccion getOrigen() { return origen; }
    public Direccion getDestino() { return destino; }
}
