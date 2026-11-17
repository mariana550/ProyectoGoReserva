package co.edu.ucaldas.goreserva.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

/** Horario de un día: diaSemana 1 = lunes ... 7 = domingo (HU-10). */
public record HorarioDto(
        @Min(value = 1, message = "El día debe estar entre 1 y 7")
        @Max(value = 7, message = "El día debe estar entre 1 y 7") int diaSemana,
        @NotNull(message = "La hora de apertura es obligatoria") LocalTime horaApertura,
        @NotNull(message = "La hora de cierre es obligatoria") LocalTime horaCierre) {
}
