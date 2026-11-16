package co.edu.ucaldas.goreserva.config;

import co.edu.ucaldas.goreserva.model.Rol;
import co.edu.ucaldas.goreserva.model.Usuario;
import co.edu.ucaldas.goreserva.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Crea el administrador inicial la primera vez que arranca la aplicación (HU-08). */
@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final String correo;
    private final String password;

    public DataInitializer(UsuarioRepository usuarios, PasswordEncoder encoder,
                           @Value("${app.admin.correo}") String correo,
                           @Value("${app.admin.password}") String password) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.correo = correo;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (usuarios.existsByCorreoIgnoreCase(correo)) return;
        Usuario admin = new Usuario();
        admin.setNombre("Administrador");
        admin.setCorreo(correo);
        admin.setTelefono("3000000000");
        admin.setPasswordHash(encoder.encode(password));
        admin.setRol(Rol.ADMIN);
        usuarios.save(admin);
        log.info("Administrador inicial creado: {}", correo);
    }
}
