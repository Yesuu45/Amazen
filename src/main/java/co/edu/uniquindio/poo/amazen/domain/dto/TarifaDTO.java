package co.edu.uniquindio.poo.amazen.domain.dto;

import java.math.BigDecimal;

public class TarifaDTO {
    public BigDecimal subtotal = BigDecimal.ZERO;
    public BigDecimal extras   = BigDecimal.ZERO;
    public BigDecimal iva      = BigDecimal.ZERO;
    public BigDecimal total    = BigDecimal.ZERO;
    public String desglose; // texto simple por ahora
}
