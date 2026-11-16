package co.edu.ucaldas.goreserva.service;

import co.edu.ucaldas.goreserva.dto.AuthResponse;
import co.edu.ucaldas.goreserva.dto.LoginRequest;
import co.edu.ucaldas.goreserva.dto.RegistroRequest;
import co.edu.ucaldas.goreserva.exception.NegocioException;
import co.edu.ucaldas.goreserva.model.Rol;
import co.edu.ucaldas.goreserva.model.Usuario;
import co.edu.ucaldas.goreserva.repository.UsuarioRepository;
import co.edu.ucaldas.goreserva.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Registro (HU-06), inicio de sesión (HU-07) y alta de administradores (HU-08). */
@Service
public class AuthService {
    static final String CREDENCIALES_INVALIDAS = "Correo o contraseña incorrectos";
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwt) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional
    public Usuario registrar(RegistroRequest r, Rol rol) {
        String correo = r.correo().trim().toLowerCase();
        if (usuarios.existsByCorreoIgnoreCase(correo)) {
            throw new NegocioException(HttpStatus.CONFLICT, "Ya existe una cuenta con este correo");
        }
        Usuario u = new Usuario();
        u.setNombre(r.nombre().trim());
        u.setCorreo(correo);
        u.setTelefono(r.telefono().trim());
        u.setPasswordHash(encoder.encode(r.password()));   // BCrypt
        u.setRol(rol);
        return usuarios.save(u);
    }

    public AuthResponse login(LoginRequest r) {
        Usuario u = usuarios.findByCorreoIgnoreCase(r.correo().trim())
                .filter(Usuario::isActivo)
                .filter(x -> encoder.matches(r.password(), x.getPasswordHash()))
                // Mensaje genérico: no revela si falló el correo o la contraseña
                .orElseThrow(() -> new NegocioException(HttpStatus.UNAUTHORIZED, CREDENCIALES_INVALIDAS));
        return new AuthResponse(jwt.generar(u), u.getId(), u.getNombre(), u.getCorreo(), u.getRol().name());
    }
}
