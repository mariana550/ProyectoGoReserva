package co.edu.ucaldas.goreserva.service;

import co.edu.ucaldas.goreserva.dto.DisponibilidadResponse;
import co.edu.ucaldas.goreserva.dto.FranjaResponse;
import co.edu.ucaldas.goreserva.dto.ReservaAdminResponse;
import co.edu.ucaldas.goreserva.dto.ReservaRequest;
import co.edu.ucaldas.goreserva.dto.ReservaResponse;
import co.edu.ucaldas.goreserva.exception.NegocioException;
import co.edu.ucaldas.goreserva.model.Cancha;
import co.edu.ucaldas.goreserva.model.EstadoReserva;
import co.edu.ucaldas.goreserva.model.Reserva;
import co.edu.ucaldas.goreserva.model.Usuario;
import co.edu.ucaldas.goreserva.repository.CanchaRepository;
import co.edu.ucaldas.goreserva.repository.ReservaRepository;
import co.edu.ucaldas.goreserva.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Crear (HU-12), listar (HU-14), cancelar (HU-15) y gestión del admin (HU-16). */
@Service
public class ReservaService {
    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");
    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    static final String HORA_NO_DISPONIBLE = "Esta hora ya no está disponible. Elige otra.";

    private final ReservaRepository reservas;
    private final CanchaRepository canchas;
    private final UsuarioRepository usuarios;
    private final CanchaService canchaService;

    public ReservaService(ReservaRepository reservas, CanchaRepository canchas,
                          UsuarioRepository usuarios, CanchaService canchaService) {
        this.reservas = reservas;
        this.canchas = canchas;
        this.usuarios = usuarios;
        this.canchaService = canchaService;
    }

    // ===================== CLIENTE: crear =====================

    @Transactional
    public ReservaResponse crear(ReservaRequest r, String correoUsuario) {
        // 1. Reutilizamos la disponibilidad: valida cancha, fechas, horario, horas pasadas y ocupadas.
        DisponibilidadResponse disp = canchaService.disponibilidad(r.canchaId(), r.fecha());
        String hora = r.horaInicio().format(HH_MM);

        FranjaResponse franja = disp.franjas().stream()
                .filter(f -> f.horaInicio().equals(hora))
                .findFirst()
                .orElseThrow(() -> new NegocioException(HttpStatus.BAD_REQUEST,
                        "La cancha no abre a esa hora"));

        if (!"LIBRE".equals(franja.estado())) {
            throw new NegocioException(HttpStatus.CONFLICT, HORA_NO_DISPONIBLE);
        }

        // 2. El usuario sale del token, no del formulario
        Usuario usuario = usuarios.findByCorreoIgnoreCase(correoUsuario)
                .orElseThrow(() -> new NegocioException(HttpStatus.UNAUTHORIZED, "Sesión no válida"));
        Cancha cancha = canchas.getReferenceById(r.canchaId());
        // 3. Armar la reserva. El valor lo pone el back, según la tarifa de esa hora.
        Reserva nueva = new Reserva();
        nueva.setUsuario(usuario);
        nueva.setCancha(cancha);
        nueva.setFecha(r.fecha());
        nueva.setHoraInicio(r.horaInicio());
        nueva.setHoraFin(r.horaInicio().plusHours(1));
        nueva.setValor(franja.valor());
        // 4. Guardar. El índice único uk_reserva_franja rechaza un choque simultáneo.
        try {
            return ReservaResponse.de(reservas.saveAndFlush(nueva));
        } catch (DataIntegrityViolationException e) {
            throw new NegocioException(HttpStatus.CONFLICT, HORA_NO_DISPONIBLE);
        }
    }
    // ===================== CLIENTE: listar y cancelar las suyas =====================
    @Transactional(readOnly = true)
    public List<ReservaResponse> mias(String correoUsuario) {
        return reservas.findByUsuarioCorreoIgnoreCaseOrderByFechaDescHoraInicioDesc(correoUsuario)
                .stream()
                .map(ReservaResponse::de)
                .toList();
    }

    @Transactional
    public ReservaResponse cancelar(Long id, String correoUsuario) {
        Reserva r = buscar(id);
        if (!r.getUsuario().getCorreo().equalsIgnoreCase(correoUsuario)) {
            throw new NegocioException(HttpStatus.FORBIDDEN, "No puedes cancelar la reserva de otra persona");
        }
        validarCancelable(r);
        r.cancelar();
        return ReservaResponse.de(reservas.save(r));
    }
    // ===================== ADMIN: ver todas y cancelar cualquiera =====================
    @Transactional(readOnly = true)
    public List<ReservaAdminResponse> listarTodas(LocalDate fecha, Long canchaId, EstadoReserva estado) {
        return reservas.buscarAdmin(fecha, canchaId, estado).stream()
                .map(ReservaAdminResponse::de)
                .toList();
    }
    @Transactional
    public ReservaAdminResponse cancelarComoAdmin(Long id) {
        Reserva r = buscar(id);
        validarCancelable(r);          // el admin no necesita ser el dueño
        r.cancelar();
        return ReservaAdminResponse.de(reservas.save(r));
    }
    // ===================== Reglas compartidas =====================
    private Reserva buscar(Long id) {
        return reservas.findById(id)
                .orElseThrow(() -> new NegocioException(HttpStatus.NOT_FOUND, "La reserva no existe"));
    }
    private void validarCancelable(Reserva r) {
        if (r.getEstado() == EstadoReserva.CANCELADA) {
            throw new NegocioException(HttpStatus.CONFLICT, "La reserva ya estaba cancelada");
        }
        LocalDateTime inicio = LocalDateTime.of(r.getFecha(), r.getHoraInicio());
        if (inicio.isBefore(LocalDateTime.now(ZONA))) {
            throw new NegocioException(HttpStatus.BAD_REQUEST, "No se puede cancelar una reserva que ya pasó");
        }
    }
}