package co.edu.uniquindio.poo.amazen.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

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
    public boolean isDisponible() {
        return disponible;
    }
}
