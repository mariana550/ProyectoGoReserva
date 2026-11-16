package co.edu.ucaldas.goreserva.dto;

import java.time.LocalDate;
import java.util.List;

public record DisponibilidadResponse(Long canchaId,
                                     LocalDate fecha,
                                     boolean abierto,
                                     List<FranjaResponse> franjas) {
}
