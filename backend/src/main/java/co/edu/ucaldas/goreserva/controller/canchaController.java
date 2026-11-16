package co.edu.ucaldas.goreserva.controller;

import co.edu.ucaldas.goreserva.dto.canchaResponse;
import co.edu.ucaldas.goreserva.dto.DisponibilidadResponse;
import co.edu.ucaldas.goreserva.service.CanchaService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/canchas")
public class canchaController {
    private final CanchaService servicio;

    public canchaController(CanchaService servicio) { this.servicio = servicio; }

    /** GET /api/canchas → todas las canchas activas */
    @GetMapping
    public List<canchaResponse> listar() {
        return servicio.listarActivas();
    }

    /** GET /api/canchas/tipos → ["Baloncesto", "Fútbol 5", ...] para los filtros */
    @GetMapping("/tipos")
    public List<String> tipos() {
        return servicio.tipos();
    }

    /** GET /api/canchas/1 → una cancha (encabezado de la pantalla de horarios) */
    @GetMapping("/{id}")
    public canchaResponse obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    /** GET /api/canchas/1/disponibilidad?fecha=2026-11-14 → franjas de ese día */
    @GetMapping("/{id}/disponibilidad")
    public DisponibilidadResponse disponibilidad(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return servicio.disponibilidad(id, fecha);
    }
}
