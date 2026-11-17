package co.edu.ucaldas.goreserva.dto;

import java.math.BigDecimal;

/** Cancha vista por el administrador: incluye si está activa. */
public record CanchaAdminResponse(
        Long id,
        String nombre,
        String tipo,
        String descripcion,
        boolean activa,
        BigDecimal tarifaDiurna,
        BigDecimal tarifaNocturna) {
}
