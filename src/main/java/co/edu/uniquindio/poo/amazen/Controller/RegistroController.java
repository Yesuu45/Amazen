package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.Persona.Usuario;
import co.edu.uniquindio.poo.amazen.Service.UsuarioFileService;

/**
 * Gestiona el registro de nuevos usuarios en archivos
 * y en la instancia central de {@link Amazen}.
 */
public class RegistroController {

    /**
     * Registra un usuario nuevo si su documento no existe.
     * Persiste en archivo y en memoria.
     *
     * @param usuario usuario a registrar
     * @return true si el registro fue exitoso, false si ya existía el documento
     */
    public boolean registrarUsuario(Usuario usuario) {
        if (UsuarioFileService.existeUsuario(usuario.getDocumento())) {
            return false;
        }

        UsuarioFileService.guardarUsuario(usuario);
        Amazen.getInstance().agregarPersona(usuario);

        System.out.println("✅ Usuario registrado: " + usuario.getNombre());
        return true;
    }
}
