package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Model.Amazen;
import co.edu.uniquindio.poo.amazen.Model.Persona.Usuario;
import co.edu.uniquindio.poo.amazen.Service.UsuarioFileService;

public class RegistroController {

    public boolean registrarUsuario(Usuario usuario) {
        if (UsuarioFileService.existeUsuario(usuario.getDocumento())) {
            return false; // ya existe
        }

        // ✅ Guarda en el archivo
        UsuarioFileService.guardarUsuario(usuario);

        // ✅ Agrega también al sistema Amazen (en memoria)
        Amazen.getInstance().agregarPersona(usuario);

        System.out.println("✅ Usuario registrado: " + usuario.getNombre());
        return true;
    }
}
