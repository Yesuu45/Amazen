package co.edu.uniquindio.poo.amazen.domain.dto;

import java.util.ArrayList;
import java.util.List;

public class EnvioDTO {
    public String id;
    public String clienteId;
    public String origenId;
    public String destinoId;
    public List<PaqueteDTO> paquetes = new ArrayList<>();
    public String prioridad;       // "Normal"/"Alta"
    public List<String> extras = new ArrayList<>(); // etiquetas por ahora
    public TarifaDTO tarifa;       // cálculo del Paso 5
    public String estado;          // espejo de EstadoEnvio
    public String repartidorId;    // si aplica
    public String pagoId;          // si aplica
}
