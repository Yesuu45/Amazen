package co.edu.uniquindio.poo.amazen.Model;

public class Producto implements Cloneable{
    private String id;
    private String nombre;
    private double precio;
    private boolean disponible;

    public Producto(String id, String nombre, double precio, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.disponible = disponible;
    }

    public Producto clone() {
        try {
            return (Producto) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String nuevoId) {
        this.id = nuevoId;
    }

    public String getNombre() {
        return nombre;
    }
    public double getPrecio() {
        return precio;
    }
    public boolean isDisponible() {
        return disponible;
    }
}
