package co.edu.ucaldas.goreserva.controller;

import co.edu.ucaldas.goreserva.dto.CanchaAdminRequest;
import co.edu.ucaldas.goreserva.dto.CanchaAdminResponse;
import co.edu.ucaldas.goreserva.dto.HorarioDto;
import co.edu.ucaldas.goreserva.service.AdminCanchaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Solo ADMIN: protegido por la regla /api/admin/** de SecurityConfig. */
@RestController
@RequestMapping("/api/admin/canchas")
public class AdminCanchaController {
    private final AdminCanchaService servicio;

    public AdminCanchaController(AdminCanchaService servicio) { this.servicio = servicio; }

    /** GET /api/admin/canchas → todas, también las desactivadas */
    @GetMapping
    public List<CanchaAdminResponse> listar() {
        return servicio.listar();
    }

    /** POST /api/admin/canchas  body: {"nombre":"Cancha 5","tipo":"Fútbol 5","descripcion":"...","tarifaDiurna":80000,"tarifaNocturna":110000} */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CanchaAdminResponse crear(@Valid @RequestBody CanchaAdminRequest r) {
        return servicio.crear(r);
    }

    /** PUT /api/admin/canchas/1  (mismo body que crear) */
    @PutMapping("/{id}")
    public CanchaAdminResponse editar(@PathVariable Long id, @Valid @RequestBody CanchaAdminRequest r) {
        return servicio.editar(id, r);
    }

    /** PUT /api/admin/canchas/1/activar  y  /desactivar */
    @PutMapping("/{id}/activar")
    public CanchaAdminResponse activar(@PathVariable Long id) {
        return servicio.cambiarEstado(id, true);
    }

    @PutMapping("/{id}/desactivar")
    public CanchaAdminResponse desactivar(@PathVariable Long id) {
        return servicio.cambiarEstado(id, false);
    }

    /** GET /api/admin/canchas/1/horarios → [{"diaSemana":1,"horaApertura":"06:00:00","horaCierre":"23:00:00"}, ...] */
    @GetMapping("/{id}/horarios")
    public List<HorarioDto> horarios(@PathVariable Long id) {
        return servicio.horarios(id);
    }

    /** PUT /api/admin/canchas/1/horarios  body: la semana completa; el día que no venga queda cerrado */
    @PutMapping("/{id}/horarios")
    public List<HorarioDto> guardarHorarios(@PathVariable Long id, @Valid @RequestBody List<@Valid HorarioDto> semana) {
        return servicio.guardarHorarios(id, semana);
    }
}
