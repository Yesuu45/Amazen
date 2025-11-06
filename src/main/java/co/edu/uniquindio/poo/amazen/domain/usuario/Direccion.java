package co.edu.uniquindio.poo.amazen.domain.usuario;

import java.util.Objects;

public class Direccion {
    private String id;
    private String alias;     // "Casa", "Trabajo"
    private String ciudad;
    private String barrio;
    private String calle;
    private String referencia; // indicaciones

    public Direccion() { }

    public Direccion(String id, String alias, String ciudad, String barrio, String calle, String referencia) {
        this.id = Objects.requireNonNull(id, "id");
        this.alias = alias;
        this.ciudad = ciudad;
        this.barrio = barrio;
        this.calle = calle;
        this.referencia = referencia;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = Objects.requireNonNull(id, "id"); }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }
    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    @Override public String toString() {
        return alias + " - " + calle + " (" + ciudad + ")";
    }
}
