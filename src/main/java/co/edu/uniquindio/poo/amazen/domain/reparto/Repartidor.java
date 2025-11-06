package co.edu.uniquindio.poo.amazen.domain.reparto;

import java.util.Objects;

public class Repartidor {
    private String id;
    private String nombre;
    private boolean disponible = true;
    private String zona; // etiqueta de zona/ciudad

    public Repartidor() { }

    public Repartidor(String id, String nombre, boolean disponible, String zona) {
        this.id = Objects.requireNonNull(id, "id");
        this.nombre = nombre;
        this.disponible = disponible;
        this.zona = zona;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = Objects.requireNonNull(id, "id"); }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    @Override public String toString() { return nombre + " (" + (disponible ? "Disponible" : "Ocupado") + ")"; }
}
