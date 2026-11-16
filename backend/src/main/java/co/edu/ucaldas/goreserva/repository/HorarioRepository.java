package co.edu.ucaldas.goreserva.repository;

import co.edu.ucaldas.goreserva.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
    Optional<Horario> findByCanchaIdAndDiaSemana(Long canchaId, int diaSemana);
}
