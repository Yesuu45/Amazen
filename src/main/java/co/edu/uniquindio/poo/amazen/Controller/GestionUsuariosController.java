package co.edu.uniquindio.poo.amazen.Controller;

import co.edu.uniquindio.poo.amazen.Service.UsuarioFileService;
import java.util.List;

/**
 * Controlador encargado de gestionar la información del usuario,
 * incluyendo actualización de datos personales y administración de direcciones.
 *
 * Todas las operaciones delegan en {@link UsuarioFileService}.
 */
public class GestionUsuariosController {

    /**
     * Actualiza la información básica de un usuario.
     *
     * @param documento documento único del usuario
     * @param nombre    nuevo nombre (opcional)
     * @param apellido  nuevo apellido (opcional)
     * @param email     nuevo email (opcional)
     * @param telefono  nuevo teléfono fijo (opcional)
     * @param celular   nuevo número de celular (opcional)
     * @return true si la actualización fue exitosa
     */
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

    /**
     * Agrega una nueva dirección al usuario.
     *
     * @param documento      documento del usuario
     * @param nuevaDireccion dirección a añadir
     * @return true si se agregó correctamente
     */
    public boolean agregarDireccion(String documento, String nuevaDireccion) {
        return UsuarioFileService.agregarDireccion(documento, nuevaDireccion);
    }

    /**
     * Elimina una dirección registrada del usuario.
     *
     * @param documento documento del usuario
     * @param direccion dirección a eliminar
     * @return true si la dirección fue eliminada
     */
    public boolean eliminarDireccion(String documento, String direccion) {
        return UsuarioFileService.eliminarDireccion(documento, direccion);
    }

    /**
     * Obtiene todas las direcciones registradas por un usuario.
     *
     * @param documento documento del usuario
     * @return lista de direcciones
     */
    public List<String> obtenerDirecciones(String documento) {
        return UsuarioFileService.obtenerDirecciones(documento);
    }
}
