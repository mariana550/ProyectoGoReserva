package co.edu.ucaldas.goreserva.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String correo,
                           @NotBlank String password) {
}
