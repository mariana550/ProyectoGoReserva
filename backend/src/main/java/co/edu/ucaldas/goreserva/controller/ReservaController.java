package co.edu.ucaldas.goreserva.controller;

import co.edu.ucaldas.goreserva.dto.ReservaRequest;
import co.edu.ucaldas.goreserva.dto.ReservaResponse;
import co.edu.ucaldas.goreserva.service.ReservaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaService servicio;

    public ReservaController(ReservaService servicio) { this.servicio = servicio; }

    /** POST /api/reservas  body: {"canchaId":1,"fecha":"2026-11-14","horaInicio":"18:00"} */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponse crear(@Valid @RequestBody ReservaRequest r, Authentication auth) {
        return servicio.crear(r, auth.getName());
    }

    /** GET /api/reservas/mias → reservas del usuario logueado */
    @GetMapping("/mias")
    public List<ReservaResponse> mias(Authentication auth) {
        return servicio.mias(auth.getName());
    }

    /** PUT /api/reservas/7/cancelar */
    @PutMapping("/{id}/cancelar")
    public ReservaResponse cancelar(@PathVariable Long id, Authentication auth) {
        return servicio.cancelar(id, auth.getName());
    }
}
