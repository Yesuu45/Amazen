package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Service.UsuarioFileService;
import java.util.List;

public class GestionUsuariosController {

    /** Editar información básica del usuario */
    public boolean actualizarInformacionUsuario(
            String documento,
            String nombre,
            String apellido,
            String email,
            String telefono,
            String celular
    ) {
        return UsuarioFileService.actualizarInformacionUsuario(
                documento, nombre, apellido, email, telefono, celular
        );
    }

    /** Agregar nueva dirección */
    public boolean agregarDireccion(String documento, String nuevaDireccion) {
        return UsuarioFileService.agregarDireccion(documento, nuevaDireccion);
    }

    /** Eliminar dirección existente */
    public boolean eliminarDireccion(String documento, String direccion) {
        return UsuarioFileService.eliminarDireccion(documento, direccion);
    }

    /** Obtener lista de direcciones */
    public List<String> obtenerDirecciones(String documento) {
        return UsuarioFileService.obtenerDirecciones(documento);
    }
}
