package co.edu.ucaldas.goreserva.repository;

import co.edu.ucaldas.goreserva.model.Horario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
    Optional<Horario> findByCanchaIdAndDiaSemana(Long canchaId, int diaSemana);

    /** Admin: el horario de los 7 días de una cancha */
    List<Horario> findByCanchaIdOrderByDiaSemanaAsc(Long canchaId);
}
