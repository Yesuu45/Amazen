package co.edu.uniquindio.poo.amazen.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Producto implements Cloneable {
    private String id;
    private String nombre;
    private double precio;
    private boolean disponible;

    // Opcionales para el cálculo de envío
    private double peso;    // en kg
    private double volumen; // en m3

    public Producto(String id, String nombre, double precio, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.disponible = disponible;
        this.peso = 0.5;     // ejemplo default
        this.volumen = 0.01; // ejemplo default
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

    @Override
    public String toString() {
        return String.format("%s - %s | Precio: %.2f | Disponible: %s", id, nombre, precio, disponible);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Producto)) return false;
        Producto other = (Producto) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
