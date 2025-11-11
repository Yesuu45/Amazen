package co.edu.uniquindio.poo.amazen.Model.Persona;

public class SesionUsuario {

    private static SesionUsuario INSTANCIA;
    private Persona persona;

    private SesionUsuario() {}

    public static SesionUsuario instancia() {
        if (INSTANCIA == null) {
            INSTANCIA = new SesionUsuario();
        }
        return INSTANCIA;
    }

    public void iniciarSesion(Persona persona) {
        this.persona = persona;
    }

    public void cerrarSesion() {
        this.persona = null;
    }

    public boolean haySesionActiva() {
        return persona != null;
    }

    public Persona getPersona() {
        return persona;
    }
}
