package co.edu.ucaldas.goreserva.repository;

import co.edu.ucaldas.goreserva.model.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
    List<Tarifa> findByCanchaId(Long canchaId);
}
