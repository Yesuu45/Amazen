package co.edu.uniquindio.poo.amazen.domain.dto;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDTO {
    public String id;
    public String nombre;
    public String email;
    public List<DireccionDTO> direcciones = new ArrayList<>();
}