package co.edu.ucaldas.goreserva.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import co.edu.ucaldas.goreserva.dto.DisponibilidadResponse;
import co.edu.ucaldas.goreserva.dto.FranjaResponse;
import co.edu.ucaldas.goreserva.exception.NegocioException;
import co.edu.ucaldas.goreserva.model.*;
import co.edu.ucaldas.goreserva.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** HU-10: generación de las franjas de una hora para una fecha. */
class CanchaServiceTest {
    private CanchaRepository canchas;
    private TarifaRepository tarifas;
    private HorarioRepository horarios;
    private ReservaRepository reservas;
    private CanchaService service;
    private LocalDate manana;

    @BeforeEach
    void setUp() {
        canchas = mock(CanchaRepository.class);
        tarifas = mock(TarifaRepository.class);
        horarios = mock(HorarioRepository.class);
        reservas = mock(ReservaRepository.class);
        service = new CanchaService(canchas, tarifas, mock(TipoCanchaRepository.class), horarios, reservas);
        manana = LocalDate.now(ZoneId.of("America/Bogota")).plusDays(1);

        when(canchas.findById(1L)).thenReturn(Optional.of(new Cancha()));   // activa por defecto
        when(tarifas.findByCanchaId(1L)).thenReturn(List.of(
                tarifa(Franja.DIURNA, 0, 18, 80000), tarifa(Franja.NOCTURNA, 18, 23, 110000)));
    }

    @Test
    void generaUnaFranjaPorHoraYMarcaLasOcupadas() {
        when(horarios.findByCanchaIdAndDiaSemana(1L, manana.getDayOfWeek().getValue()))
                .thenReturn(Optional.of(horario(17, 20)));
        Reserva ocupada = new Reserva();
        ocupada.setHoraInicio(LocalTime.of(18, 0));
        when(reservas.findByCanchaIdAndFechaAndEstado(1L, manana, EstadoReserva.CONFIRMADA))
                .thenReturn(List.of(ocupada));

        DisponibilidadResponse r = service.disponibilidad(1L, manana);

        assertTrue(r.abierto());
        List<FranjaResponse> f = r.franjas();
        assertEquals(3, f.size());                                   // 17-18, 18-19, 19-20
        assertEquals("17:00", f.get(0).horaInicio());
        assertEquals("18:00", f.get(0).horaFin());
        assertEquals("LIBRE", f.get(0).estado());
        assertEquals("OCUPADA", f.get(1).estado());
        assertEquals("LIBRE", f.get(2).estado());
        assertEquals(0, new BigDecimal("80000").compareTo(f.get(0).valor()));   // diurna
        assertEquals(0, new BigDecimal("110000").compareTo(f.get(2).valor()));  // nocturna
    }

    @Test
    void diaSinHorarioQuedaCerrado() {
        when(horarios.findByCanchaIdAndDiaSemana(1L, manana.getDayOfWeek().getValue()))
                .thenReturn(Optional.empty());
        DisponibilidadResponse r = service.disponibilidad(1L, manana);
        assertFalse(r.abierto());
        assertTrue(r.franjas().isEmpty());
    }

    @Test
    void noDejaConsultarFechasPasadas() {
        assertThrows(NegocioException.class, () -> service.disponibilidad(1L, manana.minusDays(3)));
    }

    @Test
    void cierreAntesDeAperturaNoSePermite() {
        var malo = new co.edu.ucaldas.goreserva.dto.HorarioDto(1, LocalTime.of(20, 0), LocalTime.of(8, 0));
        NegocioException e = assertThrows(NegocioException.class,
                () -> AdminCanchaService.validarSemana(List.of(malo)));
        assertEquals("La hora de cierre debe ser posterior a la de apertura", e.getMessage());
    }

    @Test
    void precioDebeSerMayorQueCero() {
        assertThrows(NegocioException.class, () -> AdminCanchaService.validarPrecio(BigDecimal.ZERO));
        assertDoesNotThrow(() -> AdminCanchaService.validarPrecio(new BigDecimal("80000")));
    }

    private static Horario horario(int abre, int cierra) {
        Horario h = new Horario();
        h.setHoraApertura(LocalTime.of(abre, 0));
        h.setHoraCierre(LocalTime.of(cierra, 0));
        return h;
    }

    private static Tarifa tarifa(Franja franja, int desde, int hasta, int valor) {
        Tarifa t = new Tarifa();
        t.setFranja(franja);
        t.setHoraInicio(LocalTime.of(desde, 0));
        t.setHoraFin(LocalTime.of(hasta, 0));
        t.setValor(new BigDecimal(valor));
        return t;
    }
}
