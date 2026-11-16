package co.edu.ucaldas.goreserva.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaRequest (
    @NotNull(message = "La cancha es obligatoria") Long canchaId,
    @NotNull(message = "La fecha es obligatoria") LocalDate fecha,
    @NotNull(message = "La hora es obligatoria") LocalTime horaInicio){
}
