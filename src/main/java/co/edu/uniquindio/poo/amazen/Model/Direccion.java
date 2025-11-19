package co.edu.uniquindio.poo.amazen.Model;

/**
 * Representa una dirección georreferenciada con latitud y longitud.
 */
public class Direccion {

    private double latitud;
    private double longitud;
    private String nombre;

    /**
     * Crea una dirección solo con coordenadas.
     *
     * @param latitud  latitud en grados
     * @param longitud longitud en grados
     */
    public Direccion(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    /**
     * Crea una dirección con coordenadas y nombre.
     *
     * @param latitud  latitud en grados
     * @param longitud longitud en grados
     * @param nombre   etiqueta o descripción
     */
    public Direccion(double latitud, double longitud, String nombre) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombre = nombre;
    }

    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return "Lat: " + latitud + ", Lon: " + longitud + (nombre != null ? " (" + nombre + ")" : "");
    }
}
