package co.edu.ucaldas.goreserva.dto;

import java.math.BigDecimal;

public record FranjaResponse(String horaInicio,
                             String horaFin,
                             String estado,
                             BigDecimal valor,
                             String franja) {
}
