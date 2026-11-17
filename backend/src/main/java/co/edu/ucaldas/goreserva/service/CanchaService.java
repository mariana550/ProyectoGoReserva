package co.edu.ucaldas.goreserva.service;

import co.edu.ucaldas.goreserva.dto.canchaResponse;
import co.edu.ucaldas.goreserva.exception.NegocioException;
import co.edu.ucaldas.goreserva.model.*;
import co.edu.ucaldas.goreserva.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.ucaldas.goreserva.dto.DisponibilidadResponse;
import co.edu.ucaldas.goreserva.dto.FranjaResponse;

/** Catálogo de canchas (HU-09). */
@Service
public class CanchaService {
    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");
    private static final int DIAS_MAXIMOS = 30;   // días en adelante que se puede reservar

    private final CanchaRepository canchas;
    private final TarifaRepository tarifas;
    public final TipoCanchaRepository tipos;
    private final HorarioRepository horarios;
    private final ReservaRepository reservas;

    public CanchaService(CanchaRepository canchas,
                         TarifaRepository tarifas,
                         TipoCanchaRepository tipos,
                         HorarioRepository horarios,
                         ReservaRepository reservas) {
        this.canchas = canchas;
        this.tarifas = tarifas;
        this.tipos = tipos;
        this.horarios = horarios;
        this.reservas = reservas;
    }

    @Transactional(readOnly = true)
    public List<canchaResponse> listarActivas() {
        return canchas.findByActivaTrueOrderByNombreAsc().stream()
                .map(this::aResponse)
                .toList();
    }

    /**
     * Detalle de una cancha.
     */
    @Transactional(readOnly = true)
    public canchaResponse obtener(Long id) {
        return aResponse(buscarActiva(id));
    }

    /**
     * Franjas de una hora de la cancha en una fecha.
     */
    @Transactional(readOnly = true)
    public DisponibilidadResponse disponibilidad(Long canchaId, LocalDate fecha) {
        buscarActiva(canchaId);

        LocalDate hoy = LocalDate.now(ZONA);
        if (fecha.isBefore(hoy)) {
            throw new NegocioException(HttpStatus.BAD_REQUEST, "No se puede consultar una fecha pasada");
        }
        if (fecha.isAfter(hoy.plusDays(DIAS_MAXIMOS))) {
            throw new NegocioException(HttpStatus.BAD_REQUEST,
                    "Solo se puede reservar con máximo " + DIAS_MAXIMOS + " días de anticipación");
        }
        // 1. A qué hora abre y cierra ese día de la semana
        Optional<Horario> horario = horarios.findByCanchaIdAndDiaSemana(canchaId, fecha.getDayOfWeek().getValue());
        if (horario.isEmpty()) {
            return new DisponibilidadResponse(canchaId, fecha, false, List.of());
        }
        // 2. Horas que ya tienen una reserva CONFIRMADA
        Set<LocalTime> ocupadas = reservas
                .findByCanchaIdAndFechaAndEstado(canchaId, fecha, EstadoReserva.CONFIRMADA)
                .stream().map(Reserva::getHoraInicio).collect(Collectors.toSet());
        // 3. Partir el horario en franjas de 1 hora
        List<Tarifa> tarifasCancha = tarifas.findByCanchaId(canchaId);
        LocalDateTime ahora = LocalDateTime.now(ZONA);
        LocalTime cierre = horario.get().getHoraCierre();
        List<FranjaResponse> franjas = new ArrayList<>();

        for (LocalTime hora = horario.get().getHoraApertura();
             hora.isBefore(cierre) && !hora.plusHours(1).isAfter(cierre);
             hora = hora.plusHours(1)) {

            String estado;
            if (LocalDateTime.of(fecha, hora).isBefore(ahora)) estado = "PASADA";
            else if (ocupadas.contains(hora)) estado = "OCUPADA";
            else estado = "LIBRE";

            Tarifa tarifa = tarifaPara(tarifasCancha, hora);
            franjas.add(new FranjaResponse(
                    hora.format(HH_MM), hora.plusHours(1).format(HH_MM), estado,
                    tarifa == null ? null : tarifa.getValor(),
                    tarifa == null ? null : tarifa.getFranja().name()));
        }
        return new DisponibilidadResponse(canchaId, fecha, true, franjas);
    }

    private canchaResponse aResponse(Cancha c) {
        BigDecimal diurna = null;
        BigDecimal nocturna = null;
        for (Tarifa t : tarifas.findByCanchaId(c.getId())) {
            if (t.getFranja() == Franja.DIURNA) diurna = t.getValor();
            else nocturna = t.getValor();
        }
        return new canchaResponse(c.getId(), c.getNombre(), c.getTipo().getNombre(),
                c.getDescripcion(), diurna, nocturna);
    }

    private Cancha buscarActiva(Long id) {
        return canchas.findById(id)
                .filter(Cancha::isActiva)
                .orElseThrow(() -> new NegocioException(HttpStatus.NOT_FOUND, "La cancha no existe"));
    }

    /**
     * Tarifa cuyo rango [inicio, fin) contiene la hora. Ej.: 18:00 → NOCTURNA
     */
    private Tarifa tarifaPara(List<Tarifa> lista, LocalTime hora) {
        for (Tarifa t : lista) {
            if (!hora.isBefore(t.getHoraInicio()) && hora.isBefore(t.getHoraFin())) return t;
        }
        return null;
    }

    /** Nombres de los tipos de cancha para los filtros del catálogo: ["Baloncesto", "Fútbol 5", ...] */
    @Transactional(readOnly = true)
    public List<String> tipos() {
        return tipos.findAllByOrderByNombreAsc().stream()
                .map(TipoCancha::getNombre)
                .toList();
    }
}
