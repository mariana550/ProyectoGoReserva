package co.edu.ucaldas.goreserva.controller;

import co.edu.ucaldas.goreserva.dto.ReservaAdminResponse;
import co.edu.ucaldas.goreserva.model.EstadoReserva;
import co.edu.ucaldas.goreserva.service.ReservaService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Solo ADMIN: protegido por la regla /api/admin/** de SecurityConfig. */
@RestController
@RequestMapping("/api/admin/reservas")
public class AdminReservaController {
    private final ReservaService servicio;

    public AdminReservaController(ReservaService servicio) { this.servicio = servicio; }

    /** GET /api/admin/reservas?fecha=2026-11-16&canchaId=1&estado=CONFIRMADA (todos opcionales) */
    @GetMapping
    public List<ReservaAdminResponse> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Long canchaId,
            @RequestParam(required = false) EstadoReserva estado) {
        return servicio.listarTodas(fecha, canchaId, estado);
    }

    /** PUT /api/admin/reservas/7/cancelar */
    @PutMapping("/{id}/cancelar")
    public ReservaAdminResponse cancelar(@PathVariable Long id) {
        return servicio.cancelarComoAdmin(id);
    }
}
