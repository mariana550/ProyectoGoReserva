package co.edu.ucaldas.goreserva.dto;

import java.math.BigDecimal;

// Lo que necesitamos para pintar en el front las tarjetas de la cancha
public record canchaResponse(
        Long id,
        String nombre,
        String tipo,
        String descripcion,
        BigDecimal tarifaDiurna,
        BigDecimal tarifaNocturna) {
}