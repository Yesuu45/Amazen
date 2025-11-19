package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.Persona.*;
import co.edu.uniquindio.poo.amazen.Service.*;

import java.util.Objects;

/**
 * Gestiona el flujo de inicio y cierre de sesión,
 * autenticando primero contra Amazen (memoria) y luego contra archivos.
 */
public class LoginController {

    private Persona usuarioActivo;
    private final Amazen amazen = Amazen.getInstance();

    /**
     * Intenta iniciar sesión con las credenciales dadas.
     * Primero busca en memoria, luego en los archivos persistentes.
     *
     * @param documento  documento del usuario
     * @param contrasena contraseña ingresada
     * @return true si las credenciales son válidas
     */
    public boolean iniciarSesion(String documento, String contrasena) {

        // Búsqueda en memoria
        Persona persona = amazen.buscarPersonaPorDocumento(documento);

        if (persona != null && Objects.equals(persona.getContrasena(), contrasena)) {
            usuarioActivo = persona;
            SesionUsuario.instancia().iniciarSesion(persona);
            System.out.println("✅ Sesión iniciada (memoria): " + persona.getNombre());
            return true;
        }

        // Búsqueda en archivos (backup)
        if (buscarEnArchivos(documento, contrasena)) {
            System.out.println("✅ Sesión iniciada (archivo): " + usuarioActivo.getNombre());
            return true;
        }

        System.out.println("❌ Credenciales incorrectas");
        return false;
    }

    /**
     * Busca al usuario en los archivos de administradores, repartidores y usuarios normales.
     *
     * @param documento  documento del usuario
     * @param contrasena contraseña ingresada
     * @return true si se encontró y autenticó correctamente
     */
    private boolean buscarEnArchivos(String documento, String contrasena) {
        for (Administrador admin : AdminFileService.cargarAdministradores()) {
            if (admin.getDocumento().equalsIgnoreCase(documento)
                    && admin.getContrasena().equals(contrasena)) {
                usuarioActivo = admin;
                SesionUsuario.instancia().iniciarSesion(admin);
                amazen.agregarPersona(admin);
                return true;
            }
        }

        for (Repartidor r : RepartidorFileService.cargarRepartidores()) {
            if (r.getDocumento().equalsIgnoreCase(documento)
                    && r.getContrasena().equals(contrasena)) {
                usuarioActivo = r;
                SesionUsuario.instancia().iniciarSesion(r);
                amazen.agregarPersona(r);
                return true;
            }
        }

        for (Usuario u : UsuarioFileService.cargarUsuarios()) {
            if (u.getDocumento().equalsIgnoreCase(documento)
                    && u.getContrasena().equals(contrasena)) {
                usuarioActivo = u;
                SesionUsuario.instancia().iniciarSesion(u);
                amazen.agregarPersona(u);
                return true;
            }
        }

        return false;
    }

    /**
     * Devuelve el usuario autenticado actualmente.
     *
     * @return persona activa o null si no hay sesión
     */
    public Persona getUsuarioActivo() {
        return usuarioActivo;
    }

    /**
     * Cierra la sesión actual y limpia la referencia en memoria.
     */
    public void cerrarSesion() {
        usuarioActivo = null;
        SesionUsuario.instancia().cerrarSesion();
    }
}
