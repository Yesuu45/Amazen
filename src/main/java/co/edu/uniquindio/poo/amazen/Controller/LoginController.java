package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;
import co.edu.uniquindio.poo.amazen.Model.Persona.SesionUsuario;

public class LoginController {

    private final Amazen amazen;
    private final SesionUsuario sesionUsuario;

    public LoginController() {
        this.amazen = Amazen.getInstance();
        this.sesionUsuario = SesionUsuario.instancia();
    }

    /**
     */
    public boolean iniciarSesion(String documento, String contrasena) {

            sesionUsuario.iniciarSesion(persona);
            System.out.println("✅ Sesión iniciada correctamente para: " + persona.getNombre());
            return true;
        }

        System.out.println("❌ Credenciales incorrectas.");
        return false;
    }

    /**
     * Cierra la sesión actual.
     */
    public void cerrarSesion() {
        sesionUsuario.cerrarSesion();
        System.out.println("🔒 Sesión cerrada correctamente.");
    }

    public boolean haySesionActiva() {
        return sesionUsuario.haySesionActiva();
    }

    public Persona getPersonaActiva() {
        return sesionUsuario.getPersona();
    }
}
