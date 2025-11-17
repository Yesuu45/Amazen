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

    // NUEVO
    private double pesoKg;       // peso del producto en kg
    private double volumenCm3;   // volumen del producto en cm³

    // Constructor antiguo (compatibilidad): si no se pasan peso/volumen, quedan en 0
    public Producto(String id, String nombre, double precio, boolean disponible) {
        this(id, nombre, precio, disponible, 0.0, 0.0);
    }

    // NUEVO constructor con peso y volumen
    public Producto(String id, String nombre, double precio, boolean disponible,
                    double pesoKg, double volumenCm3) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.disponible = disponible;
        this.pesoKg = pesoKg;
        this.volumenCm3 = volumenCm3;
    }

    @Override
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
