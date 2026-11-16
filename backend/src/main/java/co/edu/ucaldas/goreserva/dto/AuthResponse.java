package co.edu.ucaldas.goreserva.dto;

public record AuthResponse(String token,
                           Long id,
                           String nombre,
                           String correo,
                           String rol)
{
}
