package co.edu.ucaldas.goreserva.repository;

import co.edu.ucaldas.goreserva.model.Cancha;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CanchaRepository extends JpaRepository<Cancha, Long> {
    List<Cancha> findByActivaTrueOrderByNombreAsc();

    /** Admin: todas, también las desactivadas */
    List<Cancha> findAllByOrderByNombreAsc();
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
