package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.Persona.*;
import co.edu.uniquindio.poo.amazen.Service.*;

import java.util.List;

public class LoginController {

    private Persona usuarioActivo;
    private final Amazen amazen = Amazen.getInstance();

    public boolean iniciarSesion(String documento, String contrasena) {

        // 1️⃣ Buscar en memoria (Amazen)
        Persona persona = amazen.buscarPersonaPorDocumento(documento);

        if (persona != null && persona.getContrasena().equals(contrasena)) {
            usuarioActivo = persona;
            SesionUsuario.instancia().iniciarSesion(persona);
            System.out.println("✅ Sesión iniciada (memoria): " + persona.getNombre());
            return true;
        }

        // 2️⃣ Buscar en archivos (backup)
        if (buscarEnArchivos(documento, contrasena)) {
            System.out.println("✅ Sesión iniciada (archivo): " + usuarioActivo.getNombre());
            return true;
        }

        System.out.println("❌ Credenciales incorrectas");
        return false;
    }

    private boolean buscarEnArchivos(String documento, String contrasena) {
        // Administradores
        for (Administrador admin : AdminFileService.cargarAdministradores()) {
            if (admin.getDocumento().equalsIgnoreCase(documento) && admin.getContrasena().equals(contrasena)) {
                usuarioActivo = admin;
                SesionUsuario.instancia().iniciarSesion(admin);
                amazen.agregarPersona(admin);
                return true;
            }
        }

        // Repartidores
        for (Repartidor r : RepartidorFileService.cargarRepartidores()) {
            if (r.getDocumento().equalsIgnoreCase(documento) && r.getContrasena().equals(contrasena)) {
                usuarioActivo = r;
                SesionUsuario.instancia().iniciarSesion(r);
                amazen.agregarPersona(r);
                return true;
            }
        }

        // Usuarios
        for (Usuario u : UsuarioFileService.cargarUsuarios()) {
            if (u.getDocumento().equalsIgnoreCase(documento) && u.getContrasena().equals(contrasena)) {
                usuarioActivo = u;
                SesionUsuario.instancia().iniciarSesion(u);
                amazen.agregarPersona(u);
                return true;
            }
        }

        return false;
    }

    public Persona getUsuarioActivo() {
        return usuarioActivo;
    }

    public void cerrarSesion() {
        usuarioActivo = null;
        SesionUsuario.instancia().cerrarSesion();
    }
}
