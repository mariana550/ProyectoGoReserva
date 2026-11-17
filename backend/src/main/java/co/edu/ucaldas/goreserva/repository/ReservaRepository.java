package co.edu.ucaldas.goreserva.repository;

import co.edu.ucaldas.goreserva.model.EstadoReserva;
import co.edu.ucaldas.goreserva.model.Reserva;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByCanchaIdAndFechaAndEstado(Long canchaId, LocalDate fecha, EstadoReserva estado);

    List<Reserva> findByUsuarioCorreoIgnoreCaseOrderByFechaDescHoraInicioDesc(String correo);

    /** Admin: cada filtro es opcional; si llega null, no filtra por ese campo. */
    @Query("""
            SELECT r FROM Reserva r
            WHERE (:fecha IS NULL OR r.fecha = :fecha)
              AND (:canchaId IS NULL OR r.cancha.id = :canchaId)
              AND (:estado IS NULL OR r.estado = :estado)
            ORDER BY r.fecha DESC, r.horaInicio DESC
            """)
    List<Reserva> buscarAdmin(@Param("fecha") LocalDate fecha,
                              @Param("canchaId") Long canchaId,
                              @Param("estado") EstadoReserva estado);
}