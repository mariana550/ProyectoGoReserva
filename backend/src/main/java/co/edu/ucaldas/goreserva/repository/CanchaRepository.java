package co.edu.ucaldas.goreserva.repository;

import co.edu.ucaldas.goreserva.model.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CanchaRepository extends JpaRepository<Cancha, Long> {
    List<Cancha> findByActivaTrueOrderByNombreAsc();
}
