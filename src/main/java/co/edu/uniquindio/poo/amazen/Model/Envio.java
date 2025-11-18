package co.edu.uniquindio.poo.amazen.Model;

public class Envio {
    private Direccion origen;
    private Direccion destino;
    private double peso;   // en kg
    private double volumen; // en m3

    private static final double TARIFA_BASE = 5.0;      // base en moneda local
    private static final double TARIFA_KM = 2.0;        // por km
    private static final double TARIFA_PESO = 1.5;      // por kg
    private static final double TARIFA_VOLUMEN = 3.0;   // por m3

    public Envio(Direccion origen, Direccion destino, double peso, double volumen) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
        this.volumen = volumen;
    }

    public double calcularDistanciaKm() {
        // Haversine
        double lat1 = Math.toRadians(origen.getLatitud());
        double lon1 = Math.toRadians(origen.getLongitud());
        double lat2 = Math.toRadians(destino.getLatitud());
        double lon2 = Math.toRadians(destino.getLongitud());

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.sin(dlat/2) * Math.sin(dlat/2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dlon/2) * Math.sin(dlon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double radioTierra = 6371; // km
        return radioTierra * c;
    }

    public double calcularPrecio() {
        double distancia = calcularDistanciaKm();
        return TARIFA_BASE + (distancia * TARIFA_KM) + (peso * TARIFA_PESO) + (volumen * TARIFA_VOLUMEN);
    }

    public Direccion getOrigen() { return origen; }
    public Direccion getDestino() { return destino; }
}
