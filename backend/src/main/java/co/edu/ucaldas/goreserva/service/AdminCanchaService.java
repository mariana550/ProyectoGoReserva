package co.edu.ucaldas.goreserva.service;

import co.edu.ucaldas.goreserva.dto.CanchaAdminRequest;
import co.edu.ucaldas.goreserva.dto.CanchaAdminResponse;
import co.edu.ucaldas.goreserva.dto.HorarioDto;
import co.edu.ucaldas.goreserva.exception.NegocioException;
import co.edu.ucaldas.goreserva.model.*;
import co.edu.ucaldas.goreserva.repository.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Gestión de canchas (HU-09), horarios (HU-10) y precios (HU-11) por el administrador. */
@Service
public class AdminCanchaService {
    // Rangos de cada franja: diurna antes de las 18:00, nocturna desde las 18:00
    static final LocalTime INICIO_DIA = LocalTime.of(0, 0);
    static final LocalTime INICIO_NOCHE = LocalTime.of(18, 0);
    static final LocalTime FIN_DIA = LocalTime.of(23, 59);
    // Horario con el que nace una cancha nueva: todos los días de 6:00 a 23:00
    static final LocalTime APERTURA_INICIAL = LocalTime.of(6, 0);
    static final LocalTime CIERRE_INICIAL = LocalTime.of(23, 0);

    private final CanchaRepository canchas;
    private final TipoCanchaRepository tipos;
    private final HorarioRepository horarios;
    private final TarifaRepository tarifas;

    public AdminCanchaService(CanchaRepository canchas, TipoCanchaRepository tipos,
                              HorarioRepository horarios, TarifaRepository tarifas) {
        this.canchas = canchas;
        this.tipos = tipos;
        this.horarios = horarios;
        this.tarifas = tarifas;
    }

    // ===================== HU-09: canchas =====================

    /** Todas las canchas, también las desactivadas. */
    @Transactional(readOnly = true)
    public List<CanchaAdminResponse> listar() {
        return canchas.findAllByOrderByNombreAsc().stream().map(this::aResponse).toList();
    }

    @Transactional
    public CanchaAdminResponse crear(CanchaAdminRequest r) {
        String nombre = r.nombre().trim();
        if (canchas.existsByNombreIgnoreCase(nombre)) {
            throw new NegocioException(HttpStatus.CONFLICT, "Ya existe una cancha con ese nombre");
        }
        Cancha c = new Cancha();
        llenar(c, r);
        c.setActiva(true);
        c = canchas.save(c);

        // Horario inicial (el admin lo cambia después) y precios del formulario
        for (int dia = 1; dia <= 7; dia++) {
            Horario h = new Horario();
            h.setCancha(c);
            h.setDiaSemana(dia);
            h.setHoraApertura(APERTURA_INICIAL);
            h.setHoraCierre(CIERRE_INICIAL);
            horarios.save(h);
        }
        guardarTarifas(c, r.tarifaDiurna(), r.tarifaNocturna());
        return aResponse(c);
    }

    @Transactional
    public CanchaAdminResponse editar(Long id, CanchaAdminRequest r) {
        Cancha c = buscar(id);
        if (canchas.existsByNombreIgnoreCaseAndIdNot(r.nombre().trim(), id)) {
            throw new NegocioException(HttpStatus.CONFLICT, "Ya existe una cancha con ese nombre");
        }
        llenar(c, r);
        guardarTarifas(c, r.tarifaDiurna(), r.tarifaNocturna());
        return aResponse(canchas.save(c));
    }

    /** Activar o desactivar. Una cancha desactivada no aparece en el catálogo del cliente. */
    @Transactional
    public CanchaAdminResponse cambiarEstado(Long id, boolean activa) {
        Cancha c = buscar(id);
        c.setActiva(activa);
        return aResponse(canchas.save(c));
    }

    // ===================== HU-10: horarios =====================

    @Transactional(readOnly = true)
    public List<HorarioDto> horarios(Long canchaId) {
        buscar(canchaId);
        return horarios.findByCanchaIdOrderByDiaSemanaAsc(canchaId).stream()
                .map(h -> new HorarioDto(h.getDiaSemana(), h.getHoraApertura(), h.getHoraCierre()))
                .toList();
    }

    /**
     * Reemplaza el horario de la semana. Un día que no viene en la lista queda cerrado.
     * Las reservas ya confirmadas no se tocan.
     */
    @Transactional
    public List<HorarioDto> guardarHorarios(Long canchaId, List<HorarioDto> semana) {
        Cancha c = buscar(canchaId);
        validarSemana(semana);

        horarios.deleteAll(horarios.findByCanchaIdOrderByDiaSemanaAsc(canchaId));
        horarios.flush();   // borra antes de insertar, por el índice único cancha + día

        List<Horario> nuevos = new ArrayList<>();
        for (HorarioDto d : semana) {
            Horario h = new Horario();
            h.setCancha(c);
            h.setDiaSemana(d.diaSemana());
            h.setHoraApertura(d.horaApertura());
            h.setHoraCierre(d.horaCierre());
            nuevos.add(h);
        }
        horarios.saveAll(nuevos);
        return horarios(canchaId);
    }

    /** Reglas del horario: un registro por día y cierre posterior a la apertura. */
    static void validarSemana(List<HorarioDto> semana) {
        Set<Integer> dias = new HashSet<>();
        for (HorarioDto d : semana) {
            if (d.diaSemana() < 1 || d.diaSemana() > 7) {
                throw new NegocioException(HttpStatus.BAD_REQUEST, "El día debe estar entre 1 y 7");
            }
            if (d.horaApertura() == null || d.horaCierre() == null) {
                throw new NegocioException(HttpStatus.BAD_REQUEST, "Cada día abierto necesita hora de apertura y de cierre");
            }
            if (!dias.add(d.diaSemana())) {
                throw new NegocioException(HttpStatus.BAD_REQUEST, "Hay un día repetido en el horario");
            }
            if (!d.horaCierre().isAfter(d.horaApertura())) {
                throw new NegocioException(HttpStatus.BAD_REQUEST,
                        "La hora de cierre debe ser posterior a la de apertura");
            }
            if (d.horaApertura().plusHours(1).isAfter(d.horaCierre())) {
                throw new NegocioException(HttpStatus.BAD_REQUEST,
                        "La cancha debe abrir al menos una hora");
            }
        }
    }

    // ===================== HU-11: precios =====================

    /** Crea o actualiza las tarifas DIURNA y NOCTURNA de la cancha. */
    private void guardarTarifas(Cancha c, BigDecimal diurna, BigDecimal nocturna) {
        validarPrecio(diurna);
        validarPrecio(nocturna);
        List<Tarifa> actuales = c.getId() == null ? List.of() : tarifas.findByCanchaId(c.getId());
        guardarTarifa(c, actuales, Franja.DIURNA, INICIO_DIA, INICIO_NOCHE, diurna);
        guardarTarifa(c, actuales, Franja.NOCTURNA, INICIO_NOCHE, FIN_DIA, nocturna);
    }

    private void guardarTarifa(Cancha c, List<Tarifa> actuales, Franja franja,
                               LocalTime inicio, LocalTime fin, BigDecimal valor) {
        Tarifa t = actuales.stream().filter(x -> x.getFranja() == franja).findFirst().orElseGet(() -> {
            Tarifa nueva = new Tarifa();
            nueva.setCancha(c);
            nueva.setFranja(franja);
            nueva.setHoraInicio(inicio);
            nueva.setHoraFin(fin);
            return nueva;
        });
        t.setValor(valor);
        tarifas.save(t);
    }

    static void validarPrecio(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new NegocioException(HttpStatus.BAD_REQUEST, "El precio es obligatorio y debe ser mayor que cero");
        }
    }

    // ===================== Ayudantes =====================

    private void llenar(Cancha c, CanchaAdminRequest r) {
        TipoCancha tipo = tipos.findByNombreIgnoreCase(r.tipo().trim())
                .orElseThrow(() -> new NegocioException(HttpStatus.BAD_REQUEST, "El tipo de cancha no existe"));
        c.setNombre(r.nombre().trim());
        c.setTipo(tipo);
        c.setDescripcion(r.descripcion() == null || r.descripcion().isBlank() ? null : r.descripcion().trim());
    }

    private Cancha buscar(Long id) {
        return canchas.findById(id)
                .orElseThrow(() -> new NegocioException(HttpStatus.NOT_FOUND, "La cancha no existe"));
    }

    private CanchaAdminResponse aResponse(Cancha c) {
        BigDecimal diurna = null;
        BigDecimal nocturna = null;
        for (Tarifa t : tarifas.findByCanchaId(c.getId())) {
            if (t.getFranja() == Franja.DIURNA) diurna = t.getValor();
            else nocturna = t.getValor();
        }
        return new CanchaAdminResponse(c.getId(), c.getNombre(), c.getTipo().getNombre(),
                c.getDescripcion(), c.isActiva(), diurna, nocturna);
    }
}
