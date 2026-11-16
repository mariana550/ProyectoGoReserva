package co.edu.ucaldas.goreserva.dto;

import co.edu.ucaldas.goreserva.model.Reserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record ReservaResponse (
        Long id,
        String codigo,
        Long canchaId,
        String cancha,
        String tipo,
        LocalDate fecha,
        String horaInicio,
        String horaFin,
        BigDecimal valor,
        String estado) {

    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");
    public static ReservaResponse de(Reserva r) {
        return new ReservaResponse(
                r.getId(),
                String.format("GR-%05d", r.getId()),
                r.getCancha().getId(),
                r.getCancha().getNombre(),
                r.getCancha().getTipo().getNombre(),
                r.getFecha(),
                r.getHoraInicio().format(HH_MM),
                r.getHoraFin().format(HH_MM),
                r.getValor(),
                r.getEstado().name());
    }
}
