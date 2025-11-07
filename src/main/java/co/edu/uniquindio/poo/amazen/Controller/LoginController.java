package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.Persona.Persona;

/**
 * Controlador de autenticación.
 * Se apoya en el singleton Amazen para acceder a la lista global de usuarios.
 */
public class LoginController {

    private Persona personaActiva;

    /**
     * Intenta iniciar sesión con el documento y contraseña dados.
     * @return true si las credenciales son válidas, false en caso contrario.
     */
    public boolean iniciarSesion(String documento, String contrasena) {
        // obtener instancia única del modelo
        Amazen amazen = Amazen.getInstance();

        // buscar persona con ese documento
        for (Persona p : amazen.getListaPersonas()) {
            if (p.getDocumento().equalsIgnoreCase(documento) &&
                    p.getContrasena().equals(contrasena)) {
                personaActiva = p;
                return true;
            }
        }
        return false;
    }

    /** Devuelve la persona actualmente autenticada */
    public Persona getPersonaActiva() {
        return personaActiva;
    }
}
