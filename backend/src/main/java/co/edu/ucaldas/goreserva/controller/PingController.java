package co.edu.ucaldas.goreserva.controller;

import co.edu.ucaldas.goreserva.repository.TipoCanchaRepository;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint de prueba (HU-05): confirma que el backend corre y que llega a MySQL. */
@RestController
public class PingController {
    private final TipoCanchaRepository tipos;

    public PingController(TipoCanchaRepository tipos) { this.tipos = tipos; }

    @GetMapping("/api/ping")
    public Map<String, Object> ping() {
        return Map.of("estado", "ok", "tiposDeCancha", tipos.count());
    }
}
