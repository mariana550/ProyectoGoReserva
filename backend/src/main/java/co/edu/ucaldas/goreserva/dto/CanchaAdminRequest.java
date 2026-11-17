package co.edu.ucaldas.goreserva.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/** Formulario de crear y editar cancha del administrador (HU-09 y HU-11). */
public record CanchaAdminRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 80) String nombre,
        @NotBlank(message = "El tipo de cancha es obligatorio") String tipo,
        @Size(max = 255) String descripcion,
        @NotNull(message = "El precio diurno es obligatorio")
        @Positive(message = "El precio diurno debe ser mayor que cero") BigDecimal tarifaDiurna,
        @NotNull(message = "El precio nocturno es obligatorio")
        @Positive(message = "El precio nocturno debe ser mayor que cero") BigDecimal tarifaNocturna) {
}
