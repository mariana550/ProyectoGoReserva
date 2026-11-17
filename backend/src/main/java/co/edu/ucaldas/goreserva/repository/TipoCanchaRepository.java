package co.edu.ucaldas.goreserva.repository;

import co.edu.ucaldas.goreserva.model.TipoCancha;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoCanchaRepository extends JpaRepository<TipoCancha, Long> {
    List<TipoCancha> findAllByOrderByNombreAsc();
    Optional<TipoCancha> findByNombreIgnoreCase(String nombre);
}
