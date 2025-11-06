package co.edu.uniquindio.poo.amazen.domain.usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Usuario {
    private String id;
    private String nombre;
    private String email;
    private final List<Direccion> direcciones = new ArrayList<>();

    public Usuario() { }

    public Usuario(String id, String nombre, String email) {
        this.id = Objects.requireNonNull(id, "id");
        this.nombre = nombre;
        this.email = email;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = Objects.requireNonNull(id, "id"); }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Direccion> getDirecciones() { return List.copyOf(direcciones); }
    public boolean addDireccion(Direccion d) { return direcciones.add(Objects.requireNonNull(d)); }
}
