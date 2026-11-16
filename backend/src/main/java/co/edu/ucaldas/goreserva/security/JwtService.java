package co.edu.ucaldas.goreserva.security;

import co.edu.ucaldas.goreserva.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Genera y valida los tokens JWT (HU-07). */
@Service
public class JwtService {
    private final SecretKey clave;
    private final Duration vigencia;

    public JwtService(@Value("${app.jwt.secret}") String secreto,
                      @Value("${app.jwt.expiracion-horas}") long horas) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.vigencia = Duration.ofHours(horas);
    }

    public String generar(Usuario u) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(u.getCorreo())
                .claim("rol", u.getRol().name())
                .claim("id", u.getId())
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + vigencia.toMillis()))
                .signWith(clave)
                .compact();
    }

    /** Devuelve los datos del token, o lanza una excepción si es inválido o expiró. */
    public Claims leer(String token) {
        return Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
    }
}
