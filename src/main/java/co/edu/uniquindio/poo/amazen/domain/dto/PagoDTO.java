package co.edu.uniquindio.poo.amazen.domain.dto;

import java.math.BigDecimal;

public class PagoDTO {
    public String envioId;
    public BigDecimal montoTotal;
    public String medio;           // "Tarjeta", "Efectivo", etc.
    public String numeroTarjeta;   // opcional segun medio
    public String titular;         // opcional
    public String referencia;      // si ya viene de UI
}
