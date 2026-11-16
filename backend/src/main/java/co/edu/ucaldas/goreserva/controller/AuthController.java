package co.edu.ucaldas.goreserva.controller;

import co.edu.ucaldas.goreserva.dto.AuthResponse;
import co.edu.ucaldas.goreserva.dto.LoginRequest;
import co.edu.ucaldas.goreserva.dto.RegistroRequest;
import co.edu.ucaldas.goreserva.dto.UsuarioResponse;
import co.edu.ucaldas.goreserva.model.Rol;
import co.edu.ucaldas.goreserva.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registro(@Valid @RequestBody RegistroRequest r) {
        return UsuarioResponse.de(auth.registrar(r, Rol.CLIENTE));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest r) {
        return auth.login(r);
    }
}
