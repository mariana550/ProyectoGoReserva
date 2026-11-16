package co.edu.ucaldas.goreserva.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import co.edu.ucaldas.goreserva.dto.LoginRequest;
import co.edu.ucaldas.goreserva.dto.RegistroRequest;
import co.edu.ucaldas.goreserva.exception.NegocioException;
import co.edu.ucaldas.goreserva.model.Rol;
import co.edu.ucaldas.goreserva.model.Usuario;
import co.edu.ucaldas.goreserva.repository.UsuarioRepository;
import co.edu.ucaldas.goreserva.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class AuthServiceTest {
    private UsuarioRepository repo;
    private BCryptPasswordEncoder encoder;
    private AuthService service;

    @BeforeEach
    void setUp() {
        repo = mock(UsuarioRepository.class);
        encoder = new BCryptPasswordEncoder();
        JwtService jwt = new JwtService("clave-de-pruebas-de-al-menos-32-caracteres!!", 8);
        service = new AuthService(repo, encoder, jwt);
        when(repo.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void registroGuardaClienteConPasswordCifrada() {
        Usuario u = service.registrar(new RegistroRequest("Laura", "Laura@Correo.com", "3001234567", "secreta123"), Rol.CLIENTE);
        assertEquals(Rol.CLIENTE, u.getRol());
        assertEquals("laura@correo.com", u.getCorreo());
        assertNotEquals("secreta123", u.getPasswordHash());
        assertTrue(encoder.matches("secreta123", u.getPasswordHash()));
    }

    @Test
    void registroRechazaCorreoRepetido() {
        when(repo.existsByCorreoIgnoreCase("laura@correo.com")).thenReturn(true);
        NegocioException e = assertThrows(NegocioException.class, () ->
                service.registrar(new RegistroRequest("Laura", "laura@correo.com", "300", "secreta123"), Rol.CLIENTE));
        assertEquals("Ya existe una cuenta con este correo", e.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    void loginConPasswordIncorrectaDaMensajeGenerico() {
        Usuario u = new Usuario();
        u.setCorreo("laura@correo.com");
        u.setPasswordHash(encoder.encode("secreta123"));
        when(repo.findByCorreoIgnoreCase("laura@correo.com")).thenReturn(Optional.of(u));
        NegocioException e = assertThrows(NegocioException.class, () ->
                service.login(new LoginRequest("laura@correo.com", "otra-clave")));
        assertEquals(AuthService.CREDENCIALES_INVALIDAS, e.getMessage());
    }

    @Test
    void loginCorrectoDevuelveToken() {
        Usuario u = new Usuario();
        u.setNombre("Laura");
        u.setCorreo("laura@correo.com");
        u.setPasswordHash(encoder.encode("secreta123"));
        when(repo.findByCorreoIgnoreCase("laura@correo.com")).thenReturn(Optional.of(u));
        var r = service.login(new LoginRequest("laura@correo.com", "secreta123"));
        assertNotNull(r.token());
        assertEquals("CLIENTE", r.rol());
    }
}
