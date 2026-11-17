package co.edu.ucaldas.goreserva.dto;

import co.edu.ucaldas.goreserva.model.Reserva;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Reserva con los datos del cliente (solo para el administrador). */
public record ReservaAdminResponse(Long id, String codigo, String cancha, String tipo,
                                   LocalDate fecha, String horaInicio, String horaFin,
                                   BigDecimal valor, String estado,
                                   String cliente, String correo, String telefono,
                                   LocalDateTime creadaEn) {

    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

    public static ReservaAdminResponse de(Reserva r) {
        return new ReservaAdminResponse(
                r.getId(),
                String.format("GR-%05d", r.getId()),
                r.getCancha().getNombre(),
                r.getCancha().getTipo().getNombre(),
                r.getFecha(),
                r.getHoraInicio().format(HH_MM),
                r.getHoraFin().format(HH_MM),
                r.getValor(),
                r.getEstado().name(),
                r.getUsuario().getNombre(),
                r.getUsuario().getCorreo(),
                r.getUsuario().getTelefono(),
                r.getCreadaEn());
    }
}
