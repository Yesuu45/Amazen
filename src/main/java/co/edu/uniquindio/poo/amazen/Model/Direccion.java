package co.edu.uniquindio.poo.amazen.Model;

public class Direccion {
    private double latitud;
    private double longitud;
    private String nombre;

    public Direccion(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

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
