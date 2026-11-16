package co.edu.ucaldas.goreserva.repository;

import co.edu.ucaldas.goreserva.model.EstadoReserva;
import co.edu.ucaldas.goreserva.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByCanchaIdAndFechaAndEstado(Long canchaId,
                                                  LocalDate fecha,
                                                  EstadoReserva estado);
    List<Reserva> findByUsuarioCorreoIgnoreCaseOrderByFechaDescHoraInicioDesc(String correo);
}
