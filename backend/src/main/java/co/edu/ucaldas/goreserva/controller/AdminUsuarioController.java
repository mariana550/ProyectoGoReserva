package co.edu.ucaldas.goreserva.controller;

import co.edu.ucaldas.goreserva.dto.RegistroRequest;
import co.edu.ucaldas.goreserva.dto.UsuarioResponse;
import co.edu.ucaldas.goreserva.model.Rol;
import co.edu.ucaldas.goreserva.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Solo un ADMIN puede registrar administradores (protegido por /api/admin/** en SecurityConfig). */
@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminUsuarioController {
    private final AuthService auth;

    public AdminUsuarioController(AuthService auth) { this.auth = auth; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crearAdmin(@Valid @RequestBody RegistroRequest r) {
        return UsuarioResponse.de(auth.registrar(r, Rol.ADMIN));
    }
}
